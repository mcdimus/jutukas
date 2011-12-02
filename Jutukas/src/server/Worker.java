package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import client.MainWindow;

/**
 * This class represents the thread what is created by <code>Server</code>.
 * 
 * @author nail
 */
public class Worker implements Runnable {

	/**
	 * Connection is redirected by <code>Server</code> to this
	 * <code>Socket</code>.
	 */
	private Socket socket;
	/**
	 * <code>Socket</code>'s output stream.
	 */
	private OutputStreamWriter out;
	/**
	 * <code>Socket</code>'s input stream.
	 */
	private BufferedReader in;
	/**
	 * Helps splitting the lines.
	 */
	private StringTokenizer strtok;
	/**
	 * Holds values of <code>GET</code> request parameters after they have been
	 * obtained by <code>StringTokenizer</code>.
	 */
	private String[] parametersValues;
	/**
	 * Map of known hosts (names&IPs) what are being held in the file.
	 */
	private HashMap<String, String> knownHosts;
	/**
	 * Create new Worker thread bound to this <code>Socket</code>.
	 * 
	 * @param s
	 *            - <code>Socket</code> to bound to
	 */
	public Worker(Socket s) {
		socket = s;
		new Thread(this).start();
	}

	/**
	 * Run method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			knownHosts = MainWindow.hostsManager.getMapOfKnownHosts();
			out = new OutputStreamWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			strtok = new StringTokenizer(in.readLine(), " ");
			// GET
			strtok.nextToken();
			processPath(strtok.nextToken());
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method begins processing of the received parameters.
	 * 
	 * @param path
	 *            - <code>String</code> to process
	 */
	private void processPath(String path) {
		try {
			strtok = new StringTokenizer(path, "/");
			// chat
			strtok.nextToken();
			// findname, sendname, ...
			String command = strtok.nextToken("?").substring(1);
			// ?param1=value1&param2=value2
			String parameters = URLDecoder.decode(strtok.nextToken(" ")
					.substring(1), "UTF-8");
			System.out.println(parameters);
			getParametersValues(parameters);
			if (command.equals("findname")) {
				findName();
			} else if (command.equals("sendname")) {
				acceptAndSendName();
			} else if (command.equals("asknames")) {
				String log = "ASKNAMES response\n";
				try {
					out.write(MainWindow.hostsManager.getJsonString());
					Server.print(log
							+ socket.getInetAddress().toString().substring(1)
							+ ": OK");
				} catch (IOException ioe2) {
					Server.print(log
							+ socket.getInetAddress().toString().substring(1)
							+ ": host unreachable");
				}
			} else if (command.equals("sendmessage")) {
				System.out.println("I shall accept the message");
				acceptMessage();
			} else {
				throw new Exception();
			}
			out.write("HTTP/1.1 200 OK\r\n");
		} catch (Exception e) {
			Server.print(socket.getInetAddress().toString().substring(1)
					+ ": wrong URL path: " + path);
			try {
				out.write("HTTP/1.1 400 Bad Request\r\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Processes FINDNAME request, creates&sends appropriate response.
	 */
	private void findName() {
		String askedName = parametersValues[0], destIP = parametersValues[1], log = "";
		int ttl = Integer.parseInt(parametersValues[2]);
		ttl--;
		if (knownHosts.containsKey(askedName)) {
			String addr = String.format(
					"http://%s/chat/sendname?name=%s&ip=%s&" + "ttl=%s",
					destIP, askedName, knownHosts.get(askedName), ttl);
			log += "FINDNAME(name found) response\n";
			try {
				URL url = new URL(addr);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
				log += addr + ": OK";
			} catch (IOException e) {
				log += addr + ": host unreachable";
				System.exit(0);
			}
		} else {
			if (ttl != 0) {
				log += "FINDNAME(broadcast) response\n";
				for (String value : knownHosts.values()) {
					String addr = String
							.format("http://%s/chat/findname?name=%s&ip=%s&"
									+ "ttl=%s", value, askedName, destIP, ttl);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(urlcon.getInputStream()));
						br.close();
						log += addr + ": OK";
					} catch (IOException e) {
						log += addr + ": host unreachable";
						continue;
					}
				}
			}
		}
		Server.print(log);
	}

	/**
	 * Processes SENDNAME request, creates&sends appropriate response.
	 */
	private void acceptAndSendName() {
		String sentName = parametersValues[0], sentIP = parametersValues[1], log = "";
		int ttl = Integer.parseInt(parametersValues[2]);
		ttl--;
		if (!knownHosts.containsKey(sentName)) {
			if (ttl != 0) {
				log += "SENDNAME response\n";
				for (String value : knownHosts.values()) {
					if (!value.equals(Server.IP + ":" + Server.PORT)) {
						String addr = String.format(
								"http://%s/chat/sendname?name=%s&ip=%s&"
										+ "ttl=%s", value, sentName, sentIP,
								ttl);
						try {
							URL url = new URL(addr);
							URLConnection urlcon = url.openConnection();
							BufferedReader br = new BufferedReader(
									new InputStreamReader(
											urlcon.getInputStream()));
							br.close();
							log += addr + ": OK";
						} catch (IOException e) {
							log += addr + ": host unreachable";
							continue;
						}
					}
				}
				// TODO: put new key(name) & value(ip) to the knownHosts Map.
			}
		}
		Server.print(log);
	}

	/**
	 * Processes MESSAGE request, sends received message to GUI.
	 */
	private void acceptMessage() {
		// TODO: update chat window
		// should be connected with the GUI
	}

	/**
	 * Method completes processing of the received parameters.
	 * 
	 * @param data
	 *            - <code>String</code> to process
	 */
	private void getParametersValues(String data) {
		parametersValues = new String[4];
		StringTokenizer strtok = new StringTokenizer(data, "&");
		// name
		parametersValues[0] = getValue(strtok.nextToken());
		// ip
		parametersValues[1] = getValue(strtok.nextToken());
		// message OR TTL
		parametersValues[2] = getValue(strtok.nextToken());
		// TTL OR null
		try {
			parametersValues[3] = getValue(strtok.nextToken());
		} catch (NoSuchElementException e) {
			parametersValues[3] = null;
		}
	}

	/**
	 * Returns the value of a given parameter.
	 * 
	 * @param in
	 *            - <code>String</code> to process
	 * @return <code>String</code> parameter's value
	 */
	private String getValue(String in) {
		String[] words = in.split("=");
		return words[1];
	}
}

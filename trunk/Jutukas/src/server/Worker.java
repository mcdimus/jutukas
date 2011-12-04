package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import client.ChatWindow;
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
			if (command.equals("findname")) {
				getParametersValues(parameters, 3);
				findName();
			} else if (command.equals("sendname")) {
				getParametersValues(parameters, 3);
				acceptAndSendName();
			} else if (command.equals("asknames")) {
				getParametersValues(parameters, 1);
				Server.print("ASKNAMES response");
				try {
					out.write(MainWindow.hostsManager.getJsonString() + "\r\n");
					out.flush();
					Server.print(socket.getInetAddress().toString()
							.substring(1)
							+ ": OK");
				} catch (IOException ioe2) {
					Server.print(socket.getInetAddress().toString()
							.substring(1)
							+ ": host unreachable");
				}
			} else if (command.equals("sendmessage")) {
				getParametersValues(parameters, 4);
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
				Server.print(socket.getInetAddress().toString().substring(1)
						+ ": host unreachable");
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Processes FINDNAME request, creates&sends appropriate response.
	 */
	private void findName() {
		String askedName = parametersValues[0], destIP = parametersValues[1];
		int ttl = Integer.parseInt(parametersValues[2]);
		ttl--;
		if (knownHosts.containsKey(askedName)) {
			String addr = String.format(
					"http://%s/chat/sendname?name=%s&ip=%s&" + "ttl=%s",
					destIP, askedName, knownHosts.get(askedName), ttl);
			Server.print("FINDNAME(name found) response\n");
			try {
				URL url = new URL(addr);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
				Server.print(addr + ": OK");
			} catch (IOException e) {
				Server.print(addr + ": host unreachable");
				System.exit(0);
			}
		} else {
			if (ttl != 0) {
				Server.print("FINDNAME(broadcast) response\n");
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
						Server.print(addr + ": OK");
					} catch (IOException e) {
						Server.print(addr + ": host unreachable");
						continue;
					}
				}
			}
		}
	}

	/**
	 * Processes SENDNAME request, creates&sends appropriate response.
	 */
	private void acceptAndSendName() {
		String sentName = parametersValues[0], sentIP = parametersValues[1];
		int ttl = Integer.parseInt(parametersValues[2]);
		ttl--;
		if (!knownHosts.containsKey(sentName)) {
			// TODO: give new name & ip to the KnowHostsManager
		} else {
			// TODO: update ip value on given name
			// drop the name from map.. if the part below is valid.
		}
		// TODO: vot see part ???
		if (ttl != 0) {
			Server.print("SENDNAME response\n");
			for (String value : knownHosts.values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String
							.format("http://%s/chat/sendname?name=%s&ip=%s&"
									+ "ttl=%s", value, sentName, sentIP, ttl);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(urlcon.getInputStream()));
						br.close();
						Server.print(addr + ": OK");
					} catch (IOException e) {
						Server.print(addr + ": host unreachable");
						continue;
					}
				}
			}
		}
	}

	/**
	 * Processes MESSAGE request, sends received message to GUI.
	 */
	private void acceptMessage() {
		String sentName = parametersValues[0], sentIP = parametersValues[1], message = parametersValues[2];
		if (MainWindow.chatWindows.containsKey(sentName)) {
			if(!MainWindow.chatWindows.get(sentName).isVisible()) {
				MainWindow.chatWindows.get(sentName).setVisible(true);
			}
			MainWindow.chatWindows.get(sentName).appendText(sentName, message, "blue");
		} else {
			ChatWindow chat = new ChatWindow(sentName, sentIP);
			chat.appendText(sentName, message, "blue");
			MainWindow.chatWindows.put(sentName, chat);
			chat.setVisible(true);
		}
		Server.print("MESSAGE response\n" + sentIP + ": OK\n");
		if (!knownHosts.containsKey(sentName)) {
			MainWindow.hostsManager.suzanna(sentName, sentIP);
		}
	}

	/**
	 * Method completes processing of the received parameters.
	 * 
	 * @param data
	 *            - <code>String</code> to process
	 */
	private void getParametersValues(String data, int times) {
		parametersValues = new String[times];
		StringTokenizer strtok = new StringTokenizer(data, "&");
		for (int i = 0; i < times; i++) {
			String[] words = strtok.nextToken().split("=");
			parametersValues[i] = words[1];
		}
	}
}

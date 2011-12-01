package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Worker implements Runnable {

	private Socket socket;
	private StringTokenizer strtok;
	private OutputStreamWriter out;
	private BufferedReader in;
	private String[] parametersValues;

	public Worker(Socket s) {
		socket = s;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
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
			// getParametersValues(parameters);
			if (command.equals("findname")) {
//				findName();
			} else if (command.equals("sendname")) {
//				acceptAndSendName();
			} else if (command.equals("asknames")) {
				out.write(Server.knownHosts.getJsonString());
				Server.print("ASKNAMES response(JSON sent)");
			} else if (command.equals("sendmessage")) {
				System.out.println("I shall accept the message");
				acceptMessage();
			} else {
				throw new Exception();
			}
			out.write("HTTP/1.1 200 OK\r\n");
		} catch (Exception e) {
			Server.print("Wrong URL path: " + path + " from " + socket.getInetAddress());
			try {
				out.write("HTTP/1.1 400 Bad Request\r\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void findName() {
		String askedName = parametersValues[0], destIP = parametersValues[1];
		int TTL = Integer.parseInt(parametersValues[2]);
		TTL--;
		if (Server.knownHosts.getMapOfKnownHosts().containsKey(askedName)) {
			String addr = String
					.format("http://%s/chat/sendname?name=%s&ip=%s&" + "ttl=%s",
							destIP, askedName, Server.knownHosts.getMapOfKnownHosts()
									.get(askedName), TTL);
			Server.print("FINDNAME response(name found): " + addr);
			new Sender(addr, Sender.WORKER);
		} else {
			if (TTL != 0) {
				Server.print("FINDNAME response(broadcast): ");
				for (String value: Server.knownHosts.getMapOfKnownHosts().values()) {
					String addr = String.format("http://%s/chat/findname?name=%s&ip=%s&"
							+ "ttl=%s", value, askedName, destIP, TTL);
					new Sender(addr, Sender.WORKER);
					Server.print(addr);
				}
			}
		}
	}
	
	private void acceptAndSendName() {
		String sentName = parametersValues[0], sentIP = parametersValues[1];
		int TTL = Integer.parseInt(parametersValues[2]);
		TTL--;
		if (!Server.knownHosts.getMapOfKnownHosts().containsKey(sentName)) {
			if (TTL != 0) {
				Server.print("SENDNAME response(broadcast): ");
				for (String value: Server.knownHosts.getMapOfKnownHosts().values()) {
					if (!value.equals(Server.IP + ":" + Server.PORT)) {
						String addr = String.format("http://%s/chat/sendname?name=%s&ip=%s&"
								+ "ttl=%s", value, sentName, sentIP, TTL);
						new Sender(addr, Sender.WORKER);
						Server.print(addr);
					}
				}
				// put new key(name) & value(ip) to the knownHosts Map.
			}
		}
	}

	private void acceptMessage() {
		// should be connected with the GUI
		// update chat window
	}

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

	private String getValue(String in) {
		String[] words = in.split("=");
		return words[1];
	}
}

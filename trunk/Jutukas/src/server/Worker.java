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
				// findName();
			} else if (command.equals("sendname")) {
				System.out.println("I shall accept the name");
				acceptName();
			} else if (command.equals("asknames")) {
				System.out.println("I shall send the file");
				out.write(Server.knownHosts.getJsonString());
			} else if (command.equals("sendmessage")) {
				System.out.println("I shall accept the message");
				acceptMessage();
			} else {
				throw new Exception();
			}
			out.write("HTTP/1.1 200 OK\r\n");
		} catch (Exception e) {
			Server.print(new Date() + " --- " + socket.getInetAddress()
					+ ": Wrong URL path: " + path);
			try {
				out.write("HTTP/1.1 400 Bad Request\r\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void findName() {
		String name = parametersValues[0], ip = parametersValues[1];
		int TTL = Integer.parseInt(parametersValues[2]);
		TTL--;
		if (Server.knownHosts.getMapOfKnownHosts().containsKey(name)) {
			String addr = String
					.format("http://%s/chat/sendname?name=%s&ip=%s&" + "ttl=%s",
							ip, name, Server.knownHosts.getMapOfKnownHosts()
									.get(name), TTL);
			try {
				URL url = new URL(addr);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				System.out.println(br.readLine());
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (TTL != 0) {
				for (String value: Server.knownHosts.getMapOfKnownHosts().values()) {
					String addr = String.format("http://%s/chat/findname?name=%s&ip=%s&"
							+ "ttl=%s", value, name, ip, TTL);
					new Sender(addr, 1);
				}
			}
		}
	}

	private void acceptName() {
		// accept the name sent with /sendname? command
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

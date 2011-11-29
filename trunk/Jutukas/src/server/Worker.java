package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Worker implements Runnable {

	private Socket socket;
	private StringTokenizer strtok;
	private OutputStreamWriter osw;
	private BufferedReader br;
	private String[] parametersValues;

	public Worker(Socket s) {
		socket = s;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			osw = new OutputStreamWriter(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			strtok = new StringTokenizer(br.readLine(), " ");
			// GET
			strtok.nextToken();
			processPath(strtok.nextToken());
			osw.close();
			br.close();
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
			String parameters = strtok.nextToken(" ").substring(1);
			getParametersValues(parameters);
			if (command.equals("findname")) {
				System.out.println("I shall find the name");
				findName();
			} else if (command.equals("sendname")) {
				System.out.println("I shall accept the name");
				acceptName();
			} else if (command.equals("asknames")) {
				System.out.println("I shall send the file");
				sendFile();
			} else if (command.equals("sendmessage")) {
				System.out.println("I shall accept the message");
				acceptMessage();
			} else {
				throw new Exception();
			}
			osw.write("HTTP/1.1 200 OK\r\n");
		} catch (Exception e) {
			Server.print(new Date() + " --- " + socket.getInetAddress()
					+ ": Wrong URL path: " + path);
			try {
				osw.write("HTTP/1.1 400 Bad Request\r\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void findName() {
		String ip = parametersValues[1];
		String addr = String.format("http://%s/chat/sendname?name=%s&ip=%s&"
				+ "ttl=%s", ip, "name_here", "ip_here", "ttl_here");
		try {
			URL url = new URL(addr);
			URLConnection urlcon = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlcon.getInputStream()));
			System.out.println(br.readLine());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void acceptName() {
		// accept the name sent with /sendname? command
	}
	
	private void sendFile() {
		// synchronized with the other peer
		// send JSON file
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

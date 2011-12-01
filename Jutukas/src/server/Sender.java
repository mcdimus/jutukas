package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Sender implements Runnable {

	final static int WORKER = 0, USENDMESSAGE = 1, UASKNAMES = 2;
	private String address;
	private int action;
	private final int TTL = 1;

	public Sender() {
		// user's asknames request
		action = UASKNAMES;
		new Thread(this).start();
	}

	public Sender(String name) {
		// user's findname request to the neighbours
		Server.print("FINDNAME request(broadcast): ");
		for (String value : Server.knownHosts.getMapOfKnownHosts().values()) {
			if (!value.equals(Server.IP + ":" + Server.PORT)) {
				String addr = String.format("http://%s/chat/findname?name=%s"
						+ "&ip=%s&ttl=%d", value, name, Server.IP + ":"
						+ Server.PORT, TTL);
				new Sender(addr, Sender.WORKER);
				Server.print(addr);
			}
		}
	}

	public Sender(String addr, int act) {
		// user's findname & somebody's findname OR sendname request
		address = addr;
		action = act;
		new Thread(this).start();
	}

	public Sender(String name, String ip, String message) {
		// user's message request
		address = String.format("http://%s/chat/sendmessage?name=%s&ip=%s"
				+ "&message=%s&ttl=%d", ip, name,
				Server.IP + ":" + Server.PORT, message, TTL);
		action = USENDMESSAGE;
		new Thread(this).start();
	}

	@Override
	public void run() {
		switch (action) {
		case WORKER:
			try {
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
			} catch (IOException e) {
				System.exit(0);
			}
			break;
		case USENDMESSAGE:
			Server.print("MESSAGE request: " + address);
			try {
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
			} catch (IOException e) {
				Server.print("MESSAGE request failed: " + address );
				// send to GUI - "message was not delivered"
			}	
			break;
		case UASKNAMES:
			Server.print("ASKNAMES request(synchronized stream): ");
			for (String value : Server.knownHosts.getMapOfKnownHosts().values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					try {
						String addr = String.format("http://%s/chat/asknames?"
								+"ttl=1", value);
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(new InputStreamReader(
								urlcon.getInputStream()));
						String jsonhosts = br.readLine();
						Server.knownHosts.addNewHosts(jsonhosts);
						br.close();
						Server.print(value);
					} catch (IOException e) {
						Server.print("ASKNAMES request failed: " + address);
						// send to GUI - "cannot get names from a host"
						continue;
					}
				}
			}
			break;
		}
	}

}

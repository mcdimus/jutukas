package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Sender implements Runnable {

	public final static int FINDNAME = 0, SENDMESSAGE = 1, SENDNAME = 2,
			ASKNAMES = 3;
	private String name;
	private String address;
	private int action;
	private final int TTL = 1;

	public Sender() {
		// user's asknames request
		action = ASKNAMES;
		new Thread(this).start();
	}

	public Sender(String nameToFind, int actionConstant) {
		// user's findname & sendname request
		name = nameToFind;
		action = actionConstant;
		new Thread(this).start();
	}

	public Sender(String name, String ip, String message) {
		// user's message request
		address = String.format("http://%s/chat/sendmessage?name=%s&ip=%s"
				+ "&message=%s&ttl=%d", ip, name,
				Server.IP + ":" + Server.PORT, message, TTL);
		action = SENDMESSAGE;
		new Thread(this).start();
	}

	@Override
	public void run() {
		switch (action) {
		case FINDNAME:
			Server.print("FINDNAME request: ");
			for (String value : Server.knownHosts.getMapOfKnownHosts().values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/findname?name=%s"
							+ "&ip=%s&ttl=%d", value, name, Server.IP + ":"
							+ Server.PORT, TTL);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(new InputStreamReader(
								urlcon.getInputStream()));
						br.close();
						Server.print(addr + ": OK");
					} catch (IOException e) {
						Server.print(addr + ": host unreachable");
						continue;
					}
					Server.print(addr);
				}
			}
			break;
		case SENDMESSAGE:
			Server.print("MESSAGE request: " + address);
			try {
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
				Server.print(address + ": OK");
			} catch (IOException e) {
				Server.print(address + ": host unreachable");
				// send to GUI - "message was not delivered"
			}	
			break;
		case SENDNAME:
			Server.print("SENDNAME request: ");
			for (String value : Server.knownHosts.getMapOfKnownHosts().values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/sendname?name=%s"
							+ "&ip=%s&ttl=%d", value, name, Server.IP + ":"
							+ Server.PORT, TTL);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(new InputStreamReader(
								urlcon.getInputStream()));
						br.close();
					} catch (IOException e) {
						Server.print(addr + ": host unreachable");
						continue;
					}
					Server.print(addr + ": OK");
				}
			}
			break;
		case ASKNAMES:
			Server.print("ASKNAMES request: ");
			for (String value : Server.knownHosts.getMapOfKnownHosts().values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/asknames?"
							+"ttl=1", value);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(new InputStreamReader(
								urlcon.getInputStream()));
						String jsonhosts = br.readLine();
						Server.knownHosts.addNewHosts(jsonhosts);
						br.close();
						Server.print(addr + ": OK");
					} catch (IOException e) {
						Server.print(addr + ": host unreachable");
						// send to GUI - "cannot get names from a host"
						continue;
					}
				}
			}
			break;
		}
	}

}

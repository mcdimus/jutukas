package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import client.MainWindow;

/**
 * This class represents the thread what should complete the task received from
 * the GUI. The tasks are: find the user's IP by his name, ask from your
 * 'friend' hosts their hosts file, send a message, send other hosts your new
 * name.
 * 
 * @author nail
 */
public class Sender implements Runnable {

	/**
	 * Constant integer values representing actions visible to GUI.
	 */
	public static final int FINDNAME = 0, SENDMESSAGE = 1;
	/**
	 * Constant integer values representing actions invisible to GUI.
	 */
	private static final int SENDNAME = 2, ASKNAMES = 3;
	/**
	 * Map of known hosts (names&IPs) what are being held in the file.
	 */
	private HashMap<String, String> knownHosts;
	/**
	 * Name to find / name to send.
	 */
	private String name;
	/**
	 * Used in SENDMESSAGE action as the URL to send message to.
	 */
	private String address;
	/**
	 * Thread's action (uses described constants).
	 */
	private int action;
	/**
	 * Request's time to live.
	 */
	private static final int TTL = 1;

	/**
	 * Use this constructor to create new ASKNAMES request.
	 */
	public Sender(String nameToFind) {
		action = ASKNAMES;
		new Thread(this).start();
	}

	/**
	 * Use this constructor to create new FINDNAME or SENDNAME request.
	 * 
	 * @param nameHere
	 *            - name to find / to send
	 * @param actionConstant
	 *            - FINDNAME or SENDNAME request (use
	 *            <code>Sender.FINDNAME</code> or <code>Sender.SENDNAME</code>)
	 */
	public Sender(String nameHere, int actionConstant) {
		name = nameHere;
		action = actionConstant;
		new Thread(this).start();
	}

	/**
	 * Use this constructor to create new MESSAGE request (or, more simply, to
	 * send a message).
	 * 
	 * @param name
	 *            - host name to send to
	 * @param ip
	 *            - host IP to send to
	 * @param message
	 *            - message to send
	 */
	public Sender(String name, String ip, String message) {
		address = String.format("http://%s/chat/sendmessage?name=%s&ip=%s"
				+ "&message=%s&ttl=%d", ip, name,
				Server.IP + ":" + Server.PORT, message, TTL);
		action = SENDMESSAGE;
		new Thread(this).start();
	}

	/**
	 * Run method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		knownHosts = MainWindow.hostsManager.getMapOfKnownHosts();
		switch (action) {
		case FINDNAME:
			Server.print("FINDNAME request\n");
			for (String value : knownHosts.values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/findname?"
							+ "name=%s&ip=%s&ttl=%d", value, name, Server.IP
							+ ":" + Server.PORT, TTL);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(urlcon.getInputStream()));
						br.close();
						Server.print(address + ": OK");
					} catch (NoRouteToHostException e) {
						Server.print(addr + ": host unreachable\n");
						continue;
					} catch (IOException e) {
						Server.print(addr + ": " + e.getMessage());
						continue;
					}
					Server.print(addr);
				}
			}
			break;
		case SENDMESSAGE:
			Server.print("MESSAGE request\n");
			try {
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
				Server.print(address + ": OK");
			} catch (NoRouteToHostException e) {
				Server.print(address + ": host unreachable\n");
				// TODO: send to GUI - message was not delivered - user is offline
			} catch (IOException e) {
				Server.print(address + ": " + e.getMessage());
				// TODO: send to GUI - message was not delivered - end host refuses it

			}
			break;
		case SENDNAME:
			Server.print("SENDNAME request:\n");
			for (String value : knownHosts.values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/sendname?"
							+ "name=%s&ip=%s&ttl=%d", value, name, Server.IP
							+ ":" + Server.PORT, TTL);
					try {
						URL url = new URL(addr);
						URLConnection urlcon = url.openConnection();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(urlcon.getInputStream()));
						br.close();
						Server.print(addr + ": OK");
					} catch (NoRouteToHostException e) {
						Server.print(addr + ": host unreachable\n");
						continue;
					} catch (IOException e) {
						Server.print(addr + ": " + e.getMessage());
						continue;
					}
				}
			}
			break;
		case ASKNAMES:
			Server.print("ASKNAMES request\n");
			if (knownHosts.containsKey(name)) {
				// TODO: Send to GUI found name
			} else {
				for (String value : knownHosts.values()) {
					if (!value.equals(Server.IP + ":" + Server.PORT)) {
						String addr = String.format("http://%s/chat/asknames?"
								+ "ttl=1", value);
						try {
							URL url = new URL(addr);
							URLConnection urlcon = url.openConnection();
							BufferedReader br = new BufferedReader(
									new InputStreamReader(urlcon.getInputStream()));
							String jsonhosts = br.readLine();
							System.out.println(jsonhosts);
							MainWindow.hostsManager.addNewHosts(jsonhosts);
							br.close();
							Server.print(addr + ": OK\n");
						} catch (NoRouteToHostException e) {
							Server.print(addr + ": host unreachable\n");
							continue;
						} catch (IOException e) {
							Server.print(addr + ": " + e.getMessage());
							continue;
						}
					}
				}
			}
			break;
		}
		// MainWindow.addKnownUsers();
	}

}

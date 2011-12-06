package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedHashMap;

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
	public static final int FINDNAME = 0, SENDNAME = 2;
	/**
	 * Constant integer values representing actions invisible to GUI.
	 */
	private static final int  SENDMESSAGE = 1, ASKNAMES = 3;
	/**
	 * Map of known hosts (names&IPs) what are being held in the file.
	 */
	private LinkedHashMap<String, String> knownHosts;
	/**
	 * Name to find / name to send.
	 */
	private String name;
	/**
	 * Name to find / name to send after URL encoding.
	 */
	private String encodedName;
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
	private int ttl = 3;
	/**
	 * Link to the GUI.
	 */
	private MainWindow mainWindow;

	/**
	 * Use this constructor to create new ASKNAMES request.
	 * 
	 * @param nameToFind
	 *            - name which should be found
	 * @param parent
	 *            - link to the GUI to display search results
	 */
	public Sender(String nameToFind, MainWindow parent) {
		mainWindow = parent;
		action = ASKNAMES;
		name = nameToFind;
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
		encodedName = encodeString(nameHere);
		new Thread(this).start();
	}

	/**
	 * Use this constructor to create new MESSAGE request (or, more simply, to
	 * send a message).
	 * 
	 * @param hostName
	 *            - host name to send to
	 * @param ip
	 *            - host IP to send to
	 * @param message
	 *            - message to send
	 */
	public Sender(String hostName, String ip, String message) {
		name = hostName;
		encodedName = encodeString(Server.NAME);
		message = encodeString(message);
		address = String.format("http://%s/chat/sendmessage?name=%s&ip=%s"
				+ "&message=%s&ttl=%d", ip, encodedName, Server.IP + ":"
				+ Server.PORT, message, ttl);
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
		knownHosts.remove(Server.NAME);
		switch (action) {
		case FINDNAME:
			Server.print("FINDNAME request");
			for (String value : knownHosts.values()) {
				String addr = String.format("http://%s/chat/findname?name=%s"
						+ "&ip=%s&ttl=%d", value, name, Server.IP + ":"
						+ Server.PORT, ttl);
				try {
					URL url = new URL(addr);
					URLConnection urlcon = url.openConnection();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(urlcon.getInputStream()));
					br.close();
					Server.print(value + ": OK");
				} catch (IOException e) {
					Server.print(value + ": " + e.getMessage());
					continue;
				}
				Server.print(addr);
			}
			break;
		case SENDMESSAGE:
			try {
				Server.print("MESSAGE request");
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				br.close();
				Server.print("OK");
			} catch (IOException e) {
				Server.print(e.getMessage());
				MainWindow.chatWindows.get(name).appendText("Error",
								"Failed to deliver the message to " + name
								+ ".", "red");
			}
			break;
//		case SENDNAME:
//			Server.print("SENDNAME request:");
//			for (String value : knownHosts.values()) {
//				String addr = String.format("http://%s/chat/sendname?name=%s"
//						+ "&ip=%s&ttl=%d", value, Server.NAME, Server.IP + ":"
//						+ Server.PORT, ttl);
//				try {
//					URL url = new URL(addr);
//					URLConnection urlcon = url.openConnection();
//					BufferedReader br = new BufferedReader(
//							new InputStreamReader(urlcon.getInputStream()));
//					br.close();
//					Server.print(value + ": OK");
//				} catch (IOException e) {
//					Server.print(value + ": " + e.getMessage());
//					continue;
//				}
//			}
//			break;
		case ASKNAMES:
			HashSet<String> visitedHosts = new HashSet<String>();
			visitedHosts.add(Server.IP + ":" + Server.PORT);
			boolean found = false;
			if (knownHosts.containsKey(name)) {
				mainWindow.userFound(name, knownHosts.get(name));
				found = true;
			} else {
				Server.print("ASKNAMES request");
				while (ttl != 0) {
					ttl--;
					for (String value : knownHosts.values()) {
						if (visitedHosts.add(value)) {
							String addr = String.format("http://%s/chat/"
									+ "asknames?ttl=1", value);
							try {
								URL url = new URL(addr);
								URLConnection urlcon = url.openConnection();
								BufferedReader br = new BufferedReader(
										new InputStreamReader(
												urlcon.getInputStream()));
								String jsonhosts = br.readLine();
								MainWindow.hostsManager.addNewHosts(jsonhosts);
								br.close();
								Server.print(value + ": OK");
							} catch (IOException e) {
								Server.print(value + ": " + e.getMessage());
								continue;
							}
						}
					}
					knownHosts = MainWindow.hostsManager.getMapOfKnownHosts();
					if (knownHosts.containsKey(name)) {
						mainWindow.userFound(name, knownHosts.get(name));
						found = true;
						break;
					}
				}
			}
			if (!found) {
				mainWindow.userNotFound(name);
				Server.print("ASKNAMES stop: TTL = 0");
			}
			break;
		}
	}

	/**
	 * Encodes the <code>String</code> into the URL format (for example,
	 * whitespace = '+' OR '%20").
	 * 
	 * @param in
	 *            - <code>String</code> to encode.
	 * @return encoded <code>String</code>
	 */
	private String encodeString(String in) {
		String answer = null;
		try {
			answer = URLEncoder.encode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return answer;
	}
}

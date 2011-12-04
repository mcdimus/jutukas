package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
	public static final int FINDNAME = 0, SENDMESSAGE = 1;
	/**
	 * Constant integer values representing actions invisible to GUI.
	 */
	private static final int SENDNAME = 2, ASKNAMES = 3;
	/**
	 * Map of known hosts (names&IPs) what are being held in the file.
	 */
	private LinkedHashMap<String, String> knownHosts;
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
	private int ttl = 1;
	private MainWindow mainWindow;
	
	private Sender(String n) {
		try {
			name = URLEncoder.encode(n, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
	}
	
	/**
	 * Use this constructor to create new ASKNAMES request.
	 */
	public Sender(String nameToFind, MainWindow parent) {
		this(nameToFind);
		mainWindow = parent;
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
		this(nameHere);
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
	public Sender(String hostName, String ip, String message) {
		this(Server.NAME);
		name = hostName;
		address = String.format("http://%s/chat/sendmessage?name=%s&ip=%s"
				+ "&message=%s&ttl=%d", ip, name,
				Server.IP + ":" + Server.PORT, message, ttl);
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
							+ ":" + Server.PORT, ttl);
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
			} catch (IOException e) {
				Server.print(address + ": " + e.getMessage());
				MainWindow.chatWindows.get(name).appendText("<html><b>" 
						+ name + "</b>[" + new SimpleDateFormat
						("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").getCalendar()
						.getTime() + "]: Failed to deliver the message"
						+ "</html>");
			}
			break;
		case SENDNAME:
			Server.print("SENDNAME request:\n");
			for (String value : knownHosts.values()) {
				if (!value.equals(Server.IP + ":" + Server.PORT)) {
					String addr = String.format("http://%s/chat/sendname?"
							+ "name=%s&ip=%s&ttl=%d", value, name, Server.IP
							+ ":" + Server.PORT, ttl);
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
				}
			}
			break;
		case ASKNAMES:
			HashSet<String> visitedHosts = new HashSet<String>();
			visitedHosts.add(Server.IP + ":" + Server.PORT);
			boolean found = false;
			if (knownHosts.containsKey(name)) {
				mainWindow.userFound(name, knownHosts.get(name));
				found = true;
			} else {
				Server.print("ASKNAMES request\n");
				while(ttl != 0) {
					ttl--;
					for (String value : knownHosts.values()) {
						if (visitedHosts.add(value)) {
							String addr = String.format("http://%s/chat/"
									+ "asknames?ttl=1", value);
							try {
								URL url = new URL(addr);
								URLConnection urlcon = url.openConnection();
								BufferedReader br = new BufferedReader(
										new InputStreamReader(urlcon.getInputStream()));
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
			}
			break;
		}
	}

}

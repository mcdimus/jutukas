package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import javax.swing.JOptionPane;

import client.MainWindow;

/**
 * Main <code>Server Thread</code>.
 * 
 * @author nail
 */
public class Server implements Runnable {

	/**
	 * Server (this host's) name.
	 */
	public static String NAME;
	/**
	 * Server IP address.
	 */
	public static String IP;
	/**
	 * Server port number.
	 */
	public static int PORT = 6666;
	/**
	 * Socket which this <code>Server</code> is bound to.
	 */
	ServerSocket acceptSocket;
	/**
	 * The file for the server log.
	 */
	private static BufferedWriter file;
	/**
	 * Boolean indicates whether the server works (alive) or not (!alive).
	 */
	private boolean alive;
	/**
	 * Link to the MainWindow.
	 */
	static MainWindow parent;

	/**
	 * Create new <code>Server</code>.
	 * @param name - <code>Server</code>'s name
	 * @param ip
	 *            - <code>Server</code>'s IP
	 * @param port
	 *            - <code>Server</code>'s port
	 */
	public Server(MainWindow window, String hostsName, String ip, String port) {
		NAME = hostsName;
		IP = ip;
		PORT = Integer.parseInt(port);
		parent = window;
		alive = true;
		new Thread(this).start();
	}

	/**
	 * Run method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			acceptSocket = new ServerSocket(PORT);
			Server.print("Server is listening on port " + PORT);
			new Sender(Server.NAME, Sender.FINDNAME);
			while (alive) {
				try {
					Socket s = acceptSocket.accept();
					Server.print("Connection accepted: "
							+ s.getInetAddress().toString().substring(1));
					createWorkerThread(s);
				} catch (SocketException e) {
					Server.print("Server is offline\n");
				}
			}
		} catch (BindException e) {
			System.out.println(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints server messages to the server`s console and server`s log file.
	 * 
	 * @param s
	 *            - string to be written.
	 */
	public static synchronized void print(String s) {
		try {
			file = new BufferedWriter(new FileWriter("server_log.txt", true));
		} catch (IOException e) {
			System.err.println("Unable to create file.");
		}
		try {
			file.write(new Date() + " --- " + s + "\n");
			file.flush();
		} catch (IOException e) {
			System.err.println("Unable to write to the file.");
		}
		System.out.println(s);
	}

	/**
	 * Creates new <code>Worker Thread</code> to process received request.
	 * 
	 * @param s
	 *            - <code>Socket</code> to bind new <code>Worker</code> to
	 */
	private synchronized void createWorkerThread(Socket s) {
		new Worker(s);
	}

	/**
	 * Shuts the server down.
	 */
	public void killServer() {
		this.alive = false;
		try {
			acceptSocket.close();
			try {
				file.close();
			} catch (IOException e) {
				System.err.println("Unable to close server log file.");
			}
		} catch (IOException e) {
			System.err.println("Problem with server closing.");
		}
	}
}

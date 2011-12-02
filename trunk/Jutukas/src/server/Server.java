package server;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import client.KnownHostsManager;
import client.MainWindow;

public class Server implements Runnable {
	
	static MainWindow window;
	public static String IP;
	/**
	 * Constant port number.
	 */
	public static int PORT = 6666;
	/**
	 * The file for the server log.
	 */
	private static BufferedWriter file;
	public static KnownHostsManager knownHosts;
	ServerSocket acceptSocket;
	
	public Server(String ip, String port) {
		IP = ip;
		PORT = Integer.parseInt(port);
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
			Server.print("Server is listening on port "
					+ PORT);
			while (true) {
				Socket s = acceptSocket.accept();
				Server.print("Connection accepted: "
						+ s.getInetAddress());
				createWorkerThread(s);
			}
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
			// TODO: file.flush();
		} catch (IOException e) {
			System.err.println("Unable to write to the file.");
		}
		try {
			file.close();
		} catch (IOException e) {
			System.err.println("Unable to close file.");
		}
		System.out.println(s);
	}

	private synchronized void createWorkerThread(Socket s) {
		new Worker(s);
	}
}

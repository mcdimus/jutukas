package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Sender implements Runnable {
	
	private String theName;
	private String address;
	private String message;
	private String ip;
	// TODO: description here.
	// 0 - worker's findname, 1 - worker's sendname, 2 - worker's asknames, 
	// 3 - user's findname, 4 - user's message, 5 - user's asknames
	// In worker's case do not send request to the user who made the request.
	private int action;
	private int TTL = 1;
	
	public Sender(String name) {
		// user's findname request
		theName = name;
		action = 0;
		new Thread(this).start();
	}

	
	public Sender(String addr, int act) {
		// somebody's findname request
		address = addr;
		action = act;
		new Thread(this).start();
	}
	
	public Sender(String name, String ipAddr) {
		// somebody's sendname request
		// addr & action
		new Thread(this).start();
	}
	
	public Sender(String name, String ipAddr, String messg) {
		// user's message request
		theName = name;
		message = messg;
		ip = ipAddr;
		action = 2;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		if (action == 0) {
			
		} else if (action == 1) {
			try {
				URL url = new URL(address);
				URLConnection urlcon = url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlcon.getInputStream()));
				System.out.println(br.readLine());
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}	
		} else if (action == 2) {
			// send message
		} else {
			// ask names
		}
	}

}

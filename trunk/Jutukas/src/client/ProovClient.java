package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ProovClient {

	public static void main(String[] args) {
		URL url = null;
		try {
			url = new URL("http://127.0.0.1:6666/chat/?findname");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
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
}

package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Manager for the file with known hosts, which is in JSON format. Example:
 * 
 * <pre>
 *   [
 *   ["Tanel Tammet","22.33.44.55:6666"],
 *   ["Peeter Laud","22.33.44.11:6666"]
 *   ]
 * </pre>
 * 
 * 30.11.2011
 * 
 * @author Dmitri Maksimov
 * 
 */
public class KnownHostsManager {

	/**
	 * The contents of the input file.
	 */
	private String jsonStringFromInputFile;
	/**
	 * The instance of the GSON object with pretty printing.
	 */
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Constructor. Reads input file and builds the JSON-string.
	 */
	public KnownHostsManager() {
		
		jsonStringFromInputFile = getJsonStringFromFile();
		
	}
	
	private String getJsonStringFromFile() {
		Scanner scanner = null;
		String jsonString = "";
		try {
			scanner = new Scanner(new File("known_hosts.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found!");
		}

		while (scanner.hasNextLine()) {
			jsonString += scanner.nextLine().trim();
		}
		scanner.close();
		//TODO: remove syso nax.
		System.out.println("jsonString: "
				+ jsonString);
		
		return jsonString;
	}
	
	public String getJsonString() {
		return getJsonStringFromFile();
	}

	/**
	 * Generates HashMap from the JSON-string.
	 * 
	 * @return HashMap, where the <b>key</b> is a <i>name</i> and the
	 *         <b>value</b> as an <i>ip-address</i>,
	 */
	public HashMap<String, String> getMapOfKnownHosts() {
		HashMap<String, String> mapOfKnownHosts = new HashMap<String, String>();
		String[][] knownHosts = gson.fromJson(jsonStringFromInputFile,
				String[][].class);

		for (String[] host : knownHosts) {
			// System.out.println(host[0] + "\t" + host[1]);
			mapOfKnownHosts.put(host[0], host[1]);
		}
		// System.out.println(gson.toJson(knownHosts));
		return mapOfKnownHosts;
	}

	/**
	 * Adds one host to the end of the known hosts.
	 * 
	 * @param name
	 *            - the name of the host`s owner.
	 * @param ip
	 *            - the ip-address of the host.
	 */
	public void addNewHost(String name, String ip) {

		String[][] knownHosts = gson.fromJson(jsonStringFromInputFile,
				String[][].class);
		String[][] updatedKnownHosts = Arrays.copyOf(knownHosts,
				knownHosts.length + 1);

		updatedKnownHosts[updatedKnownHosts.length - 1] = new String[2];
		updatedKnownHosts[updatedKnownHosts.length - 1][0] = name;
		updatedKnownHosts[updatedKnownHosts.length - 1][1] = ip;

	}

	public void addNewHosts(String jsonString) {
		// TODO: get hashmap of already known hosts.
		HashMap<String, String> mapOfKnownHosts = getMapOfKnownHosts();
		// TODO: add to hashmap new values and update values of the existed
		// keys.
		String[][] newHosts = gson.fromJson(jsonString, String[][].class);

		for (String[] host : newHosts) {
			String key = host[0];
			String value = host[1];
			mapOfKnownHosts.put(key, value);
		}
		// TODO: parse hashmap back to the JSON.
		// TODO: update file.
	}
}

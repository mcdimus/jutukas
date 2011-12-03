package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
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
	 * The name of the file with data.
	 */
	private static final String FILE_NAME = "known_hosts.txt";
	/**
	 * The instance of the GSON object with pretty printing.
	 */
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Constructor. Does nothing.
	 */
	public KnownHostsManager() {

	}

	/**
	 * Reads data from file and generates JSON string.
	 * 
	 * @return JSON string.
	 */
	private synchronized String getJsonStringFromFile() {
		Scanner scanner = null;
		String jsonString = "";
		try {
			scanner = new Scanner(new File(FILE_NAME));
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found!");
		}

		while (scanner.hasNextLine()) {
			jsonString += scanner.nextLine().trim();
		}
		scanner.close();
		// TODO: remove syso nax.
		System.out.println("jsonString: " + jsonString);

		return jsonString;
	}

	/**
	 * Returns JSON string with all known hosts.
	 * 
	 * @return JSON string with all known hosts.
	 */
	public synchronized String getJsonString() {
		return getJsonStringFromFile();
	}

	/**
	 * Generates HashMap from the JSON-string.
	 * 
	 * @return HashMap, where the <b>key</b> is a <i>name</i> and the
	 *         <b>value</b> as an <i>ip-address</i>,
	 */
	public synchronized HashMap<String, String> getMapOfKnownHosts() {
		String jsonString = getJsonStringFromFile();
		HashMap<String, String> mapOfKnownHosts = new HashMap<String, String>();
		String[][] knownHosts = gson.fromJson(jsonString, String[][].class);

		for (String[] host : knownHosts) {
			mapOfKnownHosts.put(host[0], host[1]);
		}
		return mapOfKnownHosts;
	}

	/**
	 * Adds new hosts to the existing hosts . If host with given name already
	 * exists, it`s ip-address will be replaced.
	 * 
	 * @param jsonString
	 *            - JSON string with new hosts.
	 */
	public synchronized void addNewHosts(String jsonString) {
		// get hashmap of already known hosts.
		HashMap<String, String> mapOfKnownHosts = getMapOfKnownHosts();
		// add to hashmap new values and update values of the existed
		// keys.
		String[][] newHosts = gson.fromJson(jsonString, String[][].class);

		for (String[] host : newHosts) {
			String key = host[0];
			String value = host[1];
			mapOfKnownHosts.put(key, value);
		}
		// parse hashmap back to the JSON.
		String[][] updatedHosts = new String[mapOfKnownHosts.size()][2];
		int index = 0;
		for (Map.Entry<String, String> entry : mapOfKnownHosts.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			updatedHosts[index][0] = key;
			updatedHosts[index++][1] = value;
		}
		// update file.
		writeJsonStringToFile(gson.toJson(updatedHosts));
	}

	/**
	 * Adds one new host.
	 * 
	 * @param name
	 *            - the name of the new host`s owner.
	 * @param ip
	 *            - the ip-address of the new host.
	 */
	public synchronized void suzanna(String name, String ip) {
		String[][] newHost = new String[1][1];
		newHost[0][0] = name;
		newHost[0][1] = ip;

		String jsonStringWithOneNewHost = gson.toJson(newHost);

		addNewHosts(jsonStringWithOneNewHost);
	}

	/**
	 * Saves JSON string to the file.
	 * 
	 * @param jsonString
	 *            - JSON string for saving.
	 */
	private synchronized void writeJsonStringToFile(String jsonString) {
		Formatter output = null;
		try {
			output = new Formatter(new File(FILE_NAME));
		} catch (FileNotFoundException e) {
			System.err.println("File cannot be created!");
		}
		output.format("%s", jsonString);
		output.flush();
		output.close();
	}

	public void replaceFirstEntryInFile(String name, String ip) {
		String oldJsonString = getJsonStringFromFile();
		String[][] oldArray = gson.fromJson(oldJsonString, String[][].class);
		oldArray[0][0] = name;
		oldArray[0][1] = ip;

		String newJsonString = gson.toJson(oldArray);
		writeJsonStringToFile(newJsonString);
	}

	public String getYourNameAndPort() {
		String jsonString = getJsonStringFromFile();
		String[][] nameArray = gson.fromJson(jsonString, String[][].class);
		return nameArray[0][0] + ";" + nameArray[0][1].split(":")[1];
	}
	
	public String[][] getArrayFromJson() {
		String jsonString = getJsonStringFromFile();
		String[][] knownHosts = gson.fromJson(jsonString, String[][].class);
		return knownHosts;
	}
}

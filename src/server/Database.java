package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Database {

	private static final String				usersFile		= "C:\\Users\\vKint\\Desktop\\Files\\users.txt";
	private static final String				adminsFile		= "C:\\Users\\vKint\\Desktop\\Files\\admins.txt";

	private static Database					instance		= null;
	private Hashtable<String, String>		users			= null;
	private Hashtable<String, String>		admins			= null;
	private BufferedReader					usersReader		= null;
	private BufferedReader					adminsReader	= null;

	public Hashtable<String, ClientThread>	usersOn			= null;

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	private Database() {
		try {
			usersReader = new BufferedReader(new FileReader(usersFile));
			adminsReader = new BufferedReader(new FileReader(adminsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		users = new Hashtable<String, String>();
		admins = new Hashtable<String, String>();
		usersOn = new Hashtable<String, ClientThread>();
		updateDatabase(true, true);
	}

	public void updateDatabase(boolean updateUsers, boolean updateAdmins) {
		try {
			String line;
			if (updateUsers) {
				while ((line = usersReader.readLine()) != null) {
					String[] data = line.split(",");
					users.put(data[0], data[1]);
				}
			}
			if (updateAdmins) {
				while ((line = adminsReader.readLine()) != null) {
					String[] data = line.split(",");
					users.put(data[0], data[1]);
					admins.put(data[0], data[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Hashtable<String, String> getUsersTable() {
		return users;
	}

	public Hashtable<String, String> getAdminsTable() {
		return admins;
	}

	public String getUsersFile() {
		return usersFile;
	}

	public String getAdminsFile() {
		return adminsFile;
	}
}

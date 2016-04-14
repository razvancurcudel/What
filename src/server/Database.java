package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Database {

	private static final String					usersFile		= "C:\\Users\\vKint\\Desktop\\Files\\users.txt";
	private static final String					adminsFile		= "C:\\Users\\vKint\\Desktop\\Files\\admins.txt";

	private static Database						instance		= null;
	private Hashtable<String, String>			users			= null;
	private Hashtable<String, String>			admins			= null;
	private BufferedReader						usersReader		= null;
	private BufferedReader						adminsReader	= null;

	private static LinkedList<UserCredentials>	usersOn			= null;
	private LinkedList<UserCredentials>			adminsOn		= null;

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
		usersOn = new LinkedList<UserCredentials>();
		adminsOn = new LinkedList<UserCredentials>();
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

	public LinkedList<UserCredentials> getUsersOn() {
		return usersOn;
	}

	public LinkedList<UserCredentials> getAdminsOn() {
		return adminsOn;
	}

	public String getUsersFile() {
		return usersFile;
	}

	public String getAdminsFile() {
		return adminsFile;
	}
}

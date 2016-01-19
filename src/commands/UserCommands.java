package commands;

import java.util.Map.Entry;

import data.Packet;
import server.ClientThread;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class UserCommands implements Command {

	Database db = Database.getInstance();

	public UserCommands() {

	}

	public void verify() {
		System.out.println("Not admin");
	}

	@Override
	public void usersOn() {
		System.out.println("Number of users online: " + db.usersOn.size());
		for (Entry<String, ClientThread> entry : db.usersOn.entrySet()) {
			System.out.println(entry.getKey());
		}
	}

	@Override
	public void adminsOn() {
		for (Entry<String, ClientThread> entry : db.usersOn.entrySet()) {
			if(db.getAdminsTable().contains(entry.getKey())) {
				System.out.println(entry.getKey());
			}
		}
	}

	@Override
	public void kick(String user) {
		System.out.println("You are not admin, you can't kick");
	}

	@Override
	public void admins() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noOfUsersOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noOfAdminsOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noOfWarnings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToAll(Packet packet) {
		// TODO Auto-generated method stub
		
	}

}

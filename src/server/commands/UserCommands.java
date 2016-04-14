package server.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import data.Packet;
import data.TYPE;
import data.UserCredentials;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class UserCommands implements Command {

	protected Database db = Database.getInstance();

	public UserCommands() {

	}

	@Override
	public void help(UserCredentials user) throws IOException {
		String helpMessage = "Commands avaible:\n" + "\\time - show time of the server\n"
				+ "\\info - show info about user\n" + "\\admins - show admins of the server\n"
				+ "\\admins on - show which admins are online\n"
				+ "\\users on - show which users are online\n"
				+ "\\pm [user] - send a private message to user\n";
//		user.getSender().sendMessage(new Packet(helpMessage, TYPE.MESSAGE));
	}

	@Override
	public void info(UserCredentials user) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showTime(UserCredentials user) throws IOException {
		String message = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date());
//		user.getSender().sendMessage(new Packet(message, TYPE.MESSAGE));
	}

	@Override
	public void admins(UserCredentials user) throws IOException {
		String toSend = "Admins on this server:\n";
		Iterator<Map.Entry<String, String>> it = db.getAdminsTable().entrySet().iterator();
		while (true) {
			toSend += it.next().getKey();
			if (it.hasNext()) {
				toSend += ", ";
			} else {
				toSend += ".";
				break;
			}
		}
//		user.getSender().sendMessage(new Packet(toSend, TYPE.MESSAGE));
	}

	@Override
	public void usersOn(UserCredentials user) throws IOException {
		String users = "";
		users += "Users on: " + db.getUsersOn().size() + "\n";
		LinkedList<UserCredentials> usersOn = db.getUsersOn();
		for (int i = 0; i < usersOn.size(); i++) {
			UserCredentials userX = usersOn.get(i);
			if (i == usersOn.size() - 1) {
				users += userX.getUsername() + ".";
			} else {
				users += userX.getUsername() + ", ";
			}
		}
//		user.getSender().sendMessage(new Packet(users, TYPE.MESSAGE));
	}

	@Override
	public void adminsOn(UserCredentials user) throws IOException {
		String admins = "";
		admins += "Admins on: " + db.getAdminsOn().size() + "\n";
		LinkedList<UserCredentials> adminsOn = db.getAdminsOn();
		for (int i = 0; i < adminsOn.size(); i++) {
			UserCredentials admin = adminsOn.get(i);
			if (i == adminsOn.size() - 1) {
				admins += admin.getUsername() + ".";
			} else {
				admins += admin.getUsername() + ", ";
			}
		}
//		user.getSender().sendMessage(new Packet(admins, TYPE.MESSAGE));
	}

	@Override
	public void noOfWarnings(UserCredentials user) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void privateMessage(UserCredentials user1, UserCredentials user2, String message)
			throws IOException {

		Packet packet1 = new Packet("PM to " + user2.getUsername() + ": " + message, TYPE.MESSAGE);
		Packet packet2 = new Packet("PM from " + user1.getUsername() + ": " + message,
				TYPE.MESSAGE);
//		user1.getSender().sendMessage(packet1);
//		user2.getSender().sendMessage(packet2);
	}

	@Override
	public void kick(UserCredentials user) throws IOException {
		// TODO Auto-generated method stub

	}

}

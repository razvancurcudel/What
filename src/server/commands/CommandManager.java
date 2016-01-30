package server.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import data.Packet;
import data.TYPE;
import data.UserCredentials;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class CommandManager {

	private Command		command	= null;
	private Database	db		= Database.getInstance();

	public CommandManager(UserCommands command) {
		this.command = command;
	}

	public void execCommand(UserCredentials user, String action) throws IOException {
		action = action.toLowerCase().substring(1, action.length()); // rid of /

		String[] tokens = action.split("\\s");
		switch (tokens[0]) {

			case "help":
				command.help(user);
				break;
				
			case "time":
				command.showTime(user);
				break;
			
			case "pm":
				UserCredentials toWho = null;
				for (UserCredentials searchedUser : db.getUsersOn()) {
					if (searchedUser.getUsername().equalsIgnoreCase(tokens[1])) {
						toWho = searchedUser;
						break;
					}
				}
				if (toWho == null) {
					// XXX change message
					user.getSender().sendMessage(new Packet("User undetected", TYPE.MESSAGE));
					break;
				}
				if (toWho.equals(user)) {
					user.getSender()
							.sendMessage(new Packet("You can't pm to yourself", TYPE.MESSAGE));
					break;
				}
				String message = "";
				for (int i = 2; i < tokens.length; ++i) {
					message += tokens[i] + " ";
				}
				command.privateMessage(user, toWho, message);
				break;

			case "update":
				if (tokens.length == 1) {
					System.out.println("Updating DB");
					db.updateDatabase(true, true);
					break;
				}
				if (tokens[1].equals("users")) {
					System.out.println("Updating users");
					db.updateDatabase(true, false);
				}
				if (tokens[1].equals("admins")) {
					System.out.println("Updating admins");
					db.updateDatabase(false, true);
				}
				break;

			case "add":
				if (tokens[1].equals("user")) {
					PrintWriter out = new PrintWriter(
							new BufferedWriter(new FileWriter(db.getUsersFile(), true)));
					out.print(tokens[2] + ",");
					out.print(tokens[3]);
					out.println();
					out.close();
				}
				if (tokens[1].equals("admin")) {
					PrintWriter out = new PrintWriter(
							new BufferedWriter(new FileWriter(db.getAdminsFile(), true)));
					out.print(tokens[2] + ",");
					out.print(tokens[3]);
					out.println();
					out.close();
				}
				break;

			case "users":
				if (tokens.length != 2 || !tokens[1].equals("on")) {
					String errorMessage = "Usage \\Users On";
					user.getSender().sendMessage(new Packet((errorMessage), TYPE.MESSAGE));
					break;
				}
				command.usersOn(user);
				break;

			case "admins":
				if(tokens.length == 1) {
					command.admins(user);
					break;
				}
				if (tokens.length != 2 || !tokens[1].equals("on")) {
					String errorMessage = "Usage \\Admins On";
					user.getSender().sendMessage(new Packet((errorMessage), TYPE.MESSAGE));
					break;
				}
				command.adminsOn(user);
				break;

			case "kick":
				if (tokens.length < 2) {
					System.out.println("Usage \\kick name_of_user");
					break;
				}
				break;

			default:
				user.getSender().sendMessage(new Packet("Invalid command", TYPE.MESSAGE));
				break;
		}
	}

}

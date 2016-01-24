package server.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import data.Packet;
import data.TYPE;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class CommandManager {

	private ObjectOutputStream	toClient	= null;
	// private ObjectInputStream fromClient = null;
	private Command				command		= null;
	private Database			db			= Database.getInstance();

	public CommandManager(UserCommands command) {

//		this.toClient = toClient;
		// this.fromClient = fromClient;
		this.command = command;

	}

	public void execCommand(String action) throws IOException {
		action = action.toLowerCase();
		String[] tokens = action.split("\\s");
		switch (tokens[0]) {

			case "stop":
				toClient.writeObject(new Packet("\\Stop", TYPE.COMMAND));
				toClient.flush();
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
					System.out.println("Usage \\Users On");
					break;
				}
				command.usersOn();
				break;

			case "admins":
				if (tokens.length != 2 || !tokens[1].equals("on")) {
					System.out.println("Usage \\Admins On");
					break;
				}
				command.adminsOn();
				break;

			case "kick":
				if (tokens.length < 2) {
					System.out.println("Usage \\kick name_of_user");
					break;
				}
				command.kick(tokens[1]);
				break;

			default:
				System.out.println("invalid command");
				break;
		}
	}
}

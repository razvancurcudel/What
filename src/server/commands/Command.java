package server.commands;

import java.io.IOException;

import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public interface Command {
	
	public void help(UserCredentials user) throws IOException;
	public void info(UserCredentials user) throws IOException;
	public void showTime(UserCredentials user) throws IOException;

	public void admins(UserCredentials user) throws IOException;
	public void usersOn(UserCredentials user) throws IOException;
	public void adminsOn(UserCredentials user) throws IOException;
	public void noOfWarnings(UserCredentials user) throws IOException;

	public void privateMessage(UserCredentials user1, UserCredentials user2, String message) throws IOException;

	public void kick(UserCredentials user) throws IOException;
}


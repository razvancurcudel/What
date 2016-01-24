package server.commands;

/**
 * @author Curcudel Ioan-Razvan
 */

public interface Command {
	public void admins();
	public void usersOn();
	public void adminsOn();
	public void noOfUsersOn();
	public void noOfAdminsOn();
	public void noOfWarnings();
	
	public void kick(String user);
}


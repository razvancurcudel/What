package commands;

import data.Packet;

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
	public void sendToAll(Packet packet);
	
	public void kick(String user);
}


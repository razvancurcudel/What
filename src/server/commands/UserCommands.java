package server.commands;

import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class UserCommands implements Command {

	protected Database db = Database.getInstance();

	public UserCommands() {

	}

	public void verify() {
		System.out.println("Not admin");
	}

	@Override
	public void usersOn() {
		//TODO usersArray
		
	}

	@Override
	public void adminsOn() {
		//TODO adminArray
		
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

}

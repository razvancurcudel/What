package server.commands;

/**
 * @author Curcudel Ioan-Razvan
 */

public class AdminCommands extends UserCommands{

	@Override
	public void verify() {
		System.out.println("Admin");
	}
	
	@Override
	public void kick(String user) {
//		try {
//			db.usersOn.get(user).execCommand("Stop");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
}


package commands;

import java.io.IOException;
import java.util.Map.Entry;

import data.Packet;
import server.ClientThread;

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
	
	@Override
	public void sendToAll(Packet packet) {
		for(Entry<String, ClientThread> entry : db.usersOn.entrySet()) {
			try {
				entry.getValue().sendMessage(packet);
			} catch (IOException e) {
				System.out.println("Can't broadcast");
				e.printStackTrace();
			}
		}
	}
	
}


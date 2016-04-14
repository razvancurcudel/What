package server.threads;

import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import data.Packet;
import data.Privilege;
import data.UserCredentials;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Dispatcher implements Runnable{

	private Queue<Packet>	messages	= new LinkedList<Packet>();
	private static Database	db			= Database.getInstance();

	public synchronized void connectUser(UserCredentials user) {
		if (user.getPrivilege() == Privilege.ADMIN || user.getPrivilege() == Privilege.ROOT) {
			db.getAdminsOn().add(user);
		}
		db.getUsersOn().add(user);
	}

	public synchronized void disconnectUser(UserCredentials user) {
		int isUser = db.getUsersOn().indexOf(user);
		if (isUser != -1) {
			db.getUsersOn().remove(isUser);
			int isAdmin = db.getAdminsOn().indexOf(user);
			if (isAdmin != -1) {
				db.getAdminsOn().remove(isAdmin);
			}
			System.out.println(user.getUsername() + " has disconnected at "
					+ new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(new Date()));
		}
	}

	public synchronized void processPacket(Packet packet) {
		messages.add(packet);
		notify();
	}

	public synchronized Packet getMessage() throws InterruptedException {
		while (messages.size() == 0)
			wait();
		Packet message = messages.poll();
		return message;
	}

	public synchronized void sendToUser(UserCredentials user, Packet packet) throws IOException {
		user.getSender().writeObject(packet);
		user.getSender().flush();
	}

	public synchronized void sendMessageToAll(Packet packet) throws IOException {
		for (int i = 0; i < db.getUsersOn().size(); ++i) {
			UserCredentials user = db.getUsersOn().get(i);
			if (user.getUsername().equals(packet.getSender())) {
				sendToUser(user, packet.changeSender("You"));
			} else
				sendToUser(user, packet);
		}
	}

	public synchronized void doTriviaStuffs(Packet packet) {
		// TODO implement trivia
	}

	@Override
	public void run() {

		try {
			while (true) {
				Packet message;
				message = getMessage();
				sendMessageToAll(message);
			}

		} catch (SocketException e) {
			System.out.println("UPS SOCKET"); // TODO treat excepion right
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

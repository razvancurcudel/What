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

public class Dispatcher extends Thread {

	private Queue<Packet>	messages	= new LinkedList<Packet>();
	private static Database	db			= Database.getInstance();

	public synchronized void addUser(UserCredentials user) {
		if (user.getPrivilege() == Privilege.ADMIN || user.getPrivilege() == Privilege.ROOT) {
			db.getAdminsOn().add(user);
		}
		db.getUsersOn().add(user);
	}

	public synchronized void deleteUser(UserCredentials user) {
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

	public static synchronized void sendToUser(UserCredentials fromWho, UserCredentials toWho,
			Packet packet) throws IOException {
		System.out.println("OK1");
		if (db.getUsersOn().contains(toWho)) {
			System.out.println("OK2");
			toWho.getSender().sendMessage(packet.changeSender(fromWho.getUsername()));
		}

	}

	public synchronized void sendMessageToAll(Packet packet) throws IOException {
		for (int i = 0; i < db.getUsersOn().size(); ++i) {
			UserCredentials user = db.getUsersOn().get(i);
			if (user.getUsername().equals(packet.getSender())) {
				user.getSender().sendMessage(packet.changeSender("You"));
			} else
				user.getSender().sendMessage(packet);
		}
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

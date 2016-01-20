package server;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import data.Packet;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Dispatcher extends Thread {

	private Queue<Packet>	messages	= new LinkedList<Packet>();
	Database				db			= Database.getInstance();

	public synchronized void addUser(UserCredentials user) {
		db.online.add(user);
	}

	public synchronized void deleteUser(UserCredentials user) {
		int index = db.online.indexOf(user);
		if (index != -1) {
			db.online.remove(index);
		}
	}

	public synchronized void processPacket(Packet packet) {
		messages.add(new Packet(packet.data, "String"));
		notify();
	}

	public synchronized Packet getMessage() throws InterruptedException {
		while (messages.size() == 0)
			wait();
		Packet message = messages.poll();
		return message;
	}

	public synchronized void sendMessageToAll(Packet packet) throws IOException {
		for (int i = 0; i < db.online.size(); ++i) {
			db.online.get(i).getSender().sendMessage(packet);
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

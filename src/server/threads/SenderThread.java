package server.threads;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import data.Packet;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class SenderThread extends Thread {

	private Dispatcher			dispatcher	= null;
	private UserCredentials		user		= null;
	private ObjectOutputStream	toClient	= null;
	private Queue<Packet>		messages	= null;

	SenderThread(Dispatcher dispatcher, UserCredentials user, ObjectOutputStream toClient) {
		this.dispatcher = dispatcher;
		this.user = user;
		this.toClient = toClient;
		this.messages = new LinkedList<Packet>();
	}

	public synchronized Packet getMessage() throws InterruptedException {
		while (messages.size() == 0)
			wait();
		Packet message = messages.poll();
		return message;
	}

	public synchronized void sendMessage(Packet packet) throws IOException {
		toClient.writeObject(packet);
		toClient.flush();
	}

	@Override
	public void run() {
		try {

			while (!isInterrupted()) {
				Packet message;
				message = getMessage();
				sendMessage(message);
			}

		} catch (InterruptedException e) {
			user.getListener().interrupt(); // TODO modify somehow here..
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (user.getListener().isAlive()) {
			user.getListener().interrupt();
		}
		dispatcher.deleteUser(user);
	}
}

package server.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

import data.Packet;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ListenerThread extends Thread {

	Dispatcher			dispatcher;
	UserCredentials		user;
	ObjectInputStream	fromClient;

	ListenerThread(Dispatcher dispatcher, UserCredentials user, ObjectInputStream fromClient) {
		this.dispatcher = dispatcher;
		this.user = user;
		this.fromClient = fromClient;

	}

	@Override
	public void run() {
		try {

			while (!isInterrupted()) {

				Packet packet = (Packet) fromClient.readObject();
				if (packet == null) {
					break;
				}
				System.out
						.println("Received from client " + user.getUsername() + ": " + packet.data);
				packet = packet.changeSender(user.getUsername());
				dispatcher.processPacket(packet);

			}
		} catch (SocketException e) {
			System.out.println("Socket exception");// TODO treat exception right
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		if (user.getSender().isAlive()) {
			user.getSender().interrupt();
		}
		dispatcher.deleteUser(user);
	}
}

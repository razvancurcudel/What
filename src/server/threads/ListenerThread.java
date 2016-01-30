package server.threads;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

import data.Packet;
import data.TYPE;
import data.UserCredentials;
import server.commands.CommandManager;
import server.commands.CommandsFactory;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ListenerThread extends Thread {

	private Dispatcher			dispatcher;
	private UserCredentials		user;
	private ObjectInputStream	fromClient;
	CommandManager				cm	= null;

	ListenerThread(Dispatcher dispatcher, UserCredentials user, ObjectInputStream fromClient) {
		this.dispatcher = dispatcher;
		this.user = user;
		this.fromClient = fromClient;
		this.cm = CommandsFactory.getCommand(user.getPrivilege());
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {

				Packet packet = (Packet) fromClient.readObject();
				if (packet == null) {
					break;
				}
				System.out.println("Received (" + packet.getType() + ") from " + user.getUsername()
						+ ": " + packet.getData());
				packet = packet.changeSender(user.getUsername()); // XXX change
																	// the
																	// sender
				if (packet.getType() == TYPE.MESSAGE) {
					dispatcher.processPacket(packet);
				} else {
					cm.execCommand(user, (String) packet.getData());
				}

			}
		} catch (EOFException e) {
			// System.out.println("EOF in listenerSever"); // TODO don't skip
			// like
			// this
		} catch (SocketException e) {
			System.out.println("Socket exception");// TODO treat exception right
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (user.getSender().isAlive()) {
			user.getSender().interrupt();
		}
		dispatcher.deleteUser(user);
	}
}

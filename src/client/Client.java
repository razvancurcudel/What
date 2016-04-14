package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import data.Packet;
import data.TYPE;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Client {

	
	public static final String	HOST		= "localhost";
	public static final int		PORT		= 1234;

	private Socket				socket		= null;
	private ObjectInputStream	fromServer	= null;
	private ObjectOutputStream	toServer	= null;

	public ListenerThread		listener	= null;
	public Queue<Packet>		messages	= null;

	public void connect(String host, int port) {

		System.out.println("Trying to connect");

		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Connection went OK");

		try {
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			messages = new LinkedList<>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO use this somewhere
	public void disconnect() {

		System.out.println("Closing stuffs");
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			socket = null;
		}
		try {
			if (fromServer != null) {
				fromServer.close();
				fromServer = null;
			}
		} catch (IOException e) {
			fromServer = null;
		}
		try {
			if (toServer != null) {
				toServer.close();
				toServer = null;
			}
		} catch (IOException e) {
			toServer = null;
		}
	}

	public boolean logIn(String username, String password) {
		UserCredentials user = new UserCredentials(username, password);
		boolean canConnect = false;
		try {
			toServer.writeObject(user);
			toServer.flush();
			canConnect = fromServer.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canConnect;

	}

	public void send(String s, TYPE type) {
		try {
			toServer.writeObject(new Packet(s, type));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startListen() {
		listener = new ListenerThread();
		listener.start();
	}

	public class ListenerThread extends Thread {

		@Override
		public void run() {
			try {
				while (!isInterrupted()) {
					Object response;
					response = fromServer.readObject();
					if (response != null) {
						Packet packet = (Packet) response;
						messages.add(packet);
						System.out.println(packet.getSender() + ": " + packet.getData());
					}
				}
			} catch (SocketException e) {
				System.out.println("Socket Exception in client");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

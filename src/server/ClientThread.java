package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import commands.AdminCommands;
import commands.CommandManager;
import commands.UserCommands;
import data.Packet;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ClientThread implements Runnable {

	private Socket						socket		= null;
	private ObjectOutputStream			toClient	= null;
	private ObjectInputStream			fromClient	= null;
	private UserCredentials				user		= null;
	private CommandManager				cm			= null;
	private UserCommands				command		= null;
	public LinkedBlockingQueue<Packet>	messages	= null;

	private boolean						running		= true;
	private Database					db			= Database.getInstance();

	public ReadingThread				listener	= null;

	public ClientThread(Socket socket) {
		this.socket = socket;
	}

	private void stop() throws IOException {
		System.out.println("Client " + user.getUsername() + " has disconnected");
		db.usersOn.remove(user.getUsername());
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			socket = null;
		}
		try {
			if (fromClient != null) {
				fromClient.close();
				fromClient = null;
			}
		} catch (IOException e) {
			fromClient = null;
		}
		try {
			if (toClient != null) {
				toClient.close();
				toClient = null;
			}
		} catch (IOException e) {
			toClient = null;
		}
	}

	private boolean isPassCorrect(UserCredentials user, String password) {
		return user.getPassword().equals(password);
	}

	public Packet readMessage() throws ClassNotFoundException, IOException {
		Object response = fromClient.readObject();
		Packet data = null;
		if (response != null) {
			data = (Packet) response;
			System.out.println("Received from client " + user.getUsername() + ": " + data.data);
		}
		return data;
	}

	public void sendMessage(Packet packet) throws IOException {
		if (((String) packet.data).startsWith("\\")) {
			String packetConv = (String) packet.data;
			String action = (packetConv.substring(1, packetConv.length()));
			cm.execCommand(action);
			toClient.writeObject(null);
		} else if (((String) packet.data).equals("WMN?")) {
			toClient.writeObject(new Packet(user.getUsername(), "String"));
		} else {
			packet.data = packet.data + " changed";
			toClient.writeObject(packet);
		}
		toClient.flush();
	}

	@Override
	public void run() {

		try {
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());
			messages = new LinkedBlockingQueue<Packet>();
			while (true) {
				user = (UserCredentials) fromClient.readObject();
				// verify if user is in users list
				boolean isUserHere = db.getUsersTable().containsKey(user.getUsername());
				if (isUserHere) {

					// verify if password is right
					String password = db.getUsersTable().get(user.getUsername());

					if (isPassCorrect(user, password)) {
						toClient.writeBoolean(true); // can connect
						toClient.flush();
						// if user is admin set privilege level to high
						if (db.getAdminsTable().containsKey(user.getUsername())) {
							user.setAdmin(true);
							command = new AdminCommands();
						} else {
							command = new UserCommands();
						}
						break;
					}
				}
				toClient.writeBoolean(false);
				toClient.flush();
			}

			System.out.println(
					user.getUsername() + " has connected and has IP " + socket.getInetAddress());

			cm = new CommandManager(this, toClient, fromClient, command);

			// add thread to usersOn table
			db.usersOn.put(user.getUsername(), this);

			listener = new ReadingThread();
			listener.start();

			while (running) {
				if (!messages.isEmpty()) {
					Packet data = messages.take();
					if (data != null) {
						sendMessage(data);
					} else {
						System.out.println("data is null");
					}
				}
			}
			// listener.interrupt();
		} catch (EOFException e) {
			System.out.println("End of objects for client " + user.getUsername());
		} catch (SocketException e) {
			System.out.println("Exception socket in " + user.getUsername() + " thread");
		} catch (IOException e) {
			System.out.println("IO Excepion in " + user.getUsername() + " thread");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Wrong Packet");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void setRunning(boolean value) {
		running = value;
	}

	class ReadingThread extends Thread {

		@Override
		public void run() {
			boolean boom = true;
			while (boom) {

				try {
					Object response = null;
					Packet data = null;
					response = fromClient.readObject();
					if (response != null) {
						data = (Packet) response;
						System.out
								.println("Received from " + user.getUsername() + ": " + data.data);
						messages.put(data);
					}

				} catch (SocketException e) {
					System.out.println("socket failed");
					e.printStackTrace();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("InterrupdetExcepion");
					e.printStackTrace();
				}

			}
			System.out.println("Close listener");
		}

	}

}

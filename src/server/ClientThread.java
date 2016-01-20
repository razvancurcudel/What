package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ClientThread implements Runnable {

	private Socket				socket		= null;
	private ObjectOutputStream	toClient	= null;
	private ObjectInputStream	fromClient	= null;

	private UserCredentials		user		= null;
	// private CommandManager cm = null;
	// private UserCommands command = null;

	public ListenerThread		listener	= null;
	public SenderThread			sender		= null;
	public Dispatcher			dispatcher	= null;

	private Database			db			= Database.getInstance();

	public ClientThread(Socket socket, Dispatcher dispatcher) {
		this.socket = socket;
		this.dispatcher = dispatcher;
	}

	@SuppressWarnings("unused")
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

	@Override
	public void run() {

		try {
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());

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
						//TODO what if admin ?
						if (db.getAdminsTable().containsKey(user.getUsername())) {
							user.setAdmin(true);
							// command = new AdminCommands();
						} else {
							
							// command = new UserCommands();
						}
						break;
					}
				}
				toClient.writeBoolean(false);
				toClient.flush();
			}

			System.out.println(
					user.getUsername() + " has connected and has IP " + socket.getInetAddress());

			// TODO what to do with that ?
			// cm = new CommandManager(this, toClient, fromClient, command);

			listener = new ListenerThread(dispatcher, user, fromClient);
			sender = new SenderThread(dispatcher, user, toClient);

			user.setListener(listener);
			user.setSender(sender);
			db.online.add(user);
			listener.start();
			sender.start();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}

package server.threads;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.Privilege;
import data.UserCredentials;
import server.Database;

/**
 * @author Curcudel Ioan-Razvan
 */

public class LogInThread implements Runnable {

	private Socket				socket		= null;
	private ObjectOutputStream	toClient	= null;
	private ObjectInputStream	fromClient	= null;

	private UserCredentials		user		= null;

	private ListenerThread		listener	= null;
	private SenderThread		sender		= null;
	private Dispatcher			dispatcher	= null;

	private Database			db			= Database.getInstance();

	public LogInThread(Socket socket, Dispatcher dispatcher) {
		this.socket = socket;
		this.dispatcher = dispatcher;
	}

	// TODO use this method somehow
	private void stop() {
		System.out.println("Client with IP " + socket.getInetAddress() + " left" );
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

	// TODO maybe move this somewhere else
	private boolean isPassCorrect(UserCredentials user, String password) {
		return user.getPassword().equals(password);
	}

	@Override
	public void run() {

		try {
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());

			while (true) {
				// TODO add action: Log In, Sign Up, Change Pass
				// and act adequate

				user = (UserCredentials) fromClient.readObject();
				// verify if user is in users list
				boolean isUserHere = db.getUsersTable().containsKey(user.getUsername());

				if (isUserHere) {

					// verify if password is right
					String password = db.getUsersTable().get(user.getUsername());

					if (isPassCorrect(user, password)) {
						toClient.writeBoolean(true); // can connect
						toClient.flush();
						if (user.getUsername().equals("root")) {
							user.setPrivilege(Privilege.ROOT);
							dispatcher.addUser(user);
						} else if (db.getAdminsTable().containsKey(user.getUsername())) {
							user.setPrivilege(Privilege.ADMIN);
							dispatcher.addUser(user);
						} else {
							user.setPrivilege(Privilege.USER);
							dispatcher.addUser(user);
						}
						break;
					}
				}
				toClient.writeBoolean(false);
				toClient.flush();
			}

			System.out.println(user.getUsername() + " connected at "
					+ new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date())
					+ " and has IP " + socket.getInetAddress());

			listener = new ListenerThread(dispatcher, user, fromClient);
			sender = new SenderThread(dispatcher, user, toClient);

			user.setListener(listener);
			user.setSender(sender);
			listener.start();
			sender.start();

		} catch (EOFException e) {
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}

package client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import data.Packet;
import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Client {

	private static final String	HOST		= "localhost";
	private static final int	PORT		= 1234;

	private Socket				socket		= null;
	private ObjectInputStream	fromServer	= null;
	private ObjectOutputStream	toServer	= null;
	private BufferedReader		inFromUser	= null;

	private void connect(String host, int port) {

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
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void disconnect() {
		System.out.println("Closing stuffs");
		try {
			if (inFromUser != null) {
				inFromUser.close();
				inFromUser = null;
			}
		} catch (IOException e) {
			inFromUser = null;
		}
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

	private UserCredentials getLogInDetails() {
		UserCredentials logInDetails = null;
		try {
			System.out.print("Username: ");
			String username = inFromUser.readLine();
			System.out.print("Password: ");
			String password = inFromUser.readLine();
			logInDetails = new UserCredentials(username, password);
			logInDetails.setAdmin(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logInDetails;
	}

	private void run(Client client) {

		UserCredentials logInData = client.getLogInDetails();

		try {

			boolean canConnect = false;
			toServer.writeObject(logInData);
			toServer.flush();
			canConnect = fromServer.readBoolean();
			while (!canConnect) {
				System.out.println("Username or password is wrong");
				logInData = client.getLogInDetails();
				toServer.writeObject(logInData);
				toServer.flush();
				canConnect = fromServer.readBoolean();
			}
			System.out.println("Connected");

			ListeningThread listener = new ListeningThread(client);
			Thread t = new Thread(listener);
			t.start();

			while (true) {
				// System.out.print("Waiting for input: ");
				// String input = inFromUser.readLine();
				// toServer.writeObject(new Packet(input, "String"));
				Object response = fromServer.readObject();
				if (response != null) {
					Packet packet = (Packet) response;
					if (((String) packet.data).equals("\\Stop")) {
						System.exit(0);
						listener.terminate();
						t.interrupt();
						break;
					}
					System.out.println("Here is the response: " + packet.data);
				}
				// if (input.equals("\\Stop")) {
				// break;
				// }
			}
		} catch (EOFException e) {
			System.out.println("EOF in client");
		} catch (IOException e) {
			System.out.println("IOExcepion in client main");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.connect(HOST, PORT);
		client.run(client);
		client.disconnect();
	}

	class ListeningThread implements Runnable {

		private volatile boolean running = true;

		ListeningThread(Client client) {}

		@Override
		public void run() {
			try {
				while (running) {

					System.out.println("Waiting for input: ");
					String input = inFromUser.readLine();
					toServer.writeObject(new Packet(input, "String"));
					if (input.equals("\\Stop")) {
						break;
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void terminate() {
			running = false;
		}

	}

}

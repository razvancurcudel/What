package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Server {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket hostServer = new ServerSocket(1234);
		while (true) {
			System.out.println("Waiting for clients..");
			Socket client = hostServer.accept();
			System.out
					.println("Client with IP " + client.getInetAddress() + " is trying to connect");
			Thread thread = new Thread(new ClientThread(client));
			thread.start();
		}
	}

}

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.threads.Dispatcher;
import server.threads.LogInThread;
import server.threads.TriviaBot;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Server {

	public static Dispatcher	dispatcher	= null;
	public static TriviaBot		bot			= null;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		System.out.println("Starting the server");
		ServerSocket hostServer = new ServerSocket(1234);

		dispatcher = new Dispatcher();
		new Thread(dispatcher).start();
		bot = new TriviaBot();
		bot.start();
		
		
		while (true) {
			System.out.println("Waiting for clients..");
			Socket client = hostServer.accept();
			System.out
					.println("Client with IP " + client.getInetAddress() + " is trying to connect");
			Thread thread = new Thread(new LogInThread(client, dispatcher));
			thread.start();
		}
	}

}

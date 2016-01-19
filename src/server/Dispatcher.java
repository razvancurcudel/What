package server;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import data.Packet;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Dispatcher extends Thread {

	public Queue<Packet> messages = new LinkedList<Packet>();
	Database db = Database.getInstance();
	
	public synchronized void dispatchMessage(String message) {
		messages.add(new Packet(message, "String"));
		notify();
	}

	public synchronized Packet getMessage() throws InterruptedException {
		while (messages.size() == 0)
			wait();
		Packet message = messages.poll();
		return message;
	}

	public synchronized  void sendMessageToAllClients(Packet packet) {
		for(Entry<String, ClientThread> entry : db.usersOn.entrySet()) {
//			entry.getValue().getSender().sendMessage();
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Packet message = getMessage();
				sendMessageToAllClients(message);
			}
		} catch (InterruptedException ie) {
			// Thread interrupted. Stop its execution
			System.out.println("Interrupdet in dispatcher");
		}
	}



}

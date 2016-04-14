package server.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import data.UserCredentials;

/**
 * @author Curcudel Ioan-Razvan
 */

public class TriviaBot extends Thread {

	private LinkedList<UserCredentials>	players		= null;
	HashMap<String, String>				questions	= null;

	public TriviaBot() {
		questions = new HashMap<>();
		questions.put("Ora", "23");
		questions.put("Data", "02");
		questions.put("Luna", "2");
		questions.put("An", "2016");

		ArrayList<String> q = new ArrayList<String>(questions.values());
		System.out.println(q);
	}

	@Override
	public void run() {
		// send question
		// wait
		boolean x = true;
		long y;
		System.out.println("trivia bot");
		
		while(true) {
			if(x) {
				y = System.currentTimeMillis();
			}
		}
	}

}

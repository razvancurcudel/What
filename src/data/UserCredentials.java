package data;

import java.io.Serializable;

import server.threads.ListenerThread;
import server.threads.SenderThread;

/**
 * @author Curcudel Ioan-Razvan
 */

public class UserCredentials implements Serializable {

	private static final long	serialVersionUID	= -6424896701445342406L;

	private String				username			= null;
	private String				password			= null;

	private SenderThread		sender				= null;
	private ListenerThread		listener			= null;

	private Privilege			privilege			= Privilege.NONE;

	public UserCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isPassCorrect(String password) {
		return this.password.equals(password);
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	public Privilege getPrivilege() {
		return privilege;
	}

	public ListenerThread getListener() {
		return listener;
	}

	public void setListener(ListenerThread listener) {
		this.listener = listener;
	}

	public SenderThread getSender() {
		return sender;
	}

	public void setSender(SenderThread sender) {
		this.sender = sender;
	}

}

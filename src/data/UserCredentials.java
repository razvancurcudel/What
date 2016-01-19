package data;

import java.io.Serializable;

/**
 * @author Curcudel Ioan-Razvan
 */

public class UserCredentials implements Serializable {

	private static final long	serialVersionUID	= -6424896701445342406L;
	private String				username;
	private String				password;
	private boolean				isAdmin;

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

	public void setAdmin(boolean value) {
		isAdmin = value;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
}

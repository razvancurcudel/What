package data;

import java.io.Serializable;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Packet implements Serializable {

	private static final long serialVersionUID = 8113021798904202914L;

	
	public Object				data;
	String						type;

	public Packet(Object data, String type) {
		this.data = data;
		this.type = type;
	}

}


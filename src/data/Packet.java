package data;

import java.io.Serializable;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Packet implements Serializable {

	private static final long	serialVersionUID	= 8113021798904202914L;

	private Object				data				= null;
	private TYPE				type				= null;
	private String				sender				= null;

	public Packet(Object data, TYPE type) {
		this.data = data;
		this.type = type;
	}

	public Packet changeSender(String name) {
		Packet changed = new Packet(data, type);
		changed.sender = name;
		return changed;
	}

	public String getSender() {
		return sender;
	}

	public TYPE getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}

}

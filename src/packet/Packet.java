package packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import core.Tuonela;

public abstract class Packet implements Serializable {

	private static final long serialVersionUID = -5978127360232063019L;
	private final String packetID;
	
	public Packet(String id) {
		this.packetID = id;
	}

	public void send(ObjectOutputStream out) {
		try {
			out.writeUTF("@"+packetID);
			out.writeObject(this);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receivedOnServer() {}
	public void receivedOnClient(Tuonela tuonela) {}

}

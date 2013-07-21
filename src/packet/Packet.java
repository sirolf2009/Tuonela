package packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.Tuonela;
import core.TuonelaServer;

public abstract class Packet {

	private final String packetID;
	public long ping;
	public List<String> ignoreList = new ArrayList<String>();
	public static HashMap<String, Class<?>> PacketsIDToClass = new HashMap<>();
	public static HashMap<Class<?>, String> PacketsClassToID = new HashMap<>();
	
	public Packet() {
		this.packetID = PacketsClassToID.get(getClass());
	}

	public final void send(PrintWriter out) {
		try {
			out.println(packetID);
			out.println((int)System.currentTimeMillis());
			WritePacketData(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final void receive(BufferedReader in) throws IOException {
		int ping = Integer.valueOf(in.readLine());
		ping = (int)System.currentTimeMillis() - ping;
		readPacketData(in);
	}

	public void WritePacketData(PrintWriter out) throws IOException {}
	public void readPacketData(BufferedReader in) throws IOException {}

	public void receivedOnServer(TuonelaServer tuonelaServer) {}
	public void receivedOnClient(Tuonela tuonela) {}
	
	static void registerPacket(String ID, Class<?> packet) {
		if(PacketsIDToClass.containsKey(packet)) {
			throw new IllegalArgumentException("duplicate packet: "+packet);
		}
		if(PacketsClassToID.containsKey(ID)) {
			throw new IllegalArgumentException("duplicate ID: "+ID);
		}
		PacketsIDToClass.put(ID, packet);
		PacketsClassToID.put(packet, ID);
	}
	
	static {
		registerPacket("world", PacketWorld.class);
		registerPacket("tile", PacketTile.class);
		registerPacket("entityCreation", PacketEntityCreation.class);
		registerPacket("entityRemoval", PacketEntityRemoval.class);
		registerPacket("entityPosition", PacketEntityPDU.class);
	}

}

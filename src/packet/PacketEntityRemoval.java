package packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import core.Tuonela;

import entity.Entity;

public class PacketEntityRemoval extends Packet {

	public int entityID;

	public PacketEntityRemoval() {
	}

	public PacketEntityRemoval(Entity entity) {
		this.entityID = entity.getEntityID();
	}

	@Override
	public void WritePacketData(PrintWriter out) throws IOException {
		out.println(entityID);
	}

	@Override
	public void readPacketData(BufferedReader in) throws IOException {
		entityID = Integer.valueOf(in.readLine());
	}

	@Override
	public void receivedOnClient(Tuonela tuonela) {
		tuonela.world.removedEntities.add(tuonela.world.livingEntities.get(entityID));
	}

}

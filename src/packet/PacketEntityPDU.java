package packet;

import helper.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import core.Tuonela;
import core.TuonelaServer;
import entity.Entity;

public class PacketEntityPDU extends Packet {
	
	public int entityID;
	public Entity entity;
	public float posX;
	public float posY;
	public float velX;
	public float velY;

	public PacketEntityPDU() {
	}
	
	public PacketEntityPDU(Entity entity) {
		this.entity = entity;
		entityID = entity.getEntityID();
		posX = entity.getPosX();
		posY = entity.getPosY();
		velX = entity.getVelX();
		velY = entity.getVelY();
	}
	
	@Override
	public void WritePacketData(PrintWriter out) throws IOException {
		out.println(entityID);
		out.println((int)posX);
		out.println((int)posY);
		out.println((int)velX);
		out.println((int)velY);
	}
	
	@Override
	public void readPacketData(BufferedReader in) throws IOException {
		entityID = Integer.parseInt(in.readLine());
		posX = Integer.parseInt(in.readLine());
		posY = Integer.parseInt(in.readLine());
		velX = Integer.parseInt(in.readLine());
		velY = Integer.parseInt(in.readLine());
	}

	@Override
	public void receivedOnServer(TuonelaServer tuonelaServer) {
		tuonelaServer.communicator.sendPacket(this);
	}

	@Override
	public void receivedOnClient(Tuonela tuonela) {
		Entity entity = tuonela.world.livingEntities.get(entityID);
		if(tuonela.getLocalPlayer() == entity)
			return;
		//Logger.log(posX + " " + entityID);
		//entity.setPosX((1-ping)*posX + entity.getPosX());
		//entity.setPosY((1-ping)*posY + entity.getPosY());
		entity.setPosX(posX);
		entity.setPosY(posY);
		entity.setVelX(velX);
		entity.setVelY(velY);
	}

}

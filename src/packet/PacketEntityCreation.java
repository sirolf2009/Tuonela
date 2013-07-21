package packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import render.RenderEntity;

import world.World;

import core.Tuonela;

import entity.Entity;
import entity.EntityPlayer;

public class PacketEntityCreation extends Packet {

	public int entityID;
	public int registerID;
	public float posX;
	public float posY;
	public float velX;
	public float velY;
	
	public PacketEntityCreation() {
	}

	public PacketEntityCreation(Entity entity) {
		this.entityID = entity.getEntityID();
		this.registerID = Entity.registeredEntitiesClassToID.get(entity.getClass());
		this.posX = entity.getPosX();
		this.posY = entity.getPosY();
	}
	
	@Override
	public void WritePacketData(PrintWriter out) throws IOException {
		out.println(entityID);
		out.println(registerID);
		out.println((int)posX+"");
		out.println((int)posY+"");
	}
	
	@Override
	public void readPacketData(BufferedReader in) throws IOException {
		entityID = Integer.valueOf(in.readLine());
		registerID = Integer.valueOf(in.readLine());
		posX = Integer.valueOf(in.readLine());
		posY = Integer.valueOf(in.readLine());
		System.out.printf("entity creation pars: entityID: %d, registerID: %d, posX: %f, posY: %f\n", entityID, registerID, posX, posY);
	}
	
	@Override
	public void receivedOnClient(Tuonela tuonela) {
		try {
			Class<?> entityClass = Entity.registeredEntitiesIDToClass.get(registerID);
			Constructor<?> constructor = entityClass.getDeclaredConstructor(World.class, float.class, float.class);
			Entity entity = (Entity) constructor.newInstance(tuonela.world, posX, posY);
			entity.setRenderer(new RenderEntity(entity));
			tuonela.world.addEntityToWorld(entity, entityID);
			if(entity.getEntityID() == tuonela.getPlayerID())
				((EntityPlayer)entity).username = tuonela.username;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}

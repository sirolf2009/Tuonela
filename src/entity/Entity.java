package entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import render.RenderEntity;

import world.World;

public class Entity {

	protected float posX;
	protected float posY;
	protected float velX;
	protected float velY;
	protected float lastPosX;
	protected float lastPosY;
	protected float lastVelX;
	protected float lastVelY;
	public int PDURate = 8;
	public int PDUTimer = 0;
	protected World world;
	protected Random rand;
	protected int EntityID;
	protected RenderEntity renderer;
	protected String texture = "/sprites/mobs/assassin.png";
	public static Map<Integer, Class<?>> registeredEntitiesIDToClass = new HashMap<>();
	public static Map<Class<?>, Integer> registeredEntitiesClassToID = new HashMap<>();

	public Entity() {
	}

	public Entity(World world, float posX, float posY) {
		this.world = world;
		this.posX = posX;
		this.posY = posY;
		rand = new Random();
	}

	public void update(long deltaTime) {
		if(velX!=0 || velY!=0) {
			lastPosX = posX;
			lastPosY = posY;
			posX += velX * deltaTime/10;
			posY += velY * deltaTime/10;
		}
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public int getEntityID() {
		return EntityID;
	}

	public void setEntityID(int entityID) {
		EntityID = entityID;
	}

	public RenderEntity getRenderer() {
		return renderer;
	}

	public void setRenderer(RenderEntity renderer) {
		this.renderer = renderer;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public static void registerEntity(int registerID, Class<?> entity) {
		if(registeredEntitiesClassToID.containsKey(entity)) {
			throw new IllegalArgumentException("duplicate entity: "+entity);
		}
		if(registeredEntitiesClassToID.containsKey(registerID)) {
			throw new IllegalArgumentException("duplicate ID: "+registerID);
		}
		registeredEntitiesClassToID.put(entity, registerID);
		registeredEntitiesIDToClass.put(registerID, entity);
	}

	static {
		registerEntity(0, EntityPlayer.class);
		registerEntity(1, EntityGuard.class);
		registerEntity(2, EntityBoar.class);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public float getLastPosX() {
		return lastPosX;
	}

	public void setLastPosX(float lastPosX) {
		this.lastPosX = lastPosX;
	}

	public float getLastPosY() {
		return lastPosY;
	}

	public void setLastPosY(float lastPosY) {
		this.lastPosY = lastPosY;
	}
}

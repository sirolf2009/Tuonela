package entity;

import java.io.Serializable;
import java.util.Random;

import render.RenderEntity;

import world.World;
import world.tile.TileType;

public class Entity implements Serializable {
	
	/**
	 * i really have no fucking clue what serialVersionUID is
	 */
	private static final long serialVersionUID = 9127667073414844591L;
	protected float posX;
	protected float posY;
	protected float velX;
	protected float velY;
	protected World world;
	protected Random rand;
	protected int EntityID;
	protected RenderEntity renderer;
	protected String texture = "/assassin.png";
	
	public Entity(World world, float posX, float posY) {
		this.world = world;
		this.posX = posX;
		this.posY = posY;
		rand = new Random();
	}
	
	public void update(long deltaTime) {
		//System.out.println(velX + ", " + velY);
		posX += velX * deltaTime/10;
		posY += velY * deltaTime/10;
		if(world != null && world.getTileAt(posX, posY) != null && world.getTileAt(posX, posY).type == TileType.STONE) {
			//posX -= velX * deltaTime/10;
			//posY -= velY * deltaTime/10;
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
}

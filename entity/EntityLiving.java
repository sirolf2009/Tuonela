package entity;

import java.awt.Point;
import world.World;

public class EntityLiving extends Entity {

	private static final long serialVersionUID = -1440973314306207171L;
	protected int health;
	protected int damage;
	protected float speed;
	protected Point destination;

	public EntityLiving(World world, float posX, float posY) {
		super(world, posX, posY);
		health = 100;
		speed = 4;
	}

	

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}

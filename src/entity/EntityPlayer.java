package entity;

import world.World;

public class EntityPlayer extends EntityLiving {

	private static final long serialVersionUID = -2066650686315813011L;
	public String helmet, chestplate, leggings, boots;
	public String username;

	public EntityPlayer(World world, float posX, float posY) {
		super(world, posX, posY);
		speed=2.2F;
		texture="res/sprites/mobs/Character.xml";
	}

	public void update(long deltaTime) {
		super.update(deltaTime);
	}
}

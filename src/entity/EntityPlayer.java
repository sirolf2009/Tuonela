package entity;

import world.World;

public class EntityPlayer extends EntityLiving {

	public String helmet, chestplate, leggings, boots;
	public String username;

	public EntityPlayer(World world, float posX, float posY) {
		super(world, posX, posY);
		PDURate = 0;
		speed=2.2F;
		texture="/sprites/mobs/Character.xml";
		texture="/sprites/mobs/assassin.png";
	}

	@Override
	public void update(long deltaTime) {
		super.update(deltaTime);
	}
}

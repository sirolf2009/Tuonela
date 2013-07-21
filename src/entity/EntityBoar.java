package entity;

import world.World;

public class EntityBoar extends EntityAnimal {

	public EntityBoar(World world, float posX, float posY) {
		super(world, posX, posY);
		texture = "/sprites/mobs/Boar.png";
	}

}

package world.area;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import world.World;
import entity.Entity;

public class AreaSpawn extends Area {

	public int registerID;
	public int chanceToSpawn;

	public AreaSpawn(World world, int posX1, int posY1, int posX2, int posY2, int registerID) {
		super(world, posX1, posY1, posX2, posY2);
		this.registerID = registerID;
		this.chanceToSpawn = 0;
	}
	
	public AreaSpawn(World world, int posX1, int posY1, int posX2, int posY2, int registerID, int chanceToSpawn) {
		super(world, posX1, posY1, posX2, posY2);
		this.registerID = registerID;
		this.chanceToSpawn = chanceToSpawn;
	}

	public Entity spawnEntity() {
		try {
			Class<?> entityClass = Entity.registeredEntitiesIDToClass.get(registerID);
			Constructor<?> constructor = entityClass.getDeclaredConstructor(World.class, float.class, float.class);
			Entity entity = (Entity) constructor.newInstance(world, getRandomX(), getRandomY());
			world.addEntityToWorld(entity);
			return entity;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

}

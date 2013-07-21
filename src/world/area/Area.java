package world.area;

import java.util.Random;

import world.World;

public class Area {
	
	public int posX1;
	public int posY1;
	public int posX2;
	public int posY2;
	public World world;
	private Random rand = new Random();

	public Area(World world, int posX1, int posY1, int posX2, int posY2) {
		this.world = world;
		this.posX1 = posX1;
		this.posY1 = posY1;
		this.posX2 = posX2;
		this.posY2 = posY2;
	}
	
	public int getRandomX() {
		return rand.nextInt(posX2 - posX1)+posX1;
	}
	
	public int getRandomY() {
		return rand.nextInt(posY2 - posY1)+posY1;
	}

}

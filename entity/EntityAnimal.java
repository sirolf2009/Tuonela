package entity;

import java.awt.Point;
import world.World;
import world.tile.TileType;

public class EntityAnimal extends EntityLiving {

	private static final long serialVersionUID = -4388406939632218239L;
	private float speed;
	private Point destination;

	public EntityAnimal(World world, float posX, float posY) {
		super(world, posX, posY);
		health = 100;
		speed = 2;
	}

	public void update(long deltaTime) {
		super.update(deltaTime);
		if(destination != null && new Point((int)getPosX(), (int)getPosY()).distance(destination) <= World.TILE_HEIGHT)
			destination = null;
	}

	public void wander() {
		if(destination == null && rand.nextInt(100) == 0) {
			int range = 24;
			int relX = (int) (rand.nextInt(range)*World.TILE_WIDTH-0.5*range*World.TILE_WIDTH);
			int relY = (int) (rand.nextInt(range)*World.TILE_HEIGHT-0.5*range*World.TILE_HEIGHT);
			if(world.getTileAt(posX+relX, posY+relY).type == TileType.STONE)
				wander();
			setDestination((int)posX + relX, (int)posY + relY);
		}
	}

	public void setDestination(int x, int y) {
		destination = new Point();
		destination.x = x;
		destination.y = y;
	}
	
	public double calcAngleBetweenPoints(Point p1, Point p2) {
        return Math.toDegrees(Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX()));
    }
	
	public Point getVelocity(double angle, double speed){
        double x = Math.cos(Math.toRadians(angle))*speed;
        double y = Math.sin(Math.toRadians(angle))*speed;
        return new Point((int)x, (int)y);
    }


	public void walkToDestination(long deltaTime) {
		if(destination != null) {
			Point me = new Point((int)posX, (int)posY);
			double angle = calcAngleBetweenPoints(me, destination);
			Point velocity = getVelocity(angle, speed);
			setVelX(velocity.x);
			setVelY(velocity.y);
		}
	}

}

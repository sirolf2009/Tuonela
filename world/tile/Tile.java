package world.tile;

import java.io.Serializable;

public class Tile implements Serializable {

	private static final long serialVersionUID = -3931924611057249115L;
	public TileType type;
	public int x, y;
	public int width, height;
	
	public Tile(TileType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

}

package world.tile;

import java.util.HashMap;
import java.util.Map;

public enum TileType {
	GRASS(1), DIRT(2), SCORCHED_EARTH(3), STONE(4), ROAD(5), FOREST(6), WATER(7);
	
	protected final int ID;
	protected static Map<Integer, TileType> enumMap = new HashMap<Integer, TileType>();

	TileType(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}
	
	public static TileType getEnumByID(int ID) {
		return enumMap.get(ID);
	}
	
	static {
		enumMap.put(TileType.GRASS.ID, TileType.GRASS);
		enumMap.put(TileType.DIRT.ID, TileType.DIRT);
		enumMap.put(TileType.SCORCHED_EARTH.ID, TileType.SCORCHED_EARTH);
		enumMap.put(TileType.STONE.ID, TileType.STONE);
		enumMap.put(TileType.ROAD.ID, TileType.ROAD);
		enumMap.put(TileType.FOREST.ID, TileType.FOREST);
		enumMap.put(TileType.WATER.ID, TileType.WATER);
	}
}

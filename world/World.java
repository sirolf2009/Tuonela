package world;

import helper.Reference;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import world.tile.Tile;
import world.tile.TileType;

import entity.Entity;
import entity.EntityAnimal;

public class World implements Serializable {

	private static final long serialVersionUID = 6693949670516222844L;
	public Map<Integer, Entity> livingEntities;
	public Tile[][] tiles;
	public int levelWidth;
	public int levelHeight;
	public transient BufferedImage tilesMap;
	public static int TILE_WIDTH;
	public static int TILE_HEIGHT;

	public World(String tilesMap) {
		TILE_WIDTH = 32;
		TILE_HEIGHT = 32;
		livingEntities = new HashMap<Integer, Entity>();
		try {
			this.tilesMap = ImageIO.read(new File(getClass().getClassLoader().getResource(tilesMap).toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		levelWidth = this.tilesMap.getWidth();
		levelHeight = this.tilesMap.getHeight();
		generateTiles();
	}

	public World(BufferedImage tilesMap) {
		TILE_WIDTH = 32;
		TILE_HEIGHT = 32;
		livingEntities = new HashMap<Integer, Entity>();
		this.tilesMap = tilesMap;
		levelWidth = tilesMap.getWidth();
		levelHeight = tilesMap.getHeight();
		generateTiles();
	}

	private void generateTiles() {
		levelWidth = tilesMap.getWidth();
		levelHeight = tilesMap.getHeight();
		tiles = new Tile[levelWidth][levelHeight];
		for(int x = 1; x < levelWidth; x++)
			for(int y = 1; y < levelWidth; y++) {
				int color = tilesMap.getRGB(x, y);
				if(color == Reference.MAP_STONE)
					tiles[x][y] = new Tile(TileType.STONE, x, y);
				else if(color == Reference.MAP_WATER)
					tiles[x][y] = new Tile(TileType.WATER, x, y);
				else if(color == Reference.MAP_GRASS)
					tiles[x][y] = new Tile(TileType.WATER, x, y);
				else {
					System.out.println("the tile at "+x+","+y+" with color "+color+" has not been defined!");
					tiles[x][y] = new Tile(TileType.STONE, x, y);
				}
			}
	}

	public Tile getTileAt(float posX, float posY) {
		int tileXPos = (int) (Math.floor(posX)/TILE_WIDTH);
		int tileYPos = (int) (Math.floor(posY)/TILE_HEIGHT);
		return tiles[tileXPos][tileYPos];
	}

	public synchronized void update(long deltaTime) {
		Iterator<Entity> itr = livingEntities.values().iterator();
		List<Entity> deadEntities = new ArrayList<Entity>();
		while(itr.hasNext()) {
			Entity entity = itr.next();
			entity.update(deltaTime);
			entity.getRenderer().update(deltaTime);
			if(entity instanceof EntityAnimal && ((EntityAnimal)entity).getHealth()==0)
				deadEntities.add(entity);
		}
		itr = deadEntities.iterator();
		while(itr.hasNext()) {
			livingEntities.remove(itr.next());
		}
		deadEntities.clear();
	}

	public void renderEntities(Graphics2D g) {
		Iterator<Entity> itr = livingEntities.values().iterator();
		while(itr.hasNext()) {
			itr.next().getRenderer().draw(g);
		}
	}

	public synchronized void addEntityToWorld(Entity entity) {
		entity.setEntityID(findNextEntityID());
		livingEntities.put(findNextEntityID(), entity);

	}

	public int findNextEntityID() {
		int lowestID = 0;
		boolean occupied= false;
		while(true) {
			lowestID++;
			Iterator<Integer> itr = livingEntities.keySet().iterator();
			while(itr.hasNext())
				if(lowestID == itr.next())
					occupied = true;
			if(!occupied)
				break;
		}
		return lowestID;
	}

	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}
}

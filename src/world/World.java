package world;

import helper.Reference;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import packet.Packet;
import packet.PacketEntityCreation;
import packet.PacketEntityPDU;
import render.RenderEntity;
import world.area.AreaSpawn;
import world.tile.Tile;
import world.tile.TileType;
import core.Tuonela;
import core.TuonelaServer;
import entity.Entity;
import entity.EntityAnimal;
import entity.EntityGuard;

public class World {

	public Map<Integer, Entity> livingEntities;
	public List<Entity> removedEntities = new ArrayList<Entity>();
	public Tile[][] tiles;
	public int levelWidth;
	public int levelHeight;
	public BufferedImage tilesMap;
	public Map<Integer, List<AreaSpawn>> spawnAreas;
	public static int TILE_WIDTH;
	public static int TILE_HEIGHT;
	private Random rand;
	public boolean isRemote = false;

	public World(String tilesMap) {
		init();
		try {
			this.tilesMap = ImageIO.read(new File(getClass().getClassLoader().getResource(tilesMap).toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		generateTiles();
	}

	public World(BufferedImage tilesMap) {
		init();
		this.tilesMap = tilesMap;
		generateTiles();
	}

	public World(int levelWidth, int levelHeight) {
		init();
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		tiles = new Tile[levelWidth][levelHeight];
	}

	public void init() {
		TILE_WIDTH = 32;
		TILE_HEIGHT = 32;
		rand = new Random();
		livingEntities = new HashMap<Integer, Entity>();
		spawnAreas = new HashMap<Integer, List<AreaSpawn>>();
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
					tiles[x][y] = new Tile(TileType.GRASS, x, y);
				else {
					System.out.println("the tile at "+x+","+y+" with color "+color+" has not been defined!");
					tiles[x][y] = new Tile(TileType.STONE, x, y);
				}
			}
	}

	public Tile getTileAt(float posX, float posY) {
		int tileXPos = (int) (Math.floor(posX)/TILE_WIDTH);
		int tileYPos = (int) (Math.floor(posY)/TILE_HEIGHT);
		if(tileXPos <= TILE_WIDTH && tileYPos <= TILE_HEIGHT && tileXPos >=0 && tileYPos >= 0)
			return tiles[tileXPos][tileYPos];
		return null;
	}

	public synchronized void update(long deltaTime) {
		if(isRemote) {
			List<AreaSpawn> spawnAreasList = spawnAreas.get(Entity.registeredEntitiesClassToID.get(EntityGuard.class));
			Iterator<AreaSpawn> itrAreaSpawn = spawnAreasList.iterator();
			while(itrAreaSpawn.hasNext()) {
				AreaSpawn area = itrAreaSpawn.next();
				if(rand.nextInt(area.chanceToSpawn)==0) {
					Entity entity = area.spawnEntity();
					entity.setRenderer(new RenderEntity(entity));
					PacketEntityCreation packet = new PacketEntityCreation(entity);
					TuonelaServer.instance.communicator.sendPacket(packet);
				}
			}
		}
		Iterator<Entity> itrEntity = livingEntities.values().iterator();
		while(itrEntity.hasNext()) {
			Entity entity = itrEntity.next();
			entity.update(deltaTime);
			entity.getRenderer().update(deltaTime);
			if(entity.PDUTimer == 0) {
				entity.PDUTimer = entity.PDURate;
				entity.setLastPosX(entity.getPosX());
				entity.setLastPosY(entity.getPosY());
				if(isRemote) {
					TuonelaServer.instance.communicator.sendPacket(new PacketEntityPDU(entity));
				} else {
					Packet packet = new PacketEntityPDU(entity);
					packet.ignoreList.add(Tuonela.instance.username);
					Tuonela.instance.communicator.sendPacket(packet);
				}
			}
			entity.PDUTimer--;
			if(entity instanceof EntityAnimal && ((EntityAnimal)entity).getHealth()==0)
				removedEntities.add(entity);
		}
		itrEntity = removedEntities.iterator();
		while(itrEntity.hasNext()) {
			livingEntities.remove(itrEntity.next());
		}
		removedEntities.clear();
	}

	public void setWorldAreas(String file) {
		try {
			setWorldAreas(new File(getClass().getClassLoader().getResource(file).toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void setWorldAreas(File file) {
		try {
			BufferedReader rdr = new BufferedReader(new FileReader(file));
			String line;
			while((line = rdr.readLine()) != null) {
				String[] command = line.split(" ");
				if(command[0].equals("spawn_area")) {
					int entityID = Integer.valueOf(command[5]);
					AreaSpawn area;
					if(command.length==7)
						area = new AreaSpawn(this, Integer.valueOf(command[1])*World.TILE_WIDTH, Integer.valueOf(command[2])*World.TILE_HEIGHT, Integer.valueOf(command[3])*World.TILE_WIDTH, Integer.valueOf(command[4])*World.TILE_HEIGHT, entityID, Integer.valueOf(command[6]));
					else
						area = new AreaSpawn(this, Integer.valueOf(command[1])*World.TILE_WIDTH, Integer.valueOf(command[2])*World.TILE_HEIGHT, Integer.valueOf(command[3])*World.TILE_WIDTH, Integer.valueOf(command[4])*World.TILE_HEIGHT, entityID);
					if(spawnAreas.get(entityID) == null) {
						spawnAreas.put(entityID, new ArrayList<AreaSpawn>());
					}
					spawnAreas.get(entityID).add(area);
				}
			}
			rdr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void renderEntities(Graphics2D g) {
		Iterator<Entity> itr = livingEntities.values().iterator();
		while(itr.hasNext()) {
			itr.next().getRenderer().draw(g);
		}
	}

	public synchronized void addEntityToWorld(Entity entity) {
		addEntityToWorld(entity, findNextEntityID());
	}

	public synchronized void addEntityToWorld(Entity entity, int entityID) {
		entity.setEntityID(findNextEntityID());
		livingEntities.put(findNextEntityID(), entity);
	}
	
	public synchronized void removeEntityFromWorld(Entity entity) {
		removedEntities.add(entity);
	}

	public int findNextEntityID() {
		int lowestID = 0;
		while(true) {
			if(livingEntities.get(lowestID)==null) {
				return lowestID;
			}
			lowestID++;
		}
	}

	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}
}

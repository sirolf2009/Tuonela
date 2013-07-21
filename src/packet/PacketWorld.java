package packet;

import helper.LogType;
import helper.Logger;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.GameState;
import core.Tuonela;
import entity.Entity;
import world.World;

public class PacketWorld extends Packet {

	public BufferedImage tilesMap;
	public List<PacketEntityCreation> entityCreationPackets = new ArrayList<>();
	public List<PacketTile> tilePackets = new ArrayList<>();
	public int playerID;
	public int tileWidth;
	public int tileHeight;
	public int levelWidth;
	public int levelHeight;
	public int entities;
	public int tiles;

	public PacketWorld() {
	}

	public PacketWorld(World world, int playerID) {
		this.tilesMap = world.tilesMap;
		this.playerID = playerID;
		this.tileWidth = World.TILE_WIDTH;
		this.tileHeight = World.TILE_HEIGHT;
		this.levelWidth = world.levelWidth;
		this.levelHeight = world.levelHeight;
		this.entities = world.livingEntities.size();
		Iterator<Entity> itr = world.livingEntities.values().iterator();
		while(itr.hasNext()) {
			entityCreationPackets.add(new PacketEntityCreation(itr.next()));
		}
		for(int x = 1; x < world.levelWidth; x++) {
			for(int y = 1; y < world.levelHeight; y++) {
				tilePackets.add(new PacketTile(world.tiles[x][y]));
			}
		}
	}

	@Override
	public void WritePacketData(PrintWriter out) throws IOException {
		out.println(playerID);
		out.println(tileWidth);
		out.println(tileHeight);
		out.println(levelWidth);
		out.println(levelHeight);
		out.println(entityCreationPackets.size());
		out.println(tilePackets.size());
		Iterator<PacketEntityCreation> itr = entityCreationPackets.iterator();
		while(itr.hasNext())
			itr.next().send(out);
		Iterator<PacketTile> itr2 = tilePackets.iterator();
		while(itr2.hasNext())
			itr2.next().send(out);
	}

	@Override
	public void readPacketData(BufferedReader in) throws IOException {
		playerID = Integer.valueOf(in.readLine());
		tileWidth = Integer.valueOf(in.readLine());
		tileHeight = Integer.valueOf(in.readLine());
		levelWidth = Integer.valueOf(in.readLine());
		levelHeight = Integer.valueOf(in.readLine());
		entities = Integer.valueOf(in.readLine());
		tiles = Integer.valueOf(in.readLine());
		Logger.logf(LogType.LOG_PACKET_VARS, "world vars: player ID %d, tileWidth %d, tileHeight %d, entities %d, tiles %d\n", playerID, tileWidth, tileHeight, entities, tiles);
		for(int i = 0; i < entities; i++) {
			try {
				String packetID = in.readLine();
				if(packetID.isEmpty())
					continue;
				PacketEntityCreation packet = (PacketEntityCreation) Packet.PacketsIDToClass.get(packetID).newInstance();
				packet.receive(in);
				entityCreationPackets.add(packet);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		for(int i = 0; i < tiles; i++) {
			try {
				String packetID = in.readLine();
				if(packetID.isEmpty())
					continue;
				PacketTile packet = (PacketTile) PacketTile.class.newInstance();
				packet.receive(in);
				tilePackets.add(packet);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void receivedOnClient(Tuonela tuonela) {
		World.TILE_WIDTH = tileWidth;
		World.TILE_HEIGHT = tileHeight;
		tuonela.world = new World(levelWidth, levelHeight);
		Iterator<PacketEntityCreation> itr = entityCreationPackets.iterator();
		while(itr.hasNext())
			itr.next().receivedOnClient(tuonela);
		Iterator<PacketTile> itr2 = tilePackets.iterator();
		while(itr2.hasNext()) {
			itr2.next().receivedOnClient(tuonela);
		}
		tuonela.setPlayerID(playerID);
		Logger.log(LogType.GENERAL, "Player joined with ID "+playerID);
		while(tuonela.world.tiles[levelWidth-1][levelHeight-1] == null){}
		Tuonela.gameState = GameState.PLAYING;
	}

}

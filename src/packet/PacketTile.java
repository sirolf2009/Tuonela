package packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import core.Tuonela;

import world.tile.Tile;
import world.tile.TileType;

public class PacketTile extends Packet {
	
	public TileType tileType;
	public int x, y;
	
	public PacketTile() {
	}
	
	public PacketTile(Tile tile) {
		tileType = tile.type;
		x = tile.x;
		y = tile.y;
	}

	public void WritePacketData(PrintWriter out) throws IOException {
		out.println(tileType.getID());
		out.println(x);
		out.println(y);
	}
	

	public void readPacketData(BufferedReader in) throws IOException {
		tileType = TileType.getEnumByID(Integer.valueOf(in.readLine()));
		x = Integer.valueOf(in.readLine());
		y = Integer.valueOf(in.readLine());
	}
	

	public void receivedOnClient(Tuonela tuonela) {
		tuonela.world.tiles[x][y] = new Tile(tileType, x, y);
	}

}

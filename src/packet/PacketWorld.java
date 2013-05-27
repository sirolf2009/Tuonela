package packet;

import java.util.Iterator;

import render.RenderEntity;
import render.sprite.Sprite;

import core.GameState;
import core.Tuonela;
import entity.Entity;
import world.World;

public class PacketWorld extends Packet {

	private static final long serialVersionUID = 3619460014036292532L;

	World world;
	int playerID;
	
	public PacketWorld(World world, int playerID) {
		super("world");
		this.world = world;
		this.playerID = playerID;
	}
	
	public void receivedOnClient(Tuonela tuonela) {
		tuonela.world = world;
		tuonela.playerID = playerID;
		Iterator<Entity> itr = world.livingEntities.values().iterator();
		while(itr.hasNext()) {
			RenderEntity renderer = itr.next().getRenderer();
			if(renderer.img.endsWith("xml"))
				renderer.setAnimationsFromFile(renderer.img);
			else
				renderer.spriteDefault = new Sprite(renderer.entity.getTexture());
			renderer.update(0);
			System.out.println(renderer.sprite);
		}
		System.out.println(world+" confirmed with player id "+playerID);
		tuonela.gameState = GameState.PLAYING;
	}

}

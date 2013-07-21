package render;

import helper.ParserXML;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.w3c.dom.NodeList;

import render.sprite.Animation;
import render.sprite.Sprite;
import render.sprite.SpriteAnimated;
import screen.ScreenManager;
import world.World;
import world.tile.Tile;

public class RenderManager {

	public List<RenderEntity> entityRenderers = new ArrayList<RenderEntity>();
	public List<SpriteAnimated> armor = new ArrayList<SpriteAnimated>();
	public Sprite dirt, stone, water, grass;
	public Sprite background;
	public final String tileSheet = "/sprites/world/tilemap.png";
	private static RenderManager instance;

	public void init() {
		dirt = new Sprite();
		dirt.setImage(tileSheet, 0, 0, 32, 32);
		stone = new Sprite();
		stone.setImage(tileSheet, 32, 0, 32, 32);
		water = new Sprite();
		water.setImage(tileSheet, 64, 0, 32, 32);
		grass = new Sprite();
		grass.setImage(tileSheet, 96, 0, 32, 32);
		background = new Sprite();
		background.setImage("/sprites/world/background.jpg");
	}

	public Animation createAnimation(int speed, String... strings) {
		Animation anim = new Animation(speed);
		for(String string : strings)
			try {
				BufferedImage image = ImageIO.read(new File(getClass().getClassLoader().getResource(string).toURI()));
				if(image != null)
					anim.addScene(image);
				else {
					System.err.println("can not find: "+string);
					System.exit(-1);
				}
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		return anim;
	}

	public Animation createAnimation(String folder, int speed) {
		try {
			Animation anim = new Animation(speed);
			File dir = new File(getClass().getClassLoader().getResource(folder).toURI());
			for(File file : dir.listFiles()) {
				try {
					anim.addScene(ImageIO.read(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return anim;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates an animation from a spritesheet
	 * @param the file
	 * @param the speed
	 * @param the x origin of the first sprite
	 * @param the y origin of the first sprite
	 * @param the width of every sprite
	 * @param the height of every sprite
	 * @param the amount of sprites per row
	 * @param the amount of rows
	 * @return the animation
	 */
	public static Animation createAnimation(BufferedImage file, int speed, int originX, int originY, int width, int height, int spritesPerRow, int rows) {
		Animation anim = new Animation(speed);
		for(int row = 0; row < rows; row++)
			for(int sprite = 0; sprite < spritesPerRow; sprite++) {
				BufferedImage img = file.getSubimage(originX+width*sprite, originY+height*row, width, height);
				anim.addScene(img);
				img.flush();
			}
		return anim;
	}

	public Animation createAnimation(NodeList nodes, String attribute, BufferedImage spriteSheet, int speed) {
		int width = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "width"));
		int height = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "height"));
		int spritesPerRow = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "spritesPerRow"));
		int rows = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "rows"));
		NodeList element = ParserXML.retrieveValueFromNodeWithAttribute(nodes, "direction", attribute).getChildNodes();
		int originX = Integer.parseInt(ParserXML.retrieveValueFromNodeName(element, "originX"));
		int originY = Integer.parseInt(ParserXML.retrieveValueFromNodeName(element, "originY"));
		return createAnimation(spriteSheet, speed, originX, originY, width, height, spritesPerRow, rows);
	}

	public void update(long deltaTime, ScreenManager screenManager) {
		Iterator<RenderEntity> itr = entityRenderers.iterator();
		while(itr.hasNext())
			itr.next().update(deltaTime);
	}

	public void renderBackground(Graphics2D g, Tile[][] tiles, int width, int height, float offsetX, float offsetY, float screenX, float screenY) {
		g.drawImage(background.image, (int)offsetX, (int)offsetY, (int)screenX, (int)screenY, null);
		int startX = (int) (offsetX / World.TILE_WIDTH); // the amount of tiles that are left of the screen
		int startY = (int) (offsetY / World.TILE_HEIGHT); // the amount of tiles that are above of the screen
		int endX = (int) ((offsetX + screenX) / World.TILE_WIDTH + 1); // the amount of tiles that are in the screen + 1
		int endY = (int) ((offsetY + screenY) / World.TILE_HEIGHT + 1); // the amount of tiles that are in the screen + 1
		if(startX < 1)
			startX = 1;
		if(startY < 1)
			startY = 1;
		if(endX > width)
			endX = width;
		if(endY > height)
			endY = height;

		for(int y = startY; y < endY; y++) {
			for(int x = startX; x < endX; x++) {
				if(tiles[x][y] != null)
					switch(tiles[y][x].type) {
					case STONE:
						g.drawImage(stone.image, x*World.TILE_WIDTH, y*World.TILE_HEIGHT, null);
						break;
					case GRASS:
						g.drawImage(grass.image, x*World.TILE_WIDTH, y*World.TILE_HEIGHT, null);
						break;
					case WATER:
						g.drawImage(water.image, x*World.TILE_WIDTH, y*World.TILE_HEIGHT, null);
						break;
					default:
						break;
					}
			}
		}
	}
	
	public static RenderManager getInstance() {
		return instance == null ? (instance = new RenderManager()) : instance;
	}

	public void renderEntities(Graphics2D g) {
		Iterator<RenderEntity> itr = entityRenderers.iterator();
		while(itr.hasNext())
			itr.next().draw(g);
	}

}

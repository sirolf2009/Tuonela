package render;

import helper.Logger;
import helper.ParserXML;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import core.Tuonela;

import render.sprite.Animation;
import render.sprite.Sprite;
import render.sprite.SpriteAnimated;

import entity.Entity;
import entity.EntityPlayer;

public class RenderEntity {

	public Entity entity;
	public Sprite sprite;
	public Sprite spriteWalkingNorth, spriteWalkingSouth, spriteWalkingWest, spriteWalkingEast;
	public Sprite spriteDefault;
	public String img;

	public RenderEntity(Entity entity) {
		this.entity = entity;
		this.img = entity.getTexture();
		if(entity.getTexture().endsWith("xml")) {
			setAnimationsFromFile(entity.getTexture());
		} else {
			spriteDefault = new Sprite(entity.getTexture());
		}
		sprite = spriteDefault;
	}

	public void draw(Graphics2D g) {
		sprite.x = (int) entity.getPosX();
		sprite.y = (int) entity.getPosY();
		sprite.draw(g);
		g.drawString(entity.getEntityID()+(entity instanceof EntityPlayer ? ":"+((EntityPlayer)entity).username:""), sprite.x, sprite.y);
	}

	public void update(long deltaTime) {
		if(entity.getVelY() < 0 && spriteWalkingNorth != null)
			sprite = spriteWalkingNorth;
		else if(entity.getVelY() > 0 && spriteWalkingSouth != null)
			sprite = spriteWalkingSouth;
		else if(entity.getVelX() < 0 && spriteWalkingWest != null)
			sprite = spriteWalkingWest;
		else if(entity.getVelX() > 0 && spriteWalkingEast != null)
			sprite = spriteWalkingEast;
		else 
			sprite = spriteDefault;
	}

	public void setAnimationsFromFile(String path) {
		try {
			Document doc = ParserXML.parse(path);
			NodeList nodesFile = ParserXML.getNodeListByName(doc, "file");
			NodeList nodesDefault = ParserXML.getNodeListByName(doc, "default");
			NodeList nodesRunning = ParserXML.getNodeListByName(doc, "running");
			BufferedImage spriteSheet = ImageIO.read(new File(getClass().getResource(ParserXML.retrieveValueFromNodeName(nodesFile, "file")).toURI()));

			Animation defaultAnim = createNonDirectionalAnimation(nodesDefault, spriteSheet);
			spriteDefault = new SpriteAnimated(defaultAnim);
			
			String direction = "NORTH";
			Animation anim;
			switch(direction) {
			case "NORTH": 
				anim = createDirectionalAnimation(nodesRunning, "NORTH", spriteSheet);
				spriteWalkingNorth = new SpriteAnimated(anim);
			case "EAST": 
				anim = createDirectionalAnimation(nodesRunning, "EAST", spriteSheet);
				spriteWalkingEast = new SpriteAnimated(anim);
			case "SOUTH": 
				anim = createDirectionalAnimation(nodesRunning, "SOUTH", spriteSheet);
				spriteWalkingSouth = new SpriteAnimated(anim);
			case "WEST": 
				anim = createDirectionalAnimation(nodesRunning, "WEST", spriteSheet);
				spriteWalkingWest = new SpriteAnimated(anim);
				break;
			}
		} catch (DOMException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public Animation createDirectionalAnimation(NodeList nodes, String attribute, BufferedImage spriteSheet) {
		int speed = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "speed"));
		int width = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "width"));
		int height = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "height"));
		int spritesPerRow = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "spritesPerRow"));
		int rows = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "rows"));
		NodeList element = ParserXML.retrieveValueFromNodeWithAttribute(nodes, "direction", attribute).getChildNodes();
		int originX = Integer.parseInt(ParserXML.retrieveValueFromNodeName(element, "originX"));
		int originY = Integer.parseInt(ParserXML.retrieveValueFromNodeName(element, "originY"));
		return RenderManager.createAnimation(spriteSheet, speed, originX, originY, width, height, spritesPerRow, rows);
	}
	
	public Animation createNonDirectionalAnimation(NodeList nodes, BufferedImage spriteSheet) {
		int speed = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "speed"));
		int width = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "width"));
		int height = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "height"));
		int spritesPerRow = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "spritesPerRow"));
		int rows = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "rows"));
		int originX = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "originX"));
		int originY = Integer.parseInt(ParserXML.retrieveValueFromNodeName(nodes, "originY"));
		return RenderManager.createAnimation(spriteSheet, speed, originX, originY, width, height, spritesPerRow, rows);
	}

}

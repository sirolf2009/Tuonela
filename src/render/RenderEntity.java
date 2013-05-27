package render;

import helper.ParserXML;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import core.Tuonela;

import render.sprite.Animation;
import render.sprite.Sprite;
import render.sprite.SpriteAnimated;

import entity.Entity;

public class RenderEntity implements Serializable {

	private static final long serialVersionUID = 5054164067332249348L;
	public Entity entity;
	public transient Sprite sprite;
	public transient Sprite spriteWalkingNorth, spriteWalkingSouth, spriteWalkingWest, spriteWalkingEast;
	public transient Sprite spriteDefault;
	public String img;

	public RenderEntity(Entity entity) {
		this.entity = entity;
		this.img = entity.getTexture();
		if(entity.getTexture().endsWith("xml")) {
			setAnimationsFromFile(entity.getTexture());
		} else {
			spriteDefault = new Sprite(entity.getTexture());
		}
	}

	public void draw(Graphics2D g) {
		System.out.println(sprite+ " "+entity);
		sprite.x = (int) entity.getPosX();
		sprite.y = (int) entity.getPosY();
		sprite.draw(g);
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
			for(int i = 0; i < doc.getDocumentElement().getChildNodes().getLength(); i ++)
			if(doc.getDocumentElement().getChildNodes().item(i).getNodeName() == "default")
				System.out.println(doc.getDocumentElement().getChildNodes().item(i).getChildNodes().item(0).getTextContent());
			NodeList nodesRunning = ParserXML.getNodeListByName(doc, "running");
			BufferedImage spriteSheet = ImageIO.read(new File(ParserXML.retrieveValueFromNodeName(nodesFile, "file")));

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
		} catch (DOMException | IOException e) {
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

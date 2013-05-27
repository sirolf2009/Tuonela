package render.sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Sprite {

	public Sprite(String img) {
		setImage(img);
	}

	public Sprite() {
	}

	public int x;
	public int y;
	
	public transient BufferedImage image;

	public Sprite setImage(String img) {
		try {
			image = ImageIO.read(new File(getClass().getResource(img).toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return this;
	}

	public Sprite setImage(String img, int x, int y, int width, int height) {
		try {
			image = ImageIO.read(new File(getClass().getResource(img).toURI())).getSubimage(x, y, width, height);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, x, y, getWidth(), getHeight(), null);
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}

}

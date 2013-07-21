package render.sprite;

import java.awt.image.BufferedImage;

public class SpriteAnimated extends Sprite {
	
	private Animation anim;
	
	public SpriteAnimated(Animation anim) {
		super();
		this.anim = anim;
	}
	
	public void update(long deltaTime) {
		anim.update(deltaTime);
		image = anim.getImage();
	}
	
	@Override
	public int getWidth() {
		return anim.getImage().getWidth(null);
	}
	
	@Override
	public int getHeight() {
		return anim.getImage().getHeight(null);
	}
	
	public BufferedImage getImage() {
		return anim.getImage();
	}

}

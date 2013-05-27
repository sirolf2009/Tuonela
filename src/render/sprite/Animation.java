package render.sprite;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

	public ArrayList<OneScene> scenes;
	private int sceneIndex;
	private long movieTime;
	private long totalTime;
	private long defaultSpeed;

	public Animation() {
		scenes = new ArrayList<OneScene>();
		totalTime = 0;
	}
	
	public Animation(int defaultSpeed) {
		this();
		this.defaultSpeed = defaultSpeed;
	}

	public synchronized void addScene(BufferedImage img, long t) {
		totalTime += t;
		scenes.add(new OneScene(img, totalTime));
	}
	
	public synchronized void addScene(BufferedImage img) {
		totalTime += defaultSpeed;
		scenes.add(new OneScene(img, totalTime));
	}

	public synchronized void restart() {
		movieTime = 0;
		sceneIndex = 0;
	}

	public synchronized void update(long deltaTime) {
		if(scenes.size() > 1) {
			movieTime += deltaTime;
			if(movieTime >= totalTime)
				restart();
			while(movieTime > scenes.get(sceneIndex).endTime)
				sceneIndex++;
		}
	}

	public synchronized BufferedImage getImage() {
		if(scenes.size()==0) {
			System.err.println("no indexed scenes");
			return null;
		}
		return scenes.get(sceneIndex).image;
	}
	
	private class OneScene {
		
		BufferedImage image;
		long endTime;
		
		public OneScene(BufferedImage image, long endTime) {
			this.image = image;
			this.endTime = endTime;
		}
	}

}

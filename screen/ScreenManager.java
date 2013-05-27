package screen;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.Tuonela;

public class ScreenManager {
	
	private GraphicsDevice vc;
	private JFrame frame;
	
	public ScreenManager() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = env.getDefaultScreenDevice();
	}
	
	public DisplayMode getFirstSupportedMode(DisplayMode[] modes) {
		DisplayMode[] supportedModes = vc.getDisplayModes();
		for(int i=0; i<modes.length; i++) {
			for(int j=0; j<supportedModes.length; j++) {
				if(areModesEqual(modes[i], supportedModes[j])) {
					return modes[i];
				}
			}
		}
		return null;
	}
	
	public DisplayMode getCurrentDisplayMode() {
		return vc.getDisplayMode();
	}
	
	public boolean areModesEqual(DisplayMode testedMode, DisplayMode graphicsMode) {
		if(testedMode.getWidth() != graphicsMode.getWidth() || testedMode.getHeight() != graphicsMode.getHeight())
			return false;
		if(testedMode.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && graphicsMode.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && testedMode.getBitDepth() != graphicsMode.getBitDepth())
			return false;
		if(testedMode.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && graphicsMode.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && testedMode.getRefreshRate() != graphicsMode.getRefreshRate())
			return false;
		return true;
	}
	
	public JFrame setFullscreen(DisplayMode mode) {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		vc.setFullScreenWindow(frame);
		if(mode != null && vc.isDisplayChangeSupported())
			try {
				vc.setDisplayMode(mode);
			} catch(Exception e) {
				e.printStackTrace();
				System.err.println("Could not pass the display mode to the graphics card");
			}
		return frame;
	}
	
	public Window getFullScreenWindow() {
		return vc.getFullScreenWindow();
	}
	
	public Graphics2D getGraphics() {
		if(Tuonela.instance.frame != null) {
			BufferStrategy strategy = Tuonela.instance.frame.getBufferStrategy();
			return (Graphics2D)strategy.getDrawGraphics();
		}
		return null;
	}
	
	public void update() {
		if(Tuonela.instance.frame != null) {
			BufferStrategy strategy = Tuonela.instance.frame.getBufferStrategy();
			if(!strategy.contentsLost()) {
				strategy.show();
			}
		}
	}
	
	public int getWidth() {
		if(Tuonela.instance.frame != null)
			return Tuonela.instance.frame.getWidth();
		return 0;
	}
	
	public int getHeight() {
		if(Tuonela.instance.frame != null)
			return Tuonela.instance.frame.getHeight();
		return 0;
	}

	public void restoreScreen() {
		Window window = getFullScreenWindow();
		if(window != null) {
			window.dispose();
		}
		vc.setFullScreenWindow(null);
	}
	
	public BufferedImage createCompatibleImage(int width, int height, int alpha) {
		if(Tuonela.instance.frame != null) {
			GraphicsConfiguration config = Tuonela.instance.frame.getGraphicsConfiguration();
			return config.createCompatibleImage(width, height, alpha);
		}
		return null;
	}
}

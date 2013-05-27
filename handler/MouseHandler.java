package handler;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import core.Tuonela;

import screen.ScreenManager;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

	private Point mouse, center;
	private Robot R2D2;
	
	public MouseHandler() {
		mouse = new Point();
		center = new Point();
		try {
			R2D2 = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void centerMouse(ScreenManager screenManager) {
		if(R2D2 != null && Tuonela.instance.frame.isShowing()) {
			center.x = screenManager.getWidth()/2;
			center.y = screenManager.getHeight()/2;
			SwingUtilities.convertPointFromScreen(center, Tuonela.instance.frame);
			R2D2.mouseMove(center.x, center.y);
		}
		mouse.x = center.x;
		mouse.y = center.y;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		e.consume();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		e.consume();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		e.consume();
	}

}

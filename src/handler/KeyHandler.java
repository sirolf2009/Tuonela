package handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.Tuonela;
import entity.Entity;
import entity.EntityPlayer;

public class KeyHandler implements KeyListener {

	private Tuonela client;

	public KeyHandler(Tuonela tuonela) {
		client = tuonela;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		Entity player = client.world.livingEntities.get(client.getPlayerID());
		float speed = ((EntityPlayer)client.world.livingEntities.get(client.getPlayerID())).getSpeed();
		switch(keyCode) {
		case KeyEvent.VK_ESCAPE: 
			Tuonela.stop();
			break;
		case KeyEvent.VK_D:
			player.setVelX(speed);
			break;
		case KeyEvent.VK_A:
			player.setVelX(-speed);
			break;
		case KeyEvent.VK_W:
			player.setVelY(-speed);
			break;
		case KeyEvent.VK_S:
			player.setVelY(speed);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_D:
			client.world.livingEntities.get(client.getPlayerID()).setVelX(0);
			break;
		case KeyEvent.VK_A:
			client.world.livingEntities.get(client.getPlayerID()).setVelX(0);
			break;
		case KeyEvent.VK_W:
			client.world.livingEntities.get(client.getPlayerID()).setVelY(0);
			break;
		case KeyEvent.VK_S:
			client.world.livingEntities.get(client.getPlayerID()).setVelY(0);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}

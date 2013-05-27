package handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import core.Tuonela;
import entity.EntityPlayer;

public class KeyHandler implements KeyListener {

	private Tuonela client;

	public KeyHandler(Tuonela tuonela) {
		client = tuonela;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_ESCAPE: 
			Tuonela.stop();
			break;
		case KeyEvent.VK_D:
			client.world.livingEntities.get(client.playerID).setVelX(((EntityPlayer)client.world.livingEntities.get(client.playerID)).getSpeed());
			break;
		case KeyEvent.VK_A:
			client.world.livingEntities.get(client.playerID).setVelX(-((EntityPlayer)client.world.livingEntities.get(client.playerID)).getSpeed());
			break;
		case KeyEvent.VK_W:
			client.world.livingEntities.get(client.playerID).setVelY(-((EntityPlayer)client.world.livingEntities.get(client.playerID)).getSpeed());
			break;
		case KeyEvent.VK_S:
			client.world.livingEntities.get(client.playerID).setVelY(((EntityPlayer)client.world.livingEntities.get(client.playerID)).getSpeed());
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_D:
			client.world.livingEntities.get(client.playerID).setVelX(0);
			break;
		case KeyEvent.VK_A:
			client.world.livingEntities.get(client.playerID).setVelX(0);
			break;
		case KeyEvent.VK_W:
			client.world.livingEntities.get(client.playerID).setVelY(0);
			break;
		case KeyEvent.VK_S:
			client.world.livingEntities.get(client.playerID).setVelY(0);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}

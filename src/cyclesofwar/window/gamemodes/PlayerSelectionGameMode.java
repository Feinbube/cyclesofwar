package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class PlayerSelectionGameMode extends GameMode {

	public PlayerSelectionGameMode(GamePanel gamePanel) {
		super(gamePanel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateGame() {
		// TODO Auto-generated method stub
		switchTo(GameModes.LIVE);
	}

	@Override
	protected void paintGame(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawControls(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mouseReleasedGame(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}

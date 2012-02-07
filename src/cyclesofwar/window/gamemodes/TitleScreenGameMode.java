package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class TitleScreenGameMode extends GameMode {

	private int count = 0;
	
	public TitleScreenGameMode(GamePanel gamePanel) {
		super(gamePanel);
	}

	@Override
	protected void updateGame() {
		if(count++ > 50){
			switchTo(GameModes.PLAYERSELECTION);
		}
	}

	@Override
	protected void paintGame(Graphics g) {
		rendering.drawTitleScreen(g);
	}

	@Override
	protected void drawControls(Graphics g) {
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
	}

	@Override
	protected void mouseReleasedGame(int x, int y) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}
}

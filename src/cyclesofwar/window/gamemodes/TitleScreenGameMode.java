package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class TitleScreenGameMode extends GameMode {

	private int timecounter = 0;
	
	public TitleScreenGameMode(GamePanel gamePanel) {
		super(gamePanel);
	}

	@Override
	protected void updateGame() {
		if(timecounter++ > 50){
			switchTo(GameModes.LIVE);
		}
	}

	@Override
	protected void paintGame(Graphics g) {
		getRendering().drawTitleScreen(g);
	}

	@Override
	protected void drawControls(Graphics g) {
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
	}

	@Override
	protected void mouseMovedGame(int x, int y) {
	}
	
	@Override
	protected void mousePressedGame(int x, int y) {
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

	@Override
	public void reset() {
	}
}

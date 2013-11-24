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
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void mouseMoved(int x, int y) {
	}
	
	@Override
	public void mousePressed(int x, int y) {
	}
	
	@Override
	public void mouseReleased(int x, int y) {
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

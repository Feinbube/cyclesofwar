package cyclesofwar.window.gamemodes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;
import cyclesofwar.window.rendering.Rendering;

public abstract class GameMode {

	private GamePanel gamePanel;

	private Object renderingLock = new Object();

	protected Random random = new Random();
	protected Rendering rendering = new Rendering();

	public GameMode(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	public void update() {
		synchronized (renderingLock) {
			updateGame();
			gamePanel.repaint();
		}
	}

	protected abstract void updateGame();

	public void paintComponent(Graphics g, Dimension size) {
		synchronized (renderingLock) {
			rendering.setSize(size);

			paintGame(g);

			drawControls(g);
		}
	}

	protected abstract void paintGame(Graphics g);

	protected abstract void drawControls(Graphics g);

	public void keyReleased(KeyEvent arg0) {
		keyPressedGame(arg0);
	}

	protected abstract void keyPressedGame(KeyEvent arg0);

	public void mouseReleased(MouseEvent arg0) {
		mouseReleasedGame(arg0.getX() - 10, arg0.getY() - 32);
	}

	protected abstract void mouseReleasedGame(int x, int y);

	protected void switchTo(GameModes gameMode) {
		gamePanel.switchTo(gameMode);
	}

	protected List<Player> getAllPlayers() {
		return gamePanel.getAllPlayers();
	}
	
	protected List<Player> getSelectedPlayers() {
		return gamePanel.getSelectedPlayers();
	}

	public abstract void resume();

	public abstract void pause();

	protected void setLiveUniverse(Universe universe) {
		gamePanel.setLiveUniverse(universe);
	}

	protected boolean getShowControls() {
		return gamePanel.getShowControls();
	}
}
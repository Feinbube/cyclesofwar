package cyclesofwar.window.gamemodes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;
import cyclesofwar.window.rendering.Rendering;

public abstract class GameMode {

	private final GamePanel gamePanel;

	private final Object renderingLock = new Object();

	protected Random random = new Random();	

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
        
        protected Rendering getRendering() {
            return this.gamePanel.getRendering();
        }

	public void paintComponent(Graphics g, Dimension size) {
		synchronized (renderingLock) {
			this.getRendering().setSize(size);

			paintGame(g);

			drawControls(g);
		}
	}

	protected abstract void paintGame(Graphics g);

	protected abstract void drawControls(Graphics g);

	public abstract void keyPressed(KeyEvent arg0);
	public abstract void mousePressed(int x, int y);
	public abstract void mouseReleased(int x, int y);
	public abstract void mouseMoved(int x, int y);

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

	public abstract void reset();
	
	protected void setLiveUniverse(Universe universe) {
		gamePanel.setLiveUniverse(universe);
	}

	protected boolean getShowControls() {
		return gamePanel.getShowControls();
	}
	
	protected void toggleSelection(Player player) {
		gamePanel.toggleSelection(player);
	}
	
        protected void setSelectedRendering(String selectedRendering) {
		gamePanel.setSelectedRendering(selectedRendering);
	}
        
	protected void setSelectedNumberOfRounds(int selectedNumberOfRounds) {
		gamePanel.setSelectedNumberOfRounds(selectedNumberOfRounds);
	}
	
	protected void setSelectedNumberOfPlanetsPerPlayer(int selectedNumberOfPlanetsPerPlayer) {
		gamePanel.setSelectedNumberOfPlanetsPerPlayer(selectedNumberOfPlanetsPerPlayer);
	}
	
	protected void setSelectedUniverseSizeFactor(double selectedUniverseSizeFactor) {
		gamePanel.setSelectedUniverseSizeFactor(selectedUniverseSizeFactor);
	}
	
        protected String getSelectedRendering() {
		return gamePanel.getSelectedRendering();
	}
        
	protected int getSelectedNumberOfRounds() {
		return gamePanel.getSelectedNumberOfRounds();
	}
	
	protected int getSelectedNumberOfPlanetsPerPlayer() {
		return gamePanel.getSelectedNumberOfPlanetsPerPlayer();
	}
	
	protected double getSelectedUniverseSizeFactor() {
		return gamePanel.getSelectedUniverseSizeFactor();
	}

	protected List<Integer> getPossibleNumbersOfPlanetsPerPlayer() {
		return gamePanel.getPossibleNumbersOfPlanetsPerPlayer();
	}
	
	protected List<Integer> getPossibleNumbersOfRounds() {
		return gamePanel.getPossibleNumbersOfRounds();
	}
        
        protected List<String> getPossibleRenderings() {
		return gamePanel.getPossibleRenderings();
	}
	
	protected List<Double> getPossibleValuesForUniverseSizeFactor() {
		return gamePanel.getPossibleValuesForUniverseSizeFactor();
	}
	
	protected int getThreadCount() {
		return gamePanel.getThreadCount();
	}
}
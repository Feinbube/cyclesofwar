package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class LiveGameMode extends GameMode {

	private Universe universe;
	private boolean pause = true;

	private double speedUp = 2;
	private int speedUpCounter = 0;

	public LiveGameMode(GamePanel gamePanel) {
		super(gamePanel);

		universe = new Universe(random.nextLong(), getSelectedPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
				this.getSelectedUniverseSizeFactor());
	}

	@Override
	protected void updateGame() {
		if (!pause) {
			if (speedUp >= 1) {
				for (int i = 0; i < speedUp; i++) {
					universe.update(Universe.getRoundDuration());
				}
			} else {
				speedUpCounter++;
				if (speedUpCounter > 1 / speedUp) {
					speedUpCounter = 0;
					universe.update(Universe.getRoundDuration());
				}
			}
		}
	}

	@Override
	protected void paintGame(Graphics g) {
		rendering.drawUniverse(g, universe);
	}

	@Override
	protected void drawControls(Graphics g) {
		if (!getShowControls()) {
			rendering.drawControlInfo(g, "F1/1 toggle info");
		} else {
			String pauseString = pause ? "continue" : "pause";
			rendering.drawControlInfo(g, "[Key Mapping] ESC: Menue ... +/-: game speed (" + ((int) (speedUp * 10)) / 10.0 + ") ... SPACE: "
					+ pauseString + " ... 5: new combat ... 6: replay ... 7: switch planets");
			rendering.drawSeed(g, universe.getSeed());
		}

		if (pause && !universe.isGameOver()) {
			rendering.drawInfo(g, "press SPACE to resume");
		}
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.switchTo(GameModes.PLAYERSELECTION);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_5) {
			universe = new Universe(random.nextLong(), getSelectedPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
					this.getSelectedUniverseSizeFactor());
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			pause = !pause;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_6) {
			universe = new Universe(universe.getSeed(), universe.getPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
					this.getSelectedUniverseSizeFactor());
		}

		if (arg0.getKeyCode() == KeyEvent.VK_7) {
			universe = new Universe(universe.getSeed(), rotate(universe.getPlayers()), this.getSelectedNumberOfPlanetsPerPlayer(),
					this.getSelectedUniverseSizeFactor());
		}

		if (arg0.getKeyCode() == KeyEvent.VK_PLUS) {
			if (speedUp >= 1) {
				speedUp += 1;
			} else {
				speedUp *= 2.0;
			}
		}

		if (arg0.getKeyCode() == KeyEvent.VK_MINUS) {
			if (speedUp >= 2) {
				speedUp -= 1;
			} else if (speedUp >= 0.2) {
				speedUp /= 2.0;
			}
		}
	}

	private List<Player> rotate(List<Player> players) {
		List<Player> result = new ArrayList<Player>();
		for (int i = 1; i < players.size(); i++) {
			result.add(players.get(i));
		}
		result.add(players.get(0));

		return result;
	}

	@Override
	protected void mouseReleasedGame(int x, int y) {
	}

	@Override
	protected void mousePressedGame(int x, int y) {
	}

	@Override
	protected void mouseMovedGame(int x, int y) {
	}

	@Override
	public void resume() {
		pause = false;
	}

	@Override
	public void pause() {
		pause = true;
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

	public long getUniverseSeed() {
		return this.universe.getSeed();
	}

	@Override
	public void reset() {
		universe = new Universe(universe.getSeed(), this.getSelectedPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
				this.getSelectedUniverseSizeFactor());
	}
}

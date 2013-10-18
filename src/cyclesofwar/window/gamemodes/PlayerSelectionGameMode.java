package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import cyclesofwar.Player;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class PlayerSelectionGameMode extends GameMode {

	public PlayerSelectionGameMode(GamePanel gamePanel) {
		super(gamePanel);
	}

	@Override
	protected void updateGame() {
	}

	@Override
	protected void paintGame(Graphics g) {
		rendering.drawPlayerSelection(g, getSelectedPlayers(), getAllPlayers(), getPossibleNumbersOfRounds(), getSelectedNumberOfRounds(),
				getPossibleNumbersOfPlanetsPerPlayer(), getSelectedNumberOfPlanetsPerPlayer(), getPossibleValuesForUniverseSizeFactor(),
				getSelectedUniverseSizeFactor());
	}

	@Override
	protected void drawControls(Graphics g) {
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
            if (arg0.getKeyCode() == KeyEvent.VK_D) {
                this.switchTo(GameModes.DEMO);
            }
	}

	@Override
	protected void mouseMovedGame(int x, int y) {
	}

	@Override
	protected void mousePressedGame(int x, int y) {
	}

	@Override
	protected void mouseReleasedGame(int x, int y) {
		Player player = rendering.getPlayer(x, y);
		if (player != null) {
			this.toggleSelection(player);
		}

		String buttonCaption = rendering.getButtonCaption(x, y);
		if (buttonCaption != null) {
                    switch (buttonCaption) {
                        case "Live Mode":
                            this.switchTo(GameModes.LIVE);
                            break;
                        case "Tournament Mode":
                            this.switchTo(GameModes.TOURNAMENT);
                            break;
                        case "Arena Mode":
                            this.switchTo(GameModes.ARENA);
                            break;
                        case "Demo Mode":
                            this.switchTo(GameModes.DEMO);
                            break;
                    }

			for (Integer i : getPossibleNumbersOfRounds()) {
				if (("rounds" + i).equals(buttonCaption)) {
					this.setSelectedNumberOfRounds(i);
				}
			}
			
			for (Integer i : getPossibleNumbersOfPlanetsPerPlayer()) {
				if (("planets" + i).equals(buttonCaption)) {
					this.setSelectedNumberOfPlanetsPerPlayer(i);
				}
			}
			
			for (Double d : getPossibleValuesForUniverseSizeFactor()) {
				if (("sizefactors" + d).equals(buttonCaption)) {
					this.setSelectedUniverseSizeFactor(d);
				}
			}
		}
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

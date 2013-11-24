package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import cyclesofwar.Player;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class MenuGameMode extends GameMode {

	public MenuGameMode(GamePanel gamePanel) {
		super(gamePanel);
	}

	@Override
	protected void updateGame() {
	}

	@Override
	protected void paintGame(Graphics g) {
		getRendering().drawMainMenu(g, getSelectedPlayers(), getAllPlayers(), 
                                getPossibleRenderings(), getSelectedRendering(), getPossibleNumbersOfRounds(), getSelectedNumberOfRounds(),
				getPossibleNumbersOfPlanetsPerPlayer(), getSelectedNumberOfPlanetsPerPlayer(), getPossibleValuesForUniverseSizeFactor(),
				getSelectedUniverseSizeFactor());
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
		Player player = getRendering().getPlayer(x, y);
		if (player != null) {
			this.toggleSelection(player);
		}

		String buttonCaption = getRendering().getButtonCaption(x, y);
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

                        for (String s : getPossibleRenderings()) {
				if (("rendering" + s).equals(buttonCaption)) {
					this.setSelectedRendering(s);
				}
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

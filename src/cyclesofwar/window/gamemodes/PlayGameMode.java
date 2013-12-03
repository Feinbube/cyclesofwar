package cyclesofwar.window.gamemodes;

import cyclesofwar.Planet;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.PlayerDisqualifiedException;
import cyclesofwar.Universe;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class PlayGameMode extends GameMode {

	private Universe universe;
	private boolean pause = true;

	private double speedUp = 2;
	private int speedUpCounter = 0;
        
        private List<Planet> selectedPlanets;
        private Planet targetPlanet;
        
        private double selectedForceRatio = 0.5;

	public PlayGameMode(GamePanel gamePanel) {
		super(gamePanel);

		universe = new Universe(random.nextLong(), getPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
				this.getSelectedUniverseSizeFactor());
                
                selectedPlanets = new ArrayList<>();
	}

	@Override
	protected void updateGame() {
		if (!pause) {
			if (speedUp >= 1) {
				for (int i = 0; i < speedUp; i++) {
					try {
						universe.update(Universe.getRoundDuration());
					} catch (PlayerDisqualifiedException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				speedUpCounter++;
				if (speedUpCounter > 1 / speedUp) {
					speedUpCounter = 0;
					try {
						universe.update(Universe.getRoundDuration());
					} catch (PlayerDisqualifiedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	@Override
	protected void paintGame(Graphics g) {
		getRendering().drawUniverse(g, universe);
                getRendering().drawPlayers(g, universe.getPlayers());
                for(Planet selectedPlanet : selectedPlanets) {
                    getRendering().drawSelectedPlanet(g, selectedPlanet);
                }
                getRendering().drawSelectedPlanet(g, targetPlanet);
                getRendering().drawSelectedForce(g, selectedForceRatio);
                getRendering().drawFps(g);
	}

	@Override
	protected void drawControls(Graphics g) {
		if (!getShowControls()) {
			getRendering().drawControlInfo(g, "F1/1 toggle info");
		} else {
			String pauseString = pause ? "continue" : "pause";
			getRendering().drawControlInfo(g, "[Key Mapping] ESC: Menue ... +/-: game speed (" + ((int) (speedUp * 10)) / 10.0 + ") ... SPACE: "
					+ pauseString + " ... 5: new combat ... 6: replay ... 7: switch planets");
			getRendering().drawSeed(g, universe.getSeed());
		}

		if (pause && !universe.isGameOver()) {
			getRendering().drawInfo(g, "press SPACE to resume");
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.switchTo(GameModes.PLAYERSELECTION);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_5) {
			universe = new Universe(random.nextLong(), getPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
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
		List<Player> result = new ArrayList<>();
		for (int i = 1; i < players.size(); i++) {
			result.add(players.get(i));
		}
		result.add(players.get(0));

		return result;
	}

        boolean mouseDown = false;
        
	@Override
	public void mouseReleased(int x, int y) {
            mouseDown = false;
            selectPlanets(x, y);                       
            
            if(!selectedPlanets.isEmpty() && targetPlanet != null) {
                for(Planet selectedPlanet : selectedPlanets) {
                    if(selectedPlanet == targetPlanet || !selectedPlanet.getPlayer().equals(Player.GoldenPlayer)) {
                        continue;
                    }
                    int forces = (int)(selectedForceRatio * selectedPlanet.getForces());
                    forces = (int)Math.min(Math.max(1, forces), selectedPlanet.getForces());
                    if(forces > 0) {
                        universe.sendPlayerFleet(selectedPlanet, forces, targetPlanet);
                    }
                }
            }
            
            targetPlanet = null;
	}

	@Override
	public void mousePressed(int x, int y) {
            mouseDown = true;            
            selectPlanets(x, y);
            
            // player clicked on background -> erase selection
            if(targetPlanet == null) {
                selectedPlanets.clear();
            }
	}

	@Override
	public void mouseMoved(int x, int y) {
            if(mouseDown) {
                // planets may have been selected
                selectPlanets(x, y);
                
                // force count slider may have been used
                double newForceRatio = this.getRendering().getUpdatedSelectedForce(x, y);
                selectedForceRatio = newForceRatio > 0 ? newForceRatio : selectedForceRatio;
            }
	}
        
        private void selectPlanets(int x, int y) {
            targetPlanet = getRendering().getPlanet(x, y);
            
            if(targetPlanet != null) {
                if(targetPlanet.getPlayer().equals(Player.GoldenPlayer) && !selectedPlanets.contains(targetPlanet)) {
                    selectedPlanets.add(targetPlanet);
                }
            }
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
		universe = new Universe(universe.getSeed(), this.getPlayers(), this.getSelectedNumberOfPlanetsPerPlayer(),
				this.getSelectedUniverseSizeFactor());
	}

    private List<Player> getPlayers() {
        List<Player> result = getSelectedPlayers();
        if(!result.contains(Player.GoldenPlayer)) {
            result.add(Player.GoldenPlayer);
        }
        return result;
    }
}

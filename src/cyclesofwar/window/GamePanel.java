package cyclesofwar.window;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.gamemodes.*;
import cyclesofwar.window.rendering.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements KeyListener, MouseInputListener, ActionListener {

	private static final long serialVersionUID = 1L;

	public final List<String> possibleRenderings = Arrays.asList("Simple", "Fancy", "Galaxy", "Organic", "Neural", "Cloud", "Pretty");
        public final List<Integer> possibleNumbersOfRounds = Arrays.asList(2, 5, 10, 20, 50, 75, 100, 250, 500);
	public final List<Integer> possibleNumbersOfPlanetsPerPlayer = Arrays.asList(1, 2, 5, 10, 20, 25, 50);
	public final List<Double> possibleValuesForUniverseSizeFactor = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 7.5, 10.0);
        
        private String rendering;
	private double universeSizeFactor;
	private int numberOfPlanetsPerPlayer;
	private int numbersOfRounds;

	private GameMode gameMode;

        private Rendering renderingImpl;
        
	private final TitleScreenGameMode titleScreen;
	private final LiveGameMode liveGame;
	private final ArenaGameMode arenaGame;
	private final TournamentGameMode tournamentGame;
	private final MenuGameMode playerSelection;
        private final DemoGameMode demo;

	private final List<Player> allPlayers;
	private final List<Player> selectedPlayers;

	private final int threadCount;

	private final Timer timer;

	private boolean showControls = false;

	public GamePanel(int threadCount, List<Player> lastPlayers, List<Player> allPlayers, String rendering, int numbersOfRounds, int numberOfPlanetsPerPlayer,
			double universeSizeFactor) {
		this.threadCount = threadCount;
		this.selectedPlayers = lastPlayers;
		this.allPlayers = allPlayers;

        setSelectedRendering("Pretty");
		setSelectedNumberOfRounds(numbersOfRounds);
		setSelectedNumberOfPlanetsPerPlayer(numberOfPlanetsPerPlayer);
		setSelectedUniverseSizeFactor(universeSizeFactor);

		titleScreen = new TitleScreenGameMode(this);
		liveGame = new LiveGameMode(this);
		arenaGame = new ArenaGameMode(this);
		tournamentGame = new TournamentGameMode(this);
		playerSelection = new MenuGameMode(this);
                demo = new DemoGameMode(this);

		gameMode = demo;
		liveGame.resume();

		timer = new Timer(20, this);
		timer.start();
	}

	public void switchTo(GameModes gameMode) {
		this.gameMode.pause();

		if (gameMode == GameModes.LIVE) {
			this.gameMode = liveGame;
		} else if (gameMode == GameModes.ARENA) {
			this.gameMode = arenaGame;
		} else if (gameMode == GameModes.TOURNAMENT) {
			this.gameMode = tournamentGame;
		} else if (gameMode == GameModes.PLAYERSELECTION) {
			this.gameMode = playerSelection;
		} else if (gameMode == GameModes.DEMO) {
			this.gameMode = demo;
		}
	}

	public List<Player> getSelectedPlayers() {
		return selectedPlayers;
	}

	public List<Player> getAllPlayers() {
		return this.allPlayers;
	}

	public void update() {
		gameMode.update();
	}

        public Rendering getRendering() {
            return renderingImpl;
        }
	public void toggleSelection(Player player) {
		if (selectedPlayers.contains(player)) {
			selectedPlayers.remove(player);
		} else {
			selectedPlayers.add(player);
		}

		resetGames();
	}                   

	public void setSelectedRendering(String rendering) {
                if(!possibleRenderings.contains(rendering)) {
                    rendering = possibleRenderings.get(0);
                }
                
                if(this.rendering != rendering || renderingImpl == null) {
                    if(rendering.equals("Simple")) {
                        renderingImpl = new SimpleRendering();
                    } else if (rendering.equals("Fancy")) {
                        renderingImpl = new FancyRendering();
                    } else if (rendering.equals("Galaxy")) {
                        renderingImpl = new GalaxyRendering();
                    } else if (rendering.equals("Organic")) {
                        renderingImpl = new OrganicRendering();
                    } else if (rendering.equals("Neural")) {
                        renderingImpl = new NeuralRendering();
                    } else if (rendering.equals("Cloud")) {
                        renderingImpl = new CloudRendering();
                    } else if (rendering.equals("Pretty")) {
                        renderingImpl = new PrettyRendering();
                    }
                }
                
                this.rendering = rendering;
	}
	
        public void setSelectedNumberOfRounds(int numbersOfRounds) {
		if (numbersOfRounds < Collections.min(possibleNumbersOfRounds)) {
			numbersOfRounds = Collections.min(possibleNumbersOfRounds);
		}
		while (!possibleNumbersOfRounds.contains(numbersOfRounds)) {
			numbersOfRounds--;
		}
		this.numbersOfRounds = numbersOfRounds;

		resetGames();
	}
                
	public void setSelectedNumberOfPlanetsPerPlayer(int numberOfPlanetsPerPlayer) {
		if (numberOfPlanetsPerPlayer < Collections.min(possibleNumbersOfPlanetsPerPlayer)) {
			numberOfPlanetsPerPlayer = Collections.min(possibleNumbersOfPlanetsPerPlayer);
		}
		while (!possibleNumbersOfPlanetsPerPlayer.contains(numberOfPlanetsPerPlayer)) {
			numberOfPlanetsPerPlayer--;
		}
		this.numberOfPlanetsPerPlayer = numberOfPlanetsPerPlayer;

		resetGames();
	}
	
	public void setSelectedUniverseSizeFactor(double universeSizeFactor) {
		if (universeSizeFactor < Collections.min(possibleValuesForUniverseSizeFactor)) {
			universeSizeFactor = Collections.min(possibleValuesForUniverseSizeFactor);
		}
		while (!possibleValuesForUniverseSizeFactor.contains(universeSizeFactor)) {
			universeSizeFactor -= 0.1;
		}
		this.universeSizeFactor = universeSizeFactor;

		resetGames();
	}

	private void resetGames() {
		if (liveGame != null) {
			liveGame.reset();
		}
		if (tournamentGame != null) {
			tournamentGame.reset();
		}
		if (arenaGame != null) {
			arenaGame.reset();
		}
	}

        public String getSelectedRendering() {
            return this.rendering;
        }
        
	public int getSelectedNumberOfRounds() {
		return this.numbersOfRounds;
	}

	public int getSelectedNumberOfPlanetsPerPlayer() {
		return this.numberOfPlanetsPerPlayer;
	}
	
	public double getSelectedUniverseSizeFactor() {
		return this.universeSizeFactor;
	}
	
	public int getThreadCount() {
		return this.threadCount;
	}

	@Override
	protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gameMode.paintComponent(g2, getSize());
	}

	public void setLiveUniverse(Universe universe) {
		liveGame.setUniverse(universe);
	}

	public boolean getShowControls() {
		return showControls;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.update();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		gameMode.keyPressed(arg0);

		if (arg0.getKeyCode() == KeyEvent.VK_1 || arg0.getKeyCode() == KeyEvent.VK_F1) {
			showControls = !showControls;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		gameMode.mouseReleased(arg0.getLocationOnScreen().x - this.getLocationOnScreen().x, arg0.getLocationOnScreen().y - this.getLocationOnScreen().y);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		gameMode.mousePressed(arg0.getLocationOnScreen().x - this.getLocationOnScreen().x, arg0.getLocationOnScreen().y - this.getLocationOnScreen().y);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		gameMode.mouseMoved(arg0.getLocationOnScreen().x - this.getLocationOnScreen().x, arg0.getLocationOnScreen().y - this.getLocationOnScreen().y);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
}
package cyclesofwar.window;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.gamemodes.ArenaGameMode;
import cyclesofwar.window.gamemodes.GameMode;
import cyclesofwar.window.gamemodes.LiveGameMode;
import cyclesofwar.window.gamemodes.PlayerSelectionGameMode;
import cyclesofwar.window.gamemodes.TitleScreenGameMode;
import cyclesofwar.window.gamemodes.TournamentGameMode;

public class GamePanel extends JPanel implements KeyListener, MouseInputListener, ActionListener {

	private static final long serialVersionUID = 1L;

	public final List<Integer> possibleNumbersOfRounds = Arrays.asList(2, 4, 6, 8, 12, 16, 24, 32, 48, 64);

	private GameMode gameMode;

	private TitleScreenGameMode titleScreen;
	private LiveGameMode liveGame;
	private ArenaGameMode arenaGame;
	private TournamentGameMode tournamentGame;
	private PlayerSelectionGameMode playerSelection;

	private List<Player> allPlayers;
	private List<Player> selectedPlayers;

	private int matchCount;
	private int threadCount;

	private Timer timer;

	private boolean showControls = false;

	public GamePanel(int threadCount, List<Player> lastPlayers, List<Player> allPlayers, int matchCount) {
		this.threadCount = threadCount;
		this.selectedPlayers = lastPlayers;
		this.allPlayers = allPlayers;
		
		setSelectNumberOfRounds(matchCount);

		titleScreen = new TitleScreenGameMode(this);
		liveGame = new LiveGameMode(this);
		arenaGame = new ArenaGameMode(this);
		tournamentGame = new TournamentGameMode(this);
		playerSelection = new PlayerSelectionGameMode(this);

		gameMode = titleScreen;
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

	public void toggleSelection(Player player) {
		if (selectedPlayers.contains(player)) {
			selectedPlayers.remove(player);
		} else {
			selectedPlayers.add(player);
		}

		resetGames();
	}

	public void setSelectNumberOfRounds(int matchCount) {
		if (matchCount < 2) {
			matchCount = 2;
		}
		while (!possibleNumbersOfRounds.contains(matchCount)) {
			matchCount--;
		}
		this.matchCount = matchCount;

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

	public int getSelectedNumberOfRounds() {
		return this.matchCount;
	}
	
	public int getThreadCount() {
		return this.threadCount;
	}

	@Override
	protected void paintComponent(Graphics g) {
		gameMode.paintComponent(g, getSize());
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
		gameMode.keyReleased(arg0);

		if (arg0.getKeyCode() == KeyEvent.VK_1 || arg0.getKeyCode() == KeyEvent.VK_F1) {
			showControls = !showControls;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		gameMode.mouseReleased(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		gameMode.mousePressed(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		gameMode.mouseMoved(arg0);
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
package cyclesofwar.window;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
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

	private GameMode gameMode;

	private TitleScreenGameMode titleScreen;
	private LiveGameMode liveGame;
	private ArenaGameMode arenaGame;
	private TournamentGameMode tournamentGame;
	private PlayerSelectionGameMode playerSelection;

	List<Player> allPlayers;
	private List<Player> selectedPlayers;

	private Timer timer;

	private boolean showControls = false;

	public GamePanel(int threads, List<Player> lastPlayers, List<Player> allPlayers, int matches) {
		this.selectedPlayers = lastPlayers;
		this.allPlayers = allPlayers;

		titleScreen = new TitleScreenGameMode(this);
		liveGame = new LiveGameMode(this);
		arenaGame = new ArenaGameMode(this, threads, matches);
		tournamentGame = new TournamentGameMode(this, threads, matches);
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

		if (arg0.getKeyCode() == KeyEvent.VK_F1) {
			showControls = !showControls;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		gameMode.mouseReleased(arg0);
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
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
}
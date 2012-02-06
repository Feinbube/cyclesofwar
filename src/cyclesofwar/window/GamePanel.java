package cyclesofwar.window;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.tournament.OneOnOneTournament;
import cyclesofwar.tournament.Tournament;
import cyclesofwar.tournament.TournamentRecord;
import cyclesofwar.window.rendering.Rendering;
import cyclesofwar.window.rendering.RenderingThread;

public class GamePanel extends JPanel implements KeyListener, MouseInputListener {

	private enum Mode {
		STARTUP, GAME, ARENA
	}

	private double speedUp = 2;
	private int threads;
	private int matches;
	private List<Player> playersForGameMode;
	private List<Player> playersForArenaMode;
	
	private static final long serialVersionUID = 1L;
	private Object renderingLock = new Object();

	private Random random = new Random();

	private RenderingThread mainThread;
	private Rendering rendering = new Rendering();

	private Tournament tournament;
	private Universe universe;

	private Mode mode = Mode.STARTUP;
	private boolean showControls = false;

    private boolean pause = false;
	
	public GamePanel(int threads, List<Player> playersForGameMode, List<Player> playersForArenaMode, int matches) {
		this.threads = threads;
		this.playersForGameMode = playersForGameMode;
		this.playersForArenaMode = playersForArenaMode;
		this.matches = matches;
		
		tournament = new OneOnOneTournament(threads, playersForArenaMode, matches);
		tournament.start();
		
		universe = new Universe(random.nextLong(), playersForGameMode);

		mainThread = new RenderingThread(this);
		new Thread(mainThread).start();
	}
	
	public double getSpeedUp() {
		return speedUp;
	}

	public void update() {
		synchronized (renderingLock) {
			if (mode == Mode.STARTUP && mainThread.isGameStarted()) {
				mode = Mode.GAME;
			}

			if (mode == Mode.GAME) {
				if (!pause) {
					for (int i = 0; i < speedUp; i++) {
						universe.update(Universe.speedOfLight);
					}
				}
			}

			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		synchronized (renderingLock) {
			rendering.setSize(getSize());

			if (mode == Mode.STARTUP) {
				rendering.drawTitleScreen(g);
			} else if (mode == Mode.GAME) {
				rendering.drawUniverse(g, universe);
			} else {
				rendering.drawStatistics(g, tournament);
			}

			drawControls(g);
		}
	}

	private void drawControls(Graphics g) {
		if (!showControls && (mode == Mode.GAME || mode == Mode.ARENA)) {
			rendering.drawControlInfo(g, "F1 toggle info");
		} else {
			if (mode == Mode.GAME) {
				String pauseString = pause ? "continue" : "pause";
				rendering.drawControlInfo(g, "+/- to change game speed (" + ((int) (speedUp * 10)) / 10.0 + ") ... SPACE to "
						+ pauseString + " ... F5 to start a new combat ... F6 to replay ... F7 to switch planets ... TAB to switch mode");
				rendering.drawSeed(g);
			} else if (mode == Mode.ARENA) {
				String pauseString = tournament.isPaused() ? "continue" : "pause";
				rendering.drawControlInfo(g, "CLICK on player to toogle priority ... CLICK on stats to see battle ... SPACE to "
						+ pauseString + " ... F5 to start a new combat ... TAB to switch mode");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (mode == Mode.GAME) {
			keyPressedInGameMode(arg0);
		} else if (mode == Mode.ARENA) {
			keyPressedInArenaMode(arg0);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F1) {
			showControls = !showControls;
		}
	}

	private void keyPressedInArenaMode(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			mode = Mode.GAME;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			tournament.abort();
			tournament = new OneOnOneTournament(threads, playersForArenaMode, matches);
			tournament.start();
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			tournament.togglePause();
		}
	}

	private void keyPressedInGameMode(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			mode = Mode.ARENA;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			universe = new Universe(random.nextLong(), playersForGameMode);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			pause = !pause;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F6) {
			universe = new Universe(universe.getSeed(), universe.getPlayers());
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_F7) {
			universe = new Universe(universe.getSeed(), rotate(universe.getPlayers()));
		}

		if (arg0.getKeyCode() == KeyEvent.VK_PLUS) {
			if (speedUp >= 1) {
				speedUp += 1;
			} else {
				speedUp += 0.1;
			}
		}

		if (arg0.getKeyCode() == KeyEvent.VK_MINUS) {
			if (speedUp >= 2) {
				speedUp -= 1;
			} else if (speedUp >= 0.2) {
				speedUp -=  0.1;
			}
		}
	}

	private List<Player> rotate(List<Player> players) {
		List<Player> result = new ArrayList<Player>();
		for(int i=1; i<players.size(); i++) {
			result.add(players.get(i));
		}
		result.add(players.get(0));
		
		return result;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (mode == Mode.ARENA) {
			List<TournamentRecord> winRecords = rendering.getFightRecords(arg0.getX() - 10, arg0.getY() - 32);
			if (winRecords != null && winRecords.size() > 0) {
				TournamentRecord winRecord = winRecords.get(random.nextInt(winRecords.size()));
				universe = new Universe(winRecord.getUniverseSeed(), winRecord.getPlayers());

				mode = Mode.GAME;
			} else {
				Player player = rendering.getPlayer(arg0.getX() - 10, arg0.getY() - 32, tournament);
				if (player != null) {
					tournament.switchPriority(player);
				}
			}
		}
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
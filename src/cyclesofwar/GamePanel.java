package cyclesofwar;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

class GamePanel extends JPanel implements KeyListener, MouseInputListener {

	enum Mode {
		STARTUP, GAME, ARENA
	}

	private static final long serialVersionUID = 1L;
	Object renderingLock = new Object();

	Random random = new Random();

	RenderingThread mainThread;
	Rendering rendering = new Rendering();

	FightChronics fightChronics;
	Universe universe;

	Mode mode = Mode.STARTUP;
	boolean showControls = false;

	boolean pause = false;

	public GamePanel() {
		fightChronics = new FightChronics();
		universe = new Universe(random.nextLong(), Arena.playersForGameMode());

		mainThread = new RenderingThread(this);
		new Thread(mainThread).start();
	}

	public void update() {
		synchronized (renderingLock) {
			if (mode == Mode.STARTUP && mainThread.gameStarted) {
				mode = Mode.GAME;
			}

			if (mode == Mode.GAME) {
				if (!pause) {
					for (int i = 0; i < Arena.speedUp; i++) {
						universe.update(Universe.speedOfLight);
					}
				}
			}

			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		synchronized (renderingLock) {
			if (mode == Mode.STARTUP) {
				rendering.drawTitleScreen(g);
			} else if (mode == Mode.GAME) {
				rendering.drawUniverse(g, universe, getSize());
			} else {
				rendering.drawStatistics(g, fightChronics, getSize());
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
				rendering.drawControlInfo(g, "+/- to change game speed (" + ((int) (Arena.speedUp * 10)) / 10.0 + ") ... SPACE to "
						+ pauseString + " ... F5 to start a new combat ... F6 to replay ... TAB to switch mode");
				rendering.drawSeed(g);
			} else if (mode == Mode.ARENA) {
				String pauseString = fightChronics.pause ? "continue" : "pause";
				rendering.drawControlInfo(g, "CLICK to see battle ... SPACE to " + pauseString + " ... F5 to start a new combat ... TAB to switch mode");
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
			fightChronics.reset();
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			fightChronics.togglePause();
		}
	}

	private void keyPressedInGameMode(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			mode = Mode.ARENA;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			universe = new Universe(random.nextLong(), Arena.playersForGameMode());
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			pause = !pause;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F6) {
			universe = new Universe(universe.seed, universe.players);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_PLUS) {
			if (Arena.speedUp >= 1) {
				Arena.speedUp = (int) (Arena.speedUp + 1);
			} else {
				Arena.speedUp += 0.1;
			}
		}

		if (arg0.getKeyCode() == KeyEvent.VK_MINUS) {
			if (Arena.speedUp >= 2) {
				Arena.speedUp--;
			} else if (Arena.speedUp >= 0.2) {
				Arena.speedUp -= 0.1;
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (mode == Mode.ARENA) {
			List<FightRecord> winRecords = rendering.getFightRecords(arg0.getX()-10,arg0.getY()-32);
			if (winRecords != null && winRecords.size() > 0) {
				FightRecord winRecord = winRecords.get(random.nextInt(winRecords.size()));
				universe = new Universe(winRecord.universeSeed, winRecord.players);

				mode = Mode.GAME;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
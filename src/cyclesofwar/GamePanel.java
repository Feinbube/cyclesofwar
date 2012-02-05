package cyclesofwar;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;

class GamePanel extends JPanel implements KeyListener {

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
		universe = new Universe(random.nextLong(), Arena.combatants());

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
				rendering.drawControlInfo(g, "+/- to change game speed (" + ((int) (Arena.speedUp * 10)) / 10.0 + ") ... SPACE to " + pauseString
						+ " F5 to start a new combat ... TAB to switch mode");
			} else if (mode == Mode.ARENA) {
				String pauseString = fightChronics.pause ? "continue" : "pause";
				rendering.drawControlInfo(g, "SPACE to " + pauseString + " ... F5 to start a new combat ... TAB to switch mode");
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
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
		
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			pause = !pause;
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
		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			universe = new Universe(random.nextLong(), Arena.combatants());
		}

		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			mode = Mode.ARENA;
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
	public void keyTyped(KeyEvent arg0) {
	}
}
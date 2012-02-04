package cyclesofwar;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;

class GamePanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;

	Random random = new Random();
	
	RenderingThread mainThread;
	Rendering rendering = new Rendering();

	Universe universe;

	public GamePanel() {
		universe = new Universe(random.nextLong());
		
		mainThread = new RenderingThread(this);
		new Thread(mainThread).start();
	}

	protected void paintComponent(Graphics g) {
		synchronized (mainThread.renderingLock) {
			rendering.drawUniverse(g, universe, getSize(), !mainThread.gameStarted);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_F5){
			universe = new Universe(random.nextLong());
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
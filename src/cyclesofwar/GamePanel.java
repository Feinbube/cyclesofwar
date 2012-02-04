package cyclesofwar;

import java.awt.Graphics;
import javax.swing.JPanel;

class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	Universe universe = new Universe();
	MainThread mainThread = new MainThread(this);
	Rendering rendering = new Rendering();

	public GamePanel() {
		new Thread(mainThread).start();
	}

	protected void paintComponent(Graphics g) {
		synchronized (mainThread.renderingLock) {
			rendering.drawUniverse(g, universe, getSize(), !mainThread.gameStarted);
		}
	}
}
package cyclesofwar;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	Dimension size = new Dimension(0, 0);

	public GamePanel() {		
		new Thread(new MainThread(this)).start();
	}

	protected void paintComponent(Graphics g) {
		if (!this.size.equals(getSize()))
			reInitialize();

		Universe.INSTANCE.draw(g);
	}

	private void reInitialize() {
		this.size = getSize();
		Universe.INSTANCE.reInitialize(size);
	}
}
package cyclesofwar;

public class MainThread implements Runnable {

	GamePanel gamePanel;

	boolean gameStarted = false;

	Object renderingLock = new Object();

	public MainThread(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gameStarted = true;

		while (true) {
			gamePanel.repaint();

			synchronized (renderingLock) {
				for (int i = 0; i < Arena.speedUp; i++) {
					gamePanel.universe.update(Universe.speedOfLight);
				}
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

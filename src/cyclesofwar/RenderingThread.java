package cyclesofwar;

public class RenderingThread implements Runnable {

	GamePanel gamePanel;

	boolean gameStarted = false;

	public RenderingThread(GamePanel gamePanel) {
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
			if (gamePanel != null) {
				gamePanel.update();
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

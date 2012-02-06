package cyclesofwar.window.rendering;

import cyclesofwar.window.GamePanel;

public class RenderingThread implements Runnable {

	private GamePanel gamePanel;
	private boolean gameStarted = false;

	public RenderingThread(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {

		sleep(1000);

		gameStarted = true;

		while (true) {
			if (gamePanel != null) {
				gamePanel.update();
			}

			if (gamePanel.getSpeedUp() < 1) {
				sleep((int) (1.0 / gamePanel.getSpeedUp() * 20));
			} else {
				sleep(20);
			}
		}

	}

	private void sleep(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isGameStarted() {
		return gameStarted;
	}
}

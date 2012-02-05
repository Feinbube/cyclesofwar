package cyclesofwar;

public class RenderingThread implements Runnable {

	GamePanel gamePanel;

	boolean gameStarted = false;

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

			if(Arena.speedUp < 1){
				sleep((int)(1.0/Arena.speedUp * 20));
			} else {
				sleep(20);
			}
		}

	}

	private void sleep(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

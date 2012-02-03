package cyclesofwar;
import java.util.Date;



public class MainThread implements Runnable {

	public final double speedUp = 5.0;
	GamePanel gamePanel;
	
	boolean gameStarted = false;
	
	Object renderingLock = new Object();
	
	public MainThread(GamePanel gamePanel){
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
		
		Date lastTime = new Date();
		
		while(true)
		{
			double elapsedSeconds = (new Date().getTime() - lastTime.getTime())/1000.0;
			lastTime = new Date();
			
			gamePanel.repaint();
			synchronized (renderingLock) {
			Universe.INSTANCE.update(elapsedSeconds*speedUp);
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

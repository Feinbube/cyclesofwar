package cyclesofwar;

public class WorkerThread implements Runnable {

	Statistics statistics;
	boolean running = true;
	boolean pause = false;

	public WorkerThread(Statistics statistics) {
		this.statistics = statistics;
	}

	@Override
	public void run() {
		running = true;
		
		while (running) {
			Universe universe = statistics.getUniverse();
			if (universe == null) {
				return;
			}

			while (!universe.gameOver && running) {
				while(pause) {
					sleep(200);
				}
				
				universe.update(Universe.speedOfLight);
			}
			
			if(!running){
				break;
			}
			
			statistics.gameOver(universe);
			
			sleep(10);
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

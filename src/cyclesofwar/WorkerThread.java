package cyclesofwar;

public class WorkerThread implements Runnable {

	FightChronics fightChronics;
	boolean running = true;
	boolean pause = false;

	public WorkerThread(FightChronics fightChronics) {
		this.fightChronics = fightChronics;
	}

	@Override
	public void run() {
		running = true;
		
		while (running) {
			Universe universe = fightChronics.getUniverse();
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
			
			fightChronics.gameOver(universe);
			
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

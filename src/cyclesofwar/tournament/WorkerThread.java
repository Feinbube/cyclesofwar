package cyclesofwar.tournament;

import cyclesofwar.Universe;

public class WorkerThread implements Runnable {

	private Tournament tournament;

	public boolean running = true;
	public boolean pause = false;

	private Thread thread;

	public WorkerThread(Tournament tournament) {
		this.tournament = tournament;
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			Universe universe = tournament.getUniverse();
			if (universe == null) {
				return;
			}

			while (!universe.isGameOver() && running) {
				while (pause) {
					sleep();
				}

				universe.update(Universe.speedOfLight);
			}

			if (!running) {
				break;
			}

			tournament.gameOver(universe);
		}
	}

	private void sleep() {
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class Statistic {
	Player player;
	int wins=0;
	int gamesPlayed=0;
	
	Statistic(Player player) {
		this.player = player;
	}
	
	Statistic(Statistic statistic) {
		this.player = statistic.player;
		
		this.wins = statistic.wins;
		this.gamesPlayed = statistic.gamesPlayed;
	}
	
	boolean isEqualTo(Player other) {
		return other.getName().equals(player.getName());
	}
	
	static Statistic findPlayer(List<Statistic> statistics, Player player) {
		for(Statistic statistic : statistics) {
			if(statistic.isEqualTo(player)){
				return statistic;
			}
		}
		
		return null;
	}
	
	static List<Statistic> clone(List<Statistic> statistics) {
		List<Statistic> result = new ArrayList<Statistic>();
		for(Statistic statistic : statistics) {
			result.add(new Statistic(statistic));
		}
		sort(result);
		return result;
	}
	
	static void sort(List<Statistic> statistics) {
		Collections.sort(statistics, new Comparator<Statistic>() {
			@Override
			public int compare(Statistic one, Statistic other) {
				return other.wins - one.wins;
			}
		});
	}
}

class Statistics {

	Random random = new Random();
	
	final int workerThreadCount = 8;

	List<Universe> gamesToPlay = new ArrayList<Universe>();
	List<Universe> gamesPlayed = new ArrayList<Universe>();
	
	int gamesToPlayCount;
	int gamesPlayedCount = 0;
	
	boolean pause = true;
	
	List<Statistic> statistics = new ArrayList<Statistic>();
	
	List<WorkerThread> workerThreads = new ArrayList<WorkerThread>();

	Statistics() {
		reset();
	}
	
	void reset() {
		gamesToPlay.clear();
		gamesPlayed.clear();
		statistics.clear();
		gamesPlayedCount = 0;
		
		stopExecution();
		workerThreads.clear();
		
		for(Player player : Arena.allPlayers()) {
			statistics.add(new Statistic(player));
		}
		
		setupGames();
		
		for(int t=0; t<workerThreadCount; t++) {
			WorkerThread workerThread = new WorkerThread(this);
			workerThread.pause = pause;
			workerThreads.add(workerThread);
			new Thread(workerThread).start();
		}
	}

	private void stopExecution() {
		for(WorkerThread workerThread : workerThreads) {
			workerThread.running = false;
		}
	}

	private void setupGames() {
		int count = Arena.allPlayers().size();
		for (int match = 0; match < Arena.matchesPerPairing; match++) {
			for (int i = 0; i < count-1; i++) {
				for (int j = i + 1; j < count; j++) {
					gamesToPlay.add(new Universe(random.nextLong(), getCombatants(i, j)));
				}
			}
		}
		
		gamesToPlayCount = gamesToPlay.size();
	}

	private List<Player> getCombatants(int i, int j) {
		List<Player> combatants = Arena.allPlayers();

		List<Player> result = new ArrayList<Player>();
		result.add(combatants.get(i));
		result.add(combatants.get(j));

		return result;
	}

	Universe getUniverse() {
		Universe result;
		synchronized (gamesToPlay) {
			if(gamesToPlay.size() == 0) {
				return null;
			}
			result = gamesToPlay.get(random.nextInt(gamesToPlay.size()));
			gamesToPlay.remove(result);
		}
		return result;
	}

	void gameOver(Universe universe) {
		synchronized (gamesPlayed) {
			gamesPlayed.add(universe);
			
			synchronized (statistics) {
				Statistic statistic = Statistic.findPlayer(statistics, universe.bestPlayer());
				statistic.wins++;
			}
			
			gamesToPlayCount--;
			gamesPlayedCount++;
		}
	}

	void togglePause() {
		pause = !pause;
		
		for(WorkerThread workerThread : workerThreads) {
			workerThread.pause = pause;
		}
	}

	List<Statistic> getStatisticsSorted() {
		synchronized (statistics) {
			return Statistic.clone(statistics);
		}
	}
}

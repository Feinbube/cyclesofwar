package cyclesofwar.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public abstract class Tournament {
	Random random;

	List<Player> champions = new ArrayList<Player>();
	List<Player> prioritized = new ArrayList<Player>();

	List<Universe> gamesToPlay = new ArrayList<Universe>();
	List<Universe> gamesPlayed = new ArrayList<Universe>();

	int gamesToPlayCount = 0;
	int gamesPlayedCount = 0;

	List<TournamentRecord> records = new ArrayList<TournamentRecord>();

	List<WorkerThread> workerThreads = new ArrayList<WorkerThread>();
	boolean pause = true;
	
	int planetsPerPlayer;
	double universeSizeFactor;
	
	boolean wasAborted = false;
	Player responsibleForAbort;

	public Tournament(long randomSeed, int threads, List<Player> champions, int matches, int planetsPerPlayer, double universeSizeFactor) {
		random = new Random(randomSeed);
		this.champions = champions;

		this.planetsPerPlayer = planetsPerPlayer;
		this.universeSizeFactor = universeSizeFactor;
		
		setupGames(matches);
		gamesToPlayCount = gamesToPlay.size();

		setupThreads(threads);
	}

	protected abstract void setupGames(int matches);

	// Lightweight Clone for Rendering
	protected Tournament(Tournament other) {
		for (Player player : other.champions) {
			this.champions.add(player);
		}

		synchronized (other.records) {
			for (TournamentRecord record : other.records) {
				this.records.add(record);
			}
		}

		for (Player player : other.prioritized) {
			this.prioritized.add(player);
		}

		this.gamesPlayedCount = other.gamesPlayedCount;
		this.gamesToPlayCount = other.gamesToPlayCount;
	}

	public abstract Tournament lightWeightClone();

	public void runToCompletion() {
		if (isPaused()) {
			togglePause();
		}

		start();

		for (WorkerThread thread : workerThreads) {
			thread.join();
		}
	}

	// getters

	public List<Player> getChampions() {
		return champions;
	}

	public int getGamesToPlayCount() {
		return gamesToPlayCount;
	}

	public int getGamesPlayedCount() {
		return gamesPlayedCount;
	}

	// Priority

	public void switchPriority(Player player) {
		if (prioritized.contains(player)) {
			prioritized.remove(player);
		} else {
			prioritized.add(player);
		}
	}

	public boolean hasPriority(Player player) {
		return prioritized.contains(player);
	}

	// Rankings

	public List<Player> rankedPlayers() {
		List<Player> result = new ArrayList<Player>();

		for (Ranking ranking : getRankings()) {
			result.add(ranking.player);
		}

		return result;
	}

	// TODO caching this function may be a very good idea!
	public List<Ranking> getRankings() {
		List<Ranking> result = new ArrayList<Ranking>();
		for (Player player : champions) {
			result.add(new Ranking(player, wonBy(player).size(), participatedIn(player).size()));
		}

		Ranking.sort(result);
		return result;
	}

	public List<TournamentRecord> winsOver(Player player, Player competitor) {
		return TournamentRecord.participatedIn(wonBy(player), competitor);
	}

	public List<TournamentRecord> wonBy(Player player) {
		return TournamentRecord.wonBy(records, player);
	}

	public List<TournamentRecord> fightsAgainst(Player player, Player competitor) {
		return TournamentRecord.participatedIn(participatedIn(player), competitor);
	}

	public List<TournamentRecord> participatedIn(Player player) {
		return TournamentRecord.participatedIn(records, player);
	}

	// workerthreads

	private void setupThreads(int workerThreadCount) {
		for (int t = 0; t < workerThreadCount; t++) {
			WorkerThread workerThread = new WorkerThread(this);
			workerThread.pause = pause;
			workerThreads.add(workerThread);
		}
	}

	public void start() {
		for (WorkerThread workerThread : workerThreads) {
			workerThread.start();
		}
	}

	public void abort() {
		for (WorkerThread workerThread : workerThreads) {
			workerThread.running = false;
		}
		
		wasAborted = true;
	}

	public boolean isPaused() {
		return pause;
	}

	public void togglePause() {
		pause = !pause;

		for (WorkerThread workerThread : workerThreads) {
			workerThread.pause = pause;

			if (!pause) {
				synchronized (workerThread) {
					workerThread.notifyAll();
				}
			}
		}
	}

	public Universe getUniverse() {
		Universe result;
		synchronized (gamesToPlay) {
			if (gamesToPlay.size() == 0) {
				return null;
			}

			result = getPrioritizedGame();
			if (result == null) {
				result = gamesToPlay.get(random.nextInt(gamesToPlay.size()));
			}

			gamesToPlay.remove(result);
		}
		return result;
	}

	private Universe getPrioritizedGame() {
		List<Universe> prioritizedGames = new ArrayList<Universe>();
		for (Universe universe : gamesToPlay) {
			for (Player player : prioritized) {
				if (universe.inhabitedByPlayer(player)) {
					prioritizedGames.add(universe);
					break;
				}
			}
		}

		if (prioritizedGames.size() > 0) {
			return prioritizedGames.get(random.nextInt(prioritizedGames.size()));
		} else {
			return null;
		}
	}

	public void gameOver(Universe universe) {
		synchronized (gamesPlayed) {
			gamesPlayed.add(universe);

			synchronized (records) {
				records.add(new TournamentRecord(universe));
			}

			gamesPlayedCount++;
			gamesToPlayCount--;
		}
	}

	public void abort(Player responsilbe) {
		this.abort();
		responsibleForAbort = responsilbe;
	}
	
	public boolean wasAborted() {
		return responsibleForAbort != null;
	}
	
	public Player getPlayerResponsibleForAbort() {
		return responsibleForAbort;
	}
}

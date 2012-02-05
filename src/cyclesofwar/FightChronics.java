package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class FightRecord {
	List<Player> players;
	Player winner;
	long universeSeed;

	FightRecord(FightRecord fightRecord) {
		players = new ArrayList<Player>();
		for (Player player : fightRecord.players) {
			this.players.add(player);
		}

		this.winner = fightRecord.winner;
		this.universeSeed = fightRecord.universeSeed;
	}

	FightRecord(Universe universe) {
		players = new ArrayList<Player>();
		for (Player player : universe.players) {
			this.players.add(player);
		}

		this.winner = universe.winner;
		this.universeSeed = universe.seed;
	}

	static List<FightRecord> clone(List<FightRecord> fightRecords) {
		List<FightRecord> result = new ArrayList<FightRecord>();
		for (FightRecord fightRecord : fightRecords) {
			result.add(new FightRecord(fightRecord));
		}
		return result;
	}

	boolean containsPlayer(Player competitor) {
		return competitor.isInList(players);
	}

	boolean winnerIs(Player player) {
		return winner.isEqualTo(player);
	}
}

class Ranking {
	Player player;
	int wins;
	int games;

	Ranking(Player player, int wins, int games) {
		this.player = player;
		this.wins = wins;
		this.games = games;
	}

	double getRatio() {
		return wins / (double) games;
	}
}

class FightChronics {

	Random random = new Random();

	final int workerThreadCount = 8;

	List<Player> combatants = new ArrayList<Player>();

	List<Universe> gamesToPlay = new ArrayList<Universe>();
	List<Universe> gamesPlayed = new ArrayList<Universe>();

	int gamesToPlayCount;
	int gamesPlayedCount = 0;

	boolean pause = true;

	List<FightRecord> fightRecords = new ArrayList<FightRecord>();
	List<Ranking> rankings = new ArrayList<Ranking>();

	List<WorkerThread> workerThreads = new ArrayList<WorkerThread>();

	FightChronics() {
		reset();
	}

	FightChronics(FightChronics fightChronics) {
		for (Player player : Arena.playersForArenaMode()) {
			this.combatants.add(player);
		}

		this.gamesPlayed = fightChronics.gamesPlayed;
		this.gamesToPlayCount = fightChronics.gamesToPlayCount;

		synchronized (fightChronics.fightRecords) {
			for (FightRecord fightRecord : fightChronics.fightRecords) {
				this.fightRecords.add(fightRecord);
			}
		}

		for (Player player : Arena.playersForArenaMode()) {
			rankings.add(new Ranking(player, fightRecordsWonBy(player).size(), participatedOnly(fightRecords, player).size()));
		}

		sortRankings();
	}

	private void sortRankings() {
		Collections.sort(rankings, new Comparator<Ranking>() {

			@Override
			public int compare(Ranking one, Ranking other) {
				return Double.compare(other.getRatio(), one.getRatio());
			}
		});
	}

	void reset() {
		combatants.clear();
		gamesToPlay.clear();
		gamesPlayed.clear();
		fightRecords.clear();

		gamesPlayedCount = 0;

		stopExecution();
		workerThreads.clear();

		for (Player player : Arena.playersForArenaMode()) {
			this.combatants.add(player);
		}

		setupGames();
		setupThreads();
	}

	private void setupThreads() {
		for (int t = 0; t < workerThreadCount; t++) {
			WorkerThread workerThread = new WorkerThread(this);
			workerThread.pause = pause;
			workerThreads.add(workerThread);
			new Thread(workerThread).start();
		}
	}

	private void stopExecution() {
		for (WorkerThread workerThread : workerThreads) {
			workerThread.running = false;
		}
	}

	private void setupGames() {
		int count = Arena.playersForArenaMode().size();
		for (int match = 0; match < Arena.matchesPerPairing; match++) {
			for (int i = 0; i < count - 1; i++) {
				for (int j = i + 1; j < count; j++) {
					gamesToPlay.add(new Universe(random.nextLong(), getCombatants(i, j)));
				}
			}
		}

		gamesToPlayCount = gamesToPlay.size();
	}

	private List<Player> getCombatants(int i, int j) {
		List<Player> combatants = Arena.playersForArenaMode();

		List<Player> result = new ArrayList<Player>();
		result.add(combatants.get(i));
		result.add(combatants.get(j));

		return result;
	}

	Universe getUniverse() {
		Universe result;
		synchronized (gamesToPlay) {
			if (gamesToPlay.size() == 0) {
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

			synchronized (fightRecords) {
				fightRecords.add(new FightRecord(universe));
			}

			gamesToPlayCount--;
			gamesPlayedCount++;
		}
	}

	void togglePause() {
		pause = !pause;

		for (WorkerThread workerThread : workerThreads) {
			workerThread.pause = pause;
		}
	}

	FightChronics lightWeightClone() {
		return new FightChronics(this);
	}

	List<FightRecord> winsOver(Player player, Player competitor) {
		return participatedOnly(fightRecordsWonBy(player), competitor);
	}

	List<FightRecord> fightsAgainst(Player player, Player competitor) {
		return participatedOnly(participatedOnly(fightRecords, player), competitor);
	}

	private List<FightRecord> participatedOnly(List<FightRecord> fights, Player competitor) {
		List<FightRecord> result = new ArrayList<FightRecord>();

		for (FightRecord fightRecord : fights) {
			if (fightRecord.containsPlayer(competitor)) {
				result.add(fightRecord);
			}
		}

		return result;
	}

	List<FightRecord> fightRecordsWonBy(Player player) {
		List<FightRecord> result = new ArrayList<FightRecord>();

		for (FightRecord fightRecord : this.fightRecords) {
			if (fightRecord.winnerIs(player)) {
				result.add(fightRecord);
			}
		}

		return result;
	}
}

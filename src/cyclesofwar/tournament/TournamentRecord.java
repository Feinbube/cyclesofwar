package cyclesofwar.tournament;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public class TournamentRecord {
	List<Player> players;
	Player winner;
	long universeSeed;

	public List<Player> getPlayers() {
		return players;
	}

	public Player getWinner() {
		return winner;
	}

	public long getUniverseSeed() {
		return universeSeed;
	}

	TournamentRecord(TournamentRecord record) {
		players = new ArrayList<Player>();
		for (Player player : record.players) {
			this.players.add(player);
		}

		this.winner = record.winner;
		this.universeSeed = record.universeSeed;
	}

	TournamentRecord(Universe universe) {
		players = universe.getPlayers();
		this.winner = universe.getWinner();
		this.universeSeed = universe.getSeed();
	}

	static List<TournamentRecord> clone(List<TournamentRecord> records) {
		List<TournamentRecord> result = new ArrayList<TournamentRecord>();
		for (TournamentRecord record : records) {
			result.add(new TournamentRecord(record));
		}
		return result;
	}

	boolean containsPlayer(Player competitor) {
		return competitor.isInList(players);
	}

	boolean winnerIs(Player player) {
		return winner.isEqualTo(player);
	}
	
	static List<TournamentRecord> participatedIn(List<TournamentRecord> records, Player competitor) {
		List<TournamentRecord> result = new ArrayList<TournamentRecord>();

		for (TournamentRecord record : records) {
			if (record.containsPlayer(competitor)) {
				result.add(record);
			}
		}

		return result;
	}

	static List<TournamentRecord> wonBy(List<TournamentRecord> records, Player player) {
		List<TournamentRecord> result = new ArrayList<TournamentRecord>();

		for (TournamentRecord record : records) {
			if (record.winnerIs(player)) {
				result.add(record);
			}
		}

		return result;
	}
}
package cyclesofwar.tournament;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Player;

public class Ranking {
	Player player;
	int wins;
	int games;

	public Player getPlayer() {
		return player;
	}

	public int getWins() {
		return wins;
	}

	public int getGames() {
		return games;
	}

	Ranking(Player player, int wins, int games) {
		this.player = player;
		this.wins = wins;
		this.games = games;
	}

	public double getRatio() {
		if (games == 0) {
			return 0;
		} else {
			return wins / (double) games;
		}
	}

	static void sort(List<Ranking> rankings) {
		Collections.sort(rankings, new Comparator<Ranking>() {

			@Override
			public int compare(Ranking one, Ranking other) {
				return Double.compare(other.getRatio(), one.getRatio());
			}
		});
	}
}

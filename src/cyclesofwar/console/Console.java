package cyclesofwar.console;

import java.util.List;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.tournament.LastManStandingTournament;
import cyclesofwar.tournament.Tournament;

public class Console {
	public static void main(String[] args) {
		List<Player> champions = Arena.champions();

		int place = 0;
		Tournament tournament = null;
		while (champions.size() > 1) {
			tournament = new LastManStandingTournament(Runtime.getRuntime().availableProcessors(), champions, Arena.matchesInTournament);
			tournament.runToCompletion();
			Player winner = tournament.rankedPlayers().get(0);
			printWinner(++place, tournament, winner);
			champions.remove(winner);
		}
		printWinner(++place, tournament, champions.get(0));
	}

	private static void printWinner(int place, Tournament tournament, Player winner) {
		System.out.println(place + "," + winner.getName() + "," + tournament.wonBy(winner).size() + ","
				+ tournament.participatedIn(winner).size());
	}
}
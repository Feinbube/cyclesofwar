package cyclesofwar.console;

import java.util.List;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.tournament.LastManStandingTournament;

public class LastManStandingTournamentConsole extends Console {
	public static void main(String[] args) {
		new LastManStandingTournamentConsole().run(args);
	}

	private void run(String[] args) {
		List<Player> champions = Arena.champions();

		int place = 0;
		LastManStandingTournament tournament = null;
		while (champions.size() > 1) {
			tournament = new LastManStandingTournament(Runtime.getRuntime().availableProcessors(), champions,
					Arena.matchesInLastManStandingTournamentPerRound, 10, 1.0);
			tournament.runToCompletion();
			Player winner = tournament.rankedPlayers().get(0);
			printPlayer(++place, tournament, winner);
			champions.remove(winner);
		}
		printPlayer(++place, tournament, champions.get(0));
	}
}
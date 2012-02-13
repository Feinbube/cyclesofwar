package cyclesofwar.console;

import java.util.List;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.tournament.OneOnOneTournament;
import cyclesofwar.tournament.Ranking;

public class OneOnOneTournamentConsole extends Console {
	public static void main(String[] args) {
		new OneOnOneTournamentConsole().run(args);
	}

	private void run(String[] args) {
		List<Player> champions = Arena.champions();

		OneOnOneTournament tournament = new OneOnOneTournament(Arena.tournamentSeed, Runtime.getRuntime().availableProcessors(), champions,
				Arena.matchesInOneOnOneTournamentPerPlayer, 10, 1.0);
		tournament.runToCompletion();
		
		int place = 0;
		for(Ranking ranking : tournament.getRankings()) {
			printPlayer(++place, tournament, ranking.getPlayer());
		}
	}
}
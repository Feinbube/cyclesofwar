package cyclesofwar.console;

import java.util.ArrayList;
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
		List<Player> disqualifiedPlayers = new ArrayList<Player>();

		OneOnOneTournament tournament;

		do {
			tournament = new OneOnOneTournament(Arena.tournamentSeed, Runtime.getRuntime().availableProcessors(), champions,
					Arena.matchesInOneOnOneTournamentPerPlayer, Arena.planetsPerPlayer, Arena.universeSizeFactor);
			tournament.runToCompletion();

			if (tournament.wasAborted()) {
				Player disqualifiedPlayer = tournament.getPlayerResponsibleForAbort();
				disqualifiedPlayers.add(disqualifiedPlayer);
				champions.remove(disqualifiedPlayer);
			}

		} while (tournament.wasAborted());

		int place = 0;
		for (Ranking ranking : tournament.getRankings()) {
			printPlayer(++place, tournament, ranking.getPlayer());
		}
		
		printDisqualifiedPlayers(disqualifiedPlayers);
	}
}
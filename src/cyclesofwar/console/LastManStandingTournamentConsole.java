package cyclesofwar.console;

import java.util.ArrayList;
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
		List<Player> disqualifiedPlayers = new ArrayList<>();

		int place = 0;
		LastManStandingTournament tournament = null;
		while (champions.size() > 1) {
			tournament = new LastManStandingTournament(Arena.tournamentSeed, Runtime.getRuntime().availableProcessors(), champions,
					Arena.matchesInLastManStandingTournamentPerRound, Arena.planetsPerPlayer, Arena.universeSizeFactor);
			tournament.runToCompletion();
			if (!tournament.wasAborted()) {
				Player winner = tournament.rankedPlayers().get(0);
				printPlayer(++place, tournament, winner);
				champions.remove(winner);
			} else {
				Player disqualifiedPlayer = tournament.getPlayerResponsibleForAbort();
				disqualifiedPlayers.add(disqualifiedPlayer);
				champions.remove(disqualifiedPlayer);
			}

		}
		printPlayer(++place, tournament, champions.get(0));

		printDisqualifiedPlayers(disqualifiedPlayers);
	}
}
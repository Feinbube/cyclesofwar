package cyclesofwar.console;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.tournament.Training;
import cyclesofwar.tournament.Ranking;

public class TrainingConsole extends Console {
  public static void main(String[] args) {
    new TrainingConsole().run(args);
  }

  private void run(String[] args) {
    List<Player> champions = Arena.champions();
    List<Player> disqualifiedPlayers = new ArrayList<Player>();

    Training tournament;

    do {
      tournament = new Training(Arena.tournamentSeed, Runtime.getRuntime().availableProcessors(), champions,
          Arena.matchesInTrainingPerPlayer, Arena.planetsPerPlayer, Arena.universeSizeFactor);
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

package cyclesofwar.tournament;

import java.util.Arrays;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public class Training extends Tournament {

  public Training(long randomSeed, int threads, List<Player> champions, int matches, int planetsPerPlayer, double universeSizeFactor) {
    super(randomSeed, threads, champions, matches, planetsPerPlayer, universeSizeFactor);
  }

  private Training(Training t) {
    super(t);
  }

  @Override
  protected void setupGames(int matches) {
    int count = champions.size();
    for (int match = 0; match < matches/2; match++) {
      long seed = random.nextLong();
      for (int i = 1; i < count; i++) {
        gamesToPlay.add(new Universe(seed, getChampions(0, i), planetsPerPlayer, universeSizeFactor));
        gamesToPlay.add(new Universe(seed, getChampions(i, 0), planetsPerPlayer, universeSizeFactor));
      }
    }
  }
  
  private List<Player> getChampions(int i, int j) {
    return Arrays.asList(champions.get(i), champions.get(j));
  }

  @Override
  public Tournament lightWeightClone() {
    return new Training(this); 
  }
}

package cyclesofwar.tournament;

import java.util.Arrays;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public class OneOnOneTournament extends Tournament {

	public OneOnOneTournament(int threads, List<Player> champions, int matches) {
		super(threads, champions, matches);
	}

	private OneOnOneTournament(OneOnOneTournament oneOnOneTournament) {
		super(oneOnOneTournament);
	}

	@Override
	protected void setupGames(int matches) {
		int count = champions.size();
		for (int match = 0; match < matches/2; match++) {
			for (int i = 0; i < count - 1; i++) {
				for (int j = i + 1; j < count; j++) {
					long seed = random.nextLong();
					gamesToPlay.add(new Universe(seed, getChampions(i, j)));
					gamesToPlay.add(new Universe(seed, getChampions(j, i)));
				}
			}
		}
	}
	
	private List<Player> getChampions(int i, int j) {
		return Arrays.asList(champions.get(i), champions.get(j));
	}

	@Override
	public Tournament lightWeightClone() {
		return new OneOnOneTournament(this); 
	}
}

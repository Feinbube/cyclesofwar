package cyclesofwar.tournament;

import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public class LastManStandingTournament extends Tournament {

	public LastManStandingTournament(long randomSeed, int threads, List<Player> champions, int matches, int planetsPerPlayer, double universeSizeFactor) {
		super(randomSeed, threads, champions, matches, planetsPerPlayer, universeSizeFactor);
	}

	private LastManStandingTournament(
			LastManStandingTournament lastManStandingTournament) {
		super(lastManStandingTournament);
	}

	@Override
	protected void setupGames(int matches) {
		for (int match = 0; match < matches; match++) {
			long seed = random.nextLong();
			gamesToPlay.add(new Universe(seed, champions, planetsPerPlayer, universeSizeFactor));
		}
	}

	@Override
	public Tournament lightWeightClone() {
		return new LastManStandingTournament(this);
	}

}

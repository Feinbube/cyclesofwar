package cyclesofwar.players.frank;

import cyclesofwar.players.frank.common.Jeesh;

public class Alai extends Jeesh {

	@Override
	public void thinkYourself() {
		strategyAlwaysTheSecond();
		//colonizeFree(Hinterland);
		// supportBorderPlanets();
		//evacutatePlanetsThatAreLost(NearestOfMine);
	}
}

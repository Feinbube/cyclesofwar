package cyclesofwar.players.frank;

import cyclesofwar.Player;
import cyclesofwar.players.frank.common.Jeesh;
import cyclesofwar.players.frank.common.Target;

public class Bean extends Jeesh {

	@Override
	public void thinkYourself() {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		if (this.getOtherPlayers().size() == 1) {
			winArena(this.getOtherPlayers().get(0));
		} else {
			strategyCollective();
			// strategyAlwaysTheSecond();

			// colonizeFree(Hinterland);
			// supportBorderPlanets();
			// evacutatePlanetsThatAreLost(NearestOfMine);
		}
	}

	private void winArena(Player enemy) {
		if (enemy.getCreatorsName().equals("Martin")) {
			strategyCollective();
		} else if (enemy.getCreatorsName().equals("Theo")) {
			strategyClone();
		} else if (enemy.getCreatorsName().equals("Robert")) {
			strategyAlwaysTheSecond2();
		} else if (enemy.getCreatorsName().equals("Peter")) {
			strategyFair(2);
		} else {

		}
	}

	@Override
	protected double valueOf(Target target) {
		return -target.getForcesToConquer() - target.getForcesToKeep();
	}
}

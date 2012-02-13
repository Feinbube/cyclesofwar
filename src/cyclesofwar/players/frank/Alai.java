package cyclesofwar.players.frank;

import cyclesofwar.players.frank.common.Jeesh;

public class Alai extends Jeesh {

	@Override
	public void thinkYourself() {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		if (this.getOtherPlayers().size() == 1) {
			strategyAlwaysTheSecond();
		} else {
			strategyCollective();
		}
	}
}

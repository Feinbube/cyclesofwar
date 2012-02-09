package cyclesofwar.players.frank;

import cyclesofwar.players.frank.common.Jeesh;

public class Bean extends Jeesh {

	@Override
	public void thinkYourself() {
		if (this.getFullForce() < this.allEnemyForces()) {
			alwaysTheSecondStrategy();
		} else {
			spreadTheWordStrategy();
		}
	}
}

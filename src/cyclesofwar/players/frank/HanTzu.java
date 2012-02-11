package cyclesofwar.players.frank;

import cyclesofwar.players.frank.common.Jeesh;

public class HanTzu extends Jeesh {

	@Override
	protected void thinkYourself() {
		if (getOtherAlivePlayers().size() > 1) {
			strategyFightMultipleEnemies();
		} else {
			spreadTheWordStrategy(10);
		}
	}
}

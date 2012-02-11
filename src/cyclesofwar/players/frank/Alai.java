package cyclesofwar.players.frank;

import cyclesofwar.players.frank.common.Target;

public class Alai extends Bean {

	@Override
	public void thinkYourself() {
		strategyAlwaysTheSecond2();
	}

	@Override
	protected double valueOf(Target target) {
		return -target.getForcesToConquer() - target.getForcesToKeep();
	}
}

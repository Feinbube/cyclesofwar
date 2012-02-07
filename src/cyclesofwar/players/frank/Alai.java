package cyclesofwar.players.frank;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Target;

public class Alai extends Bean {

	@Override
	public void thinkYourself() {
		for (Planet planet : this.getPlanets()) {
			Target target = Target.bestTarget(this, planet);
			if (target == null) {
				return; // already won :D
			} else if ((int) target.getForcesToConquer() > 0 && planet.getForces() >= target.getForcesToConquer()) {
				this.sendFleet(planet, target.getForcesToConquer(), target.getPlanet());
			}

			// TODO send fleets to keep as well?
			/*
			 * if (target.forcesToKeep > 0 && planet.getForces() >=
			 * target.forcesToKeep) { this.sendNewFleet(planet,
			 * (int)(target.forcesToKeep + 1), target.planet); }
			 */
		}

		// TODO Teamwork
		if (this.getFleets().size() == 0) {
			attackFromAll(getNearestFreeOrEnemyPlanet(this.getPlanets().get(0)), 0.5);
		}
	}

	@Override
	protected double valueOf(Target target) {
		return -target.getForcesToConquer() - target.getForcesToKeep();
	}
}

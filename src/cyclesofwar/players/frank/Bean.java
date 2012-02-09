package cyclesofwar.players.frank;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Jeesh;
import cyclesofwar.players.frank.common.Target;

public class Bean extends Jeesh {
	
	@Override
	public void thinkYourself() {
		while (true) {
			boolean send = false;

			for (Planet planet : this.getPlanets()) {
				Target target = Target.bestTarget(this, planet);
				if (target == null) {
					return; // already won :D
				} else if ((int) target.getForcesToConquer() > 0 && planet.getForces() >= target.getForcesToConquer()) {
					this.sendFleet(planet, target.getForcesToConquer(), target.getPlanet());
					send = true;
				}
				
				// TODO consider keeping the planet as well
			}

			if (!send) {
				break;
			}
		}

		// TODO Teamwork
		if (this.getFleets().size() == 0) {
			attackFromAll(firstOrNull(othersOnly((this.getPlanets().get(0).getOthersByDistance()))), 0.5);
		}
	}
}

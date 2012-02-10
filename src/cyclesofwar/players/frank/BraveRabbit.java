package cyclesofwar.players.frank;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Jeesh;

public class BraveRabbit extends Jeesh {

	@Override
	protected void thinkYourself() {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		for (Planet planet : this.getPlanets()) {
			Planet target = this.hostileOnly(planet.getOthersByDistance()).get(0);

			// jump around
			if (planet.getForces() >= 21) {
				sendFleet(planet, 21, target);
			}
		}
	}
}
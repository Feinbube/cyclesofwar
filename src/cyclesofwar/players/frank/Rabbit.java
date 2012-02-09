package cyclesofwar.players.frank;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Jeesh;

public class Rabbit extends Jeesh {

	@Override
	protected void thinkYourself() {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		for (Planet planet : this.getPlanets()) {
			Planet target = this.othersOnly(planet.getOthersByDistance()).get(0);

			// jump around
			if (planet.getForces() >= 31) {
				sendFleet(planet, 31, target);
			}

			// flee
			if (planet.getForces() + planet.getProductionRatePerRound() < forcesArrivingNextRound(planet)) {
				sendFleetUpTo(planet, (int) planet.getForces(), target);
			}
		}
	}

	private int forcesArrivingNextRound(Planet planet) {
		int result = 0;
		for (Fleet fleet : Fleet.sortedByArrivalTime(othersOnly(getFleetsWithTarget(planet)))) {
			if (fleet.getRoundsToTarget() <= 1) {
				result += fleet.getForce();
			}
		}
		return result;
	}
}
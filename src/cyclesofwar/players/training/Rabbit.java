package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Rabbit extends Player {

	@Override
	public void think() {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		for (Planet planet : this.getPlanets()) {
			Planet target = this.hostileOnly(planet.getOthersByDistance()).get(0);

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
		for (Fleet fleet : Fleet.sortedByArrivalTime(hostileOnly(getFleetsWithTarget(planet)))) {
			if (fleet.getRoundsToTarget() <= 1) {
				result += fleet.getForce();
			}
		}
		return result;
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.yellow;
	}

	@Override
	public String getCreatorsName() {
		return "Noob";
	}

}

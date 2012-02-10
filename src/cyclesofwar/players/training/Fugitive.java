package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Fugitive extends Player {

	@Override
	public void think() {
		for (Planet planet : this.getPlanets()) {
			Planet target = planet.getOthersByDistance().get(getRandomInt(5));
			if (0 < forcesArrivingNextRound(planet)) {
				sendFleetUpTo(planet, (int)(planet.getForces()*0.5), target);
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

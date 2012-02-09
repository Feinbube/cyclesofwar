package cyclesofwar.players.frank;

import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Jeesh;

public class Petra extends Jeesh {
	
	@Override
	public void thinkYourself() {
		boolean attacked = false;
		for (Planet planet : this.getPlanets()) {
			List<Planet> targets = mostProductiveByDistance(planet);
			if (targets.isEmpty())
				continue;

			attacked = attack(attacked, planet, targets, 0, 1.5, 0.3);
			attacked = attack(attacked, planet, targets, 1, 2, 0.3);
			attacked = attack(attacked, planet, targets, 2, 3, 0.3);
		}

		if (attacked == false) {
			List<Planet> targets = mostProductiveByDistance(null);
			if (!targets.isEmpty() && this.getGroundForce() > targets.get(0).getForces() * 3) {
				attackFromAll(targets.get(0), 0.5);
			}
		}
	}
	
	private boolean attack(boolean attacked, Planet planet, List<Planet> targets, int i, double compareFactor, double attacFactor) {
		if (!attacked && targets.size() > i && planet.getForces() > targets.get(i).getForces() * compareFactor) {
			this.sendFleetUpTo(planet, (int) (planet.getForces() * attacFactor), targets.get(i));
			return true;
		} else {
			return false;
		}
	}
}

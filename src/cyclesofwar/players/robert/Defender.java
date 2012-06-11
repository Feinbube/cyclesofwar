package cyclesofwar.players.robert;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;

public class Defender extends Player {

	@Override
	protected void think() {
		List<Planet> myPlanets = this.getPlanets();
		Planet.sortByForceCount(myPlanets);

		if (myPlanets.size() == 0)
			return;

		Planet capital = myPlanets.get(0);
		myPlanets.remove(capital);

		List<Planet> other = getAllPlanetsButMine();
		Planet.sortByForceCount(other);
		if (other.size() > 0) {
			Planet target = other.get(other.size() - 1);
			int requiredForce = (int)target.getForces() + 1;
			if (requiredForce < capital.getForces()) {
				sendFleetUpTo(capital, requiredForce, target);
			}
		}

		for (Planet planet : myPlanets) {
			if (planet.getForces() > 5) {
				int requiredForce = (int)planet.getForces() / 4;
				if (requiredForce >= planet.getForces()) {
					sendFleet(planet, requiredForce, capital);
				}
			}
		}

	}

}

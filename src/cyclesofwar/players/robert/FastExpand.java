package cyclesofwar.players.robert;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import cyclesofwar.Planet;


public class FastExpand extends Player {

	@Override
	protected void think() {
		for (Planet  planet : getPlanets()) {
			List<Planet> targets = getAllPlanetsButMine();
                        Planet.sortBy(Planet.ForceCountComparator, targets);
			Collections.reverse(targets);
			
			while (planet.getForces() > 1 && !targets.isEmpty()) {
				Planet target = targets.remove(0);
				sendFleetUpTo(planet, (int)target.getForces() + 1, target);
			}
		}
	}

}

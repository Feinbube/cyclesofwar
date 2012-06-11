package cyclesofwar.players.robert;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Universe;


public class NasChoka extends Player {
	
	int t = 10;

	@Override
	protected void think() {
		t += 0.01;
		
		for (Planet origin : getPlanetsOf(this)) {
			List<Planet> otherPlanets = getAllPlanetsButMine();
			Planet.sortByDistanceTo(otherPlanets, origin);
			for (Planet target : otherPlanets) {
				while (origin.getForces() >= t) {
					sendFleetUpTo(origin, origin.getForces() / 2, target);
				}
			}
		}
		

	}
	
	private List<Planet> notUnderAttack(List<Planet> planets) {
		List<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			if (!isUnderAttack(planet))
				result.add(planet);
		}
		
		return result;
	}
	
	private boolean isUnderAttack(Planet planet) {
		for (Fleet fleet : getFleets()) {
			if (fleet.getTarget().equals(planet))
				return true;
		}
		return false;
	}


}

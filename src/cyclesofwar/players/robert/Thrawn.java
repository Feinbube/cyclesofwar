package cyclesofwar.players.robert;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;


public class Thrawn extends Player {

	@Override
	protected void think() {
		List<Planet> otherPlanets = getAllPlanetsButMine();	
		List<Planet> otherPlanetsNotUnderAttack = notUnderAttack(otherPlanets);	
		for (Planet target : Planet.sortedBy(Planet.ProductivityComparator, otherPlanetsNotUnderAttack)) {
			for (Planet origin : getPlanets()) {
				double time = origin.getTimeTo(target);
				double force = target.getForces() + target.getProductionRatePerSecond()*time;
				if (origin.getForces() >= force+1) {
					sendFleet(origin, (int) (force+1), target);
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

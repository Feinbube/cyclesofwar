package cyclesofwar.players.robert;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

public class AttackLargestPlayer extends Player {
	
	@Override
	protected void think() {
		
		List<Planet> myPlanets = this.getPlanets();
		Planet.sortByForceCount(myPlanets);
		
		for (Planet planet : myPlanets) {
			int attackForceSize = (int)(planet.getForces()/2);
			
			Planet target = getLargestBeatablePlanet(attackForceSize);
			if (target != null) {
				sendFleet(planet, attackForceSize, target);
			}
			
			if (getOtherPlanetsNotUnderAttack().size() == 1) {
				myPlanets = this.getPlanets();
				Planet.sortByForceCount(myPlanets);
				sendFleetUpTo(myPlanets.get(0), (int)myPlanets.get(0).getForces() / 2, getOtherPlanetsNotUnderAttack().get(0));
			}
		}
	}
	
	public Planet getLargestBeatablePlanet(int attackForceSize) {
		for (Planet target : getOtherPlanetsNotUnderAttack()) {
			if (target.getForces() < attackForceSize) {				
				return target;
			}	
		}
		return null;
	}
	
	public List<Planet> getOtherPlanetsNotUnderAttack() {
		List<Planet> result = getAllPlanetsButMine();
		Planet.sortByForceCount(result);
		for (Fleet fleet : this.getFleets()) {
			result.remove(fleet.getTarget());
		}
		return result;
	}

}

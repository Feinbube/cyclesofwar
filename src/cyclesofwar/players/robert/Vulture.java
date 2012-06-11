package cyclesofwar.players.robert;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

public class Vulture extends Player {
	
	List<Fleet> counteredFleets = new ArrayList<Fleet>();
	Fleet expensionFleet = null;

	@Override
	protected void think() {
		vulture(getAllEnemyFleets());
		expand();
	}
	
	private void vulture(List<Fleet> fleets) {
		for (Fleet fleet : fleets) {
			vulture(fleet);
		}
	}

	private void expand() {
		if (!getAllFleets().contains(expensionFleet)) {
			List<Planet> myPlanets = getPlanetsOf(this);
			List<Planet> otherPlanets = getAllPlanetsButMine();
			if (!myPlanets.isEmpty() && !otherPlanets.isEmpty()) {
				Planet.sortByForceCount(myPlanets);
				Planet attacker = myPlanets.get(0);
				Planet.sortByDistanceTo(otherPlanets, attacker);
				Planet next = otherPlanets.get(0);
				expensionFleet = sendFleetUpTo(attacker, (int) attacker.getForces() / 2, next);
			}
		}
	}
	
	private void vulture(Fleet fleet) {
		if (counteredFleets.contains(fleet)) {
			return;
		}
		System.out.println(fleet + " -> " + fleet.getTarget());
		
		double cadavarForce = forceAfterAttack(fleet);	
		
		for (Planet planet : getFartherPlanets(fleet)) {
			double requiredForce = 1 + force(fleet.getTarget(), planet.getTimeTo(fleet.getTarget())) + forceAfterAttack(fleet);
			if (requiredForce > cadavarForce && planet.getForces() > requiredForce) {
				sendFleetUpTo(planet, (int)requiredForce, fleet.getTarget());
				counteredFleets.add(fleet);
				break;
			}
			
		}
	}

//	private void vulture(Fleet fleet) {
//		if (counteredFleets.contains(fleet)) {
//			return;
//		}
//		System.out.println(fleet + " -> " + fleet.getTarget());
//		double cadavarForce = forceAfterAttack(fleet);
//		
//		for (Planet planet : getFartherPlanets(fleet)) {
//			double requiredForce = 1 + forceAtLandingTime(planet.timeTo(fleet.getTarget()), fleet.getTarget()) - forceAfterAttack(fleet);
//			if (requiredForce > cadavarForce && planet.getForces() > requiredForce) {
//				sendFleetUpTo(planet, (int)requiredForce, fleet.getTarget());
//				counteredFleets.add(fleet);
//				break;
//			}
//			
//		}
//	}
	
	private double force(Planet target, double time) {
		return target.getProductionRatePerSecond() * time;
	}

	private double forceAfterAttack(Fleet fleet) {
		return Math.abs(forceAtLandingTime(fleet) - fleet.getForce());
	}

	private double forceAtLandingTime(Fleet fleet) {
		return forceAtLandingTime(fleet.getTimeToTarget(), fleet.getTarget());
	}
	
	private double forceAtLandingTime(double flightTime, Planet target) {
		double currentTargetForce = target.getForces();
		double growthDuringApproach = target.getProductionRatePerSecond() * flightTime;
		return currentTargetForce + growthDuringApproach;
	}

	private List<Planet> getFartherPlanets(final Fleet fleet) {
		List<Planet> result = new ArrayList<Planet>();
		for (Planet planet : getPlanetsOf(this)) {
			if (planet.getTimeTo(fleet.getTarget()) > fleet.getTimeToTarget()) {
				result.add(planet);
			}				
		}
		Collections.sort(result, new Comparator<Planet>() {

			@Override
			public int compare(Planet p1, Planet p2) {
				return (int) (p1.getTimeTo(fleet.getTarget()) - p2.getTimeTo(fleet.getTarget()));
			}
			
		});
//		if (result.size() > 2) {
//			for (Planet p : result)
//				System.out.println(p.timeTo(fleet.getTarget()));
//			System.exit(0);
//		}
		return result;
	}


}

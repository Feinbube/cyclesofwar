package cyclesofwar.players;
import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;


public class AttackLargestPlayer extends Player {

	@Override
	public void think() {
		List<Planet> myPlanets = this.getPlanets();
		sortByFleetSize(myPlanets);
		
		for (Planet planet : myPlanets) {
			int attackForceSize = (int)(planet.getForces()/2);
			
			Planet target = getLargestBeatablePlanet(attackForceSize);
			if (target != null) {
				sendFleet(planet, attackForceSize, target);
			}
		}
	}
	
	private Planet getLargestBeatablePlanet(int attackForceSize) {
		for (Planet target : getOtherPlanetsNotUnderAttack()) {
			if (target.getForces() < attackForceSize) {				
				return target;
			}	
		}
		return null;
	}
	
	public List<Planet> getOtherPlanetsNotUnderAttack() {
		List<Planet> result = getOtherPlanets();
		for (Fleet fleet : this.getFleets()) {
			result.remove(fleet.getTarget());
		}
		return result;
	}
	
	public List<Planet> getOtherPlanets() {
		List<Planet> result = new LinkedList<Planet>();
		for (Planet planet : getAllPlanets()) {
			if (isNotMyPlanet(planet)) {
				result.add(planet);
			}
		}
		
		sortByFleetSize(result);		
		return result;
	}

	private void sortByFleetSize(List<Planet> result) {
		Collections.sort(result, new Comparator<Planet>() {

			@Override
			public int compare(Planet o1, Planet o2) {
				return (int) (o2.getForces() - o1.getForces());
			}
		});
	}

	private boolean isNotMyPlanet(Planet planet) {
		return !planet.getPlayer().equals(this);
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.red;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	public String getCreatorsName() {
		return "Robert";
	}

}

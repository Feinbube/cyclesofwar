package cyclesofwar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class AttackLargestPlayer extends Player {

	public AttackLargestPlayer(Color color, Planet starterPlanet) {
		super(color, starterPlanet);
	}

	@Override
	public void think() {
		List<Planet> myPlanets = Universe.INSTANCE.PlanetsOfPlayer(this);
		sortByFleetSize(myPlanets);
		
		for (Planet planet : myPlanets) {
			int attackForceSize = (int)(planet.forces/2);
			
			List<Planet> possibleTargets = notUnderAttack();
			for (Planet target : possibleTargets) {
				if (target.forces < attackForceSize) {
					Universe.INSTANCE.SendFleet(this, planet, attackForceSize, target);
					possibleTargets.remove(target);
					break;
				}	
			}
		}

	}
	
	public List<Fleet> getAramada() {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : Universe.INSTANCE.fleets) {
			if (fleet.player.equals(this)) {
				result.add(fleet);
			}
		}
		return result;
	}
	
	public List<Planet> notUnderAttack() {
		List<Planet> planets = getOtherPlanets();
		for (Fleet fleet : getAramada()) {
			planets.remove(fleet.target);
		}
		return planets;
	}
	
	public List<Planet> getOtherPlanets() {
		List<Planet> result = new LinkedList<Planet>();
		for (Planet planet : Universe.INSTANCE.AllPlanets()) {
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
				return (int) (o2.forces - o1.forces);
			}
		});
	}

	private boolean isNotMyPlanet(Planet planet) {
		return !planet.player.equals(this);
	}

}

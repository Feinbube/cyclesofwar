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
		for (Planet planet : Universe.INSTANCE.PlanetsOfPlayer(this)) {
			int attackForceSize = (int)(planet.forces/2);		
			List<Planet> possibleTargets = getOtherPlanets();
			for (Planet target : possibleTargets) {
				if (target.forces < attackForceSize) {
					Universe.INSTANCE.SendFleet(this, planet, attackForceSize, target);
					possibleTargets.remove(target);
					break;
				}	
			}
		}

	}
	
	public List<Planet> getOtherPlanets() {
		List<Planet> result = new LinkedList<Planet>();
		for (Planet planet : Universe.INSTANCE.AllPlanets()) {
			if (isNotMyPlanet(planet)) {
				result.add(planet);
			}
		}
		
		Collections.sort(result, new Comparator<Planet>() {

			@Override
			public int compare(Planet o1, Planet o2) {
				return (int) (o1.forces - o2.forces);
			}
		});
		
		return result;
	}

	private boolean isNotMyPlanet(Planet planet) {
		return !planet.player.equals(this);
	}

}

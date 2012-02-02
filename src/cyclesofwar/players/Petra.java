package cyclesofwar.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Petra extends Player {

	List<Fleet> fleetsHandled = new ArrayList<Fleet>();
	
	@Override
	public void think() {
		for(Planet planet : this.getPlanets()) {
			List<Planet> targets = notOwnedByMe(getOtherPlanetsSortedByDistance(planet));
			if(targets.size() == 0)
				continue;
			
			Planet target = targets.get(0);
			if(planet.getForces() > target.getForces()*2)
				this.sendFleet(planet, planet.getForces(), target);
		}
	}
	
	private List<Planet> notOwnedByMe(List<Planet> planets) {
		List<Planet> result = new ArrayList<Planet>();
		
		for(Planet planet : planets)
			if(!planet.getPlayer().equals(this))
				result.add(planet);
				
		return result;
	}
	
	private List<Planet> getOtherPlanetsSortedByDistance(final Planet planet) {
		List<Planet> result = this.getAllPlanets();
		result.remove(planet);
		
		Collections.sort(result, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {				
				return (int) (planet.distanceTo(planet1) - planet.distanceTo(planet2));
			}
		});
		
		return result;
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.blue.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	public String getCreatorsName() {
		return "Frank";
	}

}

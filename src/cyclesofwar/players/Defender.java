package cyclesofwar.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Defender extends AttackLargestPlayer {

	@Override
	protected void think() {
		List<Planet> myPlanets = this.getPlanets();
		AttackLargestPlayer.sortByFleetSize(myPlanets);
		
		if (myPlanets.size() == 0)
			return;
					
		Planet capital = myPlanets.get(0);
		myPlanets.remove(capital);
		
		List<Planet> other = getOtherPlanets();
		if (other.size() > 0) {
			AttackLargestPlayer.sortByFleetSize(other);
			Planet target = other.get(other.size() - 1);
			sendFleet(capital, target.getForces() + 1, target);
		}
		
		for (Planet planet : myPlanets) {
			if (planet.getForces() > 5) {
				sendFleet(planet, planet.getForces() / 4, capital);
			}
		}

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
	
	protected boolean isNotMyPlanet(Planet planet) {
		return !planet.getPlayer().equals(this);
	}
	
	public List<Fleet> getEnemyFleets() {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : getAllFleets()) {
			if (!isMyFleet(fleet)) {
				result.add(fleet);
			}
		}
		return result;
	}

	private boolean isMyFleet(Fleet fleet) {
		return fleet.getPlayer().equals(this);
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

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
		if(this.getPlanets().size() == 0)
			return;
		
		boolean attack = false;
		for (Planet planet : this.getPlanets()) {
			List<Planet> targets = getTargets(planet);
			if (targets.size() == 0)
				continue;

			if (planet.getForces() > targets.get(0).getForces() * 1.5) {				
				this.sendFleet(planet, (int) (planet.getForces() * 0.8), targets.get(0));
				attack = true;
			} else if (targets.size() > 1 && planet.getForces() > targets.get(1).getForces() * 2) {
				this.sendFleet(planet, (int) (planet.getForces() * 0.6), targets.get(1));
				attack = true;
			} else if (targets.size() > 2 && planet.getForces() > targets.get(2).getForces() * 3) {
				this.sendFleet(planet, (int) (planet.getForces() * 0.4), targets.get(2));
				attack = true;
			} 
		}
		
		if(attack == false) {
			int forces = 0;
			
			List<Planet> targets = getTargets();
			if (targets.size() == 0)
				return;
			
			for (Planet planet : this.getPlanets()) {
				forces += planet.getForces();
			}
			
			if(forces > targets.get(0).getForces()*3) {
				for (Planet planet : this.getPlanets()) {
					this.sendFleet(planet, (int) (planet.getForces() * 0.5), targets.get(0));
				}
			}
		}
	}

	private List<Planet> getTargets() {
		List<Planet> targets = notOwnedByMe(this.getAllPlanets());
		List<Planet> result = new ArrayList<Planet>();
		
		while(targets.size() > 0) {
			List<Planet> next = mostProductive(targets);
			
			for(Planet productivePlanet : next) {
				result.add(productivePlanet);
				targets.remove(productivePlanet);
			}
		}

		return result;
	}
	
	private List<Planet> getTargets(final Planet planet) {
		List<Planet> targets = notOwnedByMe(getOtherPlanets(planet));
		List<Planet> result = new ArrayList<Planet>();
		
		while(targets.size() > 0) {
			List<Planet> next = mostProductive(targets);
			Collections.sort(next, new Comparator<Planet>() {
				@Override
				public int compare(Planet planet1, Planet planet2) {
					return (int) (planet.distanceTo(planet1) - planet.distanceTo(planet2));
				}
			});
			
			for(Planet productivePlanet : next) {
				result.add(productivePlanet);
				targets.remove(productivePlanet);
			}
		}

		return result;
	}

	private List<Planet> mostProductive(List<Planet> planets) {
		double maxProductionRate = 0.0;
		for (Planet planet : planets) {
			if (planet.getProductionRatePerSecond() > maxProductionRate) {
				maxProductionRate = planet.getProductionRatePerSecond();
			}
		}

		List<Planet> result = new ArrayList<Planet>();
		
		for (Planet planet : planets) {
			if (planet.getProductionRatePerSecond() == maxProductionRate) {
				result.add(planet);
			}
		}

		return result;
	}

	private List<Planet> notOwnedByMe(List<Planet> planets) {
		List<Planet> result = new ArrayList<Planet>();

		for (Planet planet : planets)
			if (!planet.getPlayer().equals(this))
				result.add(planet);

		return result;
	}

	private List<Planet> getOtherPlanets(Planet planet) {
		List<Planet> result = this.getAllPlanets();
		result.remove(planet);

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

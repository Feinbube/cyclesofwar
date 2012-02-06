package cyclesofwar.players.frank;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Fleet.Formation;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Petra extends Player {
	
	double attackFirst = 0.3;
	double attackSecond = 0.3;
	double attackThird = 0.3;
	
	@Override
	public void think() {
		if (this.getPlanets().size() == 0)
			return;

		boolean attack = false;
		for (Planet planet : this.getPlanets()) {
			List<Planet> targets = getTargets(planet);
			if (targets.size() == 0)
				continue;

			if (planet.getForces() > targets.get(0).getForces() * 1.5) {
				this.sendFleetUpTo(planet, (int) (planet.getForces() * attackFirst), targets.get(0));
				attack = true;
			} else if (targets.size() > 1 && planet.getForces() > targets.get(1).getForces() * 2) {
				this.sendFleetUpTo(planet, (int) (planet.getForces() * attackSecond), targets.get(1));
				attack = true;
			} else if (targets.size() > 2 && planet.getForces() > targets.get(2).getForces() * 3) {
				this.sendFleetUpTo(planet, (int) (planet.getForces() * attackThird), targets.get(2));
				attack = true;
			}
		}

		if (attack == false) {
			int forces = 0;

			List<Planet> targets = getTargets(null);
			if (targets.size() == 0)
				return;

			for (Planet planet : this.getPlanets()) {
				forces += planet.getForces();
			}

			if (forces > targets.get(0).getForces() * 3) {
				for (Planet planet : this.getPlanets()) {
					this.sendFleetUpTo(planet, (int) (planet.getForces() * 0.5), targets.get(0));
				}
			}
		}
		
		for(Fleet fleet : this.getFleets()){
			fleet.setFormation(Formation.ARROW);
		}
	}

	private List<Planet> getTargets(final Planet planet) {
		List<Planet> targets = this.getAllPlanetsButMine();
		List<Planet> result = new ArrayList<Planet>();

		while (targets.size() > 0) {
			List<Planet> next = mostProductive(targets);
			if (planet != null) {
				sortByDistanceTo(next, planet);
			}

			for (Planet productivePlanet : next) {
				result.add(productivePlanet);
				targets.remove(productivePlanet);
			}
		}

		return result;
	}

	private List<Planet> mostProductive(List<Planet> planets) {
		sortByProductivity(planets);
		double maxProductionRate = planets.get(0).getProductionRatePerSecond();

		List<Planet> result = new ArrayList<Planet>();

		for (Planet planet : planets) {
			if (planet.getProductionRatePerSecond() < maxProductionRate) {
				break;
			} else {
				result.add(planet);
			}
		}

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

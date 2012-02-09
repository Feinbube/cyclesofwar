package cyclesofwar.players.frank.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet.Formation;

public abstract class Jeesh extends Player {

	@Override
	public void think() {
		if (this.getPlanets().size() == 0)
			return;

		thinkYourself();

		setFleetFormations();
	}

	protected abstract void thinkYourself();

	private void setFleetFormations() {
		for (Fleet fleet : this.getFleets()) {
			fleet.setFormation(Formation.ARROW);
		}
	}

	protected double getlastFleetArrivalTime() {
		List<Fleet> fleets = getAllFleets();
		if (fleets.size() == 0) {
			return 0;
		}
		Fleet.sortByArrivalTime(fleets);
		return fleets.get(fleets.size() - 1).getTimeToTarget();
	}

	protected List<Planet> mostProductiveByDistance(Planet planet) {
		List<Planet> targets = this.getAllPlanetsButMine();
		List<Planet> result = new ArrayList<Planet>();

		while (targets.size() > 0) {
			List<Planet> next = mostProductive(targets);
			if (planet != null) {
				planet.sortOthersByDistance(next);
			}

			for (Planet productivePlanet : next) {
				result.add(productivePlanet);
				targets.remove(productivePlanet);
			}
		}

		return result;
	}

	protected List<Planet> mostProductive(List<Planet> planets) {
		Planet.sortByProductivity(planets);
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

	protected void attackFromAll(Planet target, double factor) {
		for (Planet planet : this.getPlanets()) {
			this.sendFleetUpTo(planet, (int) (planet.getForces() * factor), target);
		}
	}
	
	protected void fireOverproductionFromAll(Planet target, int rounds) {
		for (Planet planet : getPlanets()) {
			this.sendFleetUpTo(planet, (int) (planet.getProductionRatePerRound() * rounds), target);
		}
	}
	
	protected double valueOf(Target target) {
		Planet planet = this.firstOrNull(this.othersOnly(target.getPlanet().getOthersByDistance()));
		if (planet == null) {
			return -target.getForcesToConquer() - target.getForcesToKeep();
		} else {
			// TODO: consider nearestPlanet in the FUTURE!!
			return -target.getForcesToConquer() - target.getForcesToKeep() + target.getPlanet().getTimeTo(planet)
					* target.getPlanet().getProductionRatePerSecond();
		}
	}
	
	protected double allEnemyForces() {
		double result = 0;
		for (Player enemy : this.getOtherPlayers()) {
			result += enemy.getFullForce();
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
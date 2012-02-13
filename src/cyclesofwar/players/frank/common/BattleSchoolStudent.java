package cyclesofwar.players.frank.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public abstract class BattleSchoolStudent extends Player {

	// --------------------------------------------------------------------------------------------

	protected interface TargetSelector {
		List<Planet> getTargets(Planet planet);
	}

	protected class NearestTargetSelector implements TargetSelector {
		@Override
		public List<Planet> getTargets(Planet planet) {
			return planet.getOthersByDistance();
		}
	}

	protected class NearestOfMineTargetSelector implements TargetSelector {
		@Override
		public List<Planet> getTargets(Planet planet) {
			return mineOnly(planet.getOthersByDistance());
		}
	}

	protected class NearestEnemyTargetSelector implements TargetSelector {
		@Override
		public List<Planet> getTargets(Planet planet) {
			return hostileOnly(planet.getOthersByDistance());
		}
	}

	protected class NearestFreeTargetSelector implements TargetSelector {
		@Override
		public List<Planet> getTargets(Planet planet) {
			return freeOnly(planet.getOthersByDistance());
		}
	}

	// --------------------------------------------------------------------------------------------

	protected interface PlanetSelector {
		List<Planet> getPlanets(Player player);
	}

	protected class HinterlandPlanetSelector implements PlanetSelector {
		@Override
		public List<Planet> getPlanets(Player player) {
			List<Planet> result = new ArrayList<Planet>();

			for (Planet planet : player.getPlanets()) {
				List<Planet> neighbors = getNeighbors(planet);
				boolean hinterland = true;
				for (Planet neighbor : neighbors) {
					if (!neighbor.isFree() && neighbor.getPlayer() != player) {
						hinterland = false;
						break;
					}
				}
				if (hinterland) {
					result.add(planet);
				}
			}

			return result;
		}
	}

	// ---------------------------------------------------------------------------------------------

	protected int enemyForcesArrivingNextRound(Planet planet) {
		int result = 0;
		for (Fleet fleet : Fleet.sortedByArrivalTime(hostileOnly(getFleetsWithTarget(planet)))) {
			if (fleet.getRoundsToTarget() <= 1) {
				result += fleet.getForce();
			}
		}
		return result;
	}

	protected int myForcesArrivingNextRound(Planet planet) {
		int result = 0;
		for (Fleet fleet : Fleet.sortedByArrivalTime(mineOnly((getFleetsWithTarget(planet))))) {
			if (fleet.getRoundsToTarget() <= 1) {
				result += fleet.getForce();
			}
		}
		return result;
	}

	protected boolean isLost(Planet planet) {
		return planet.getForces() + planet.getProductionRatePerRound() + myForcesArrivingNextRound(planet) < enemyForcesArrivingNextRound(planet);
	}
	
	protected List<Planet> getNeighbors(Planet planet) {
		return firstElements(planet.getOthersByDistance(), 5);
	}

	// ---------------------------------------------------------------------------------------------

	protected double getlastFleetArrivalTime() {
		List<Fleet> fleets = getAllFleets();
		if (fleets.isEmpty()) {
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

	protected List<Planet> leastDefendedByDistance(Planet planet) {
		List<Planet> targets = this.getAllPlanetsButMine();
		List<Planet> result = new ArrayList<Planet>();

		while (targets.size() > 0) {
			List<Planet> next = leastDefended(targets);
			if (planet != null) {
				planet.sortOthersByDistance(next);
			}

			for (Planet leastDefendedPlanet : next) {
				result.add(leastDefendedPlanet);
				targets.remove(leastDefendedPlanet);
			}
		}

		return result;
	}

	protected List<Planet> leastDefended(List<Planet> planets) {
		Planet.sortByForceCount(planets);
		double maxForceCount = planets.get(0).getForces();

		List<Planet> result = new ArrayList<Planet>();

		for (Planet planet : planets) {
			if (planet.getForces() < maxForceCount) {
				break;
			} else {
				result.add(planet);
			}
		}

		return result;
	}

	protected Planet mostForcefulEnemyPlanet() {
		return Collections.max(this.getAllPlanetsButMine(), new Comparator<Planet>() {
			@Override
			public int compare(Planet one, Planet other) {
				return Double.compare(one.getForces(), other.getForces());
			}
		});
	}

	protected void attackFromAll(Planet target, double factor) {
		if (target != null) {
			for (Planet planet : this.getPlanets()) {
				this.sendFleetUpTo(planet, (int) (planet.getForces() * factor), target);
			}
		}
	}

	protected void fireOverproductionFromAll(Planet target, int rounds) {
		for (Planet planet : getPlanets()) {
			this.sendFleetUpTo(planet, (int) (planet.getProductionRatePerRound() * rounds), target);
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
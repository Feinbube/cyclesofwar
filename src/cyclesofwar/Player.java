package cyclesofwar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Player {

	public static Player NonePlayer = new NonePlayer();
	
	private Universe universe;
	
	void setUniverse(Universe universe) {
		this.universe = universe;
	}

	protected abstract void think();

	public abstract Color getPlayerBackColor();

	public abstract Color getPlayerForeColor();

	public abstract String getCreatorsName();

	public String getName() {
		return getCreatorsName() + "'s " + this.getClass().getSimpleName();
	}

	public Player freshOne() {
		for (Player other : Arena.registeredPlayers()) {
			if (other.isEqualTo(this)) {
				return other;
			}
		}

		return null;
	}

	public boolean isEqualTo(Player other) {
		return this.getName().equals(other.getName());
	}

	public boolean isInList(List<Player> players) {
		for (Player candidate : players) {
			if (candidate.isEqualTo(this)) {
				return true;
			}
		}

		return false;
	}
	

	public List<Player> listWithoutPlayer(List<Player> players) {
		List<Player> result = new ArrayList<Player>();
		for (Player candidate : players) {
			if (!candidate.isEqualTo(this)) {
				result.add(candidate);
			}
		}
		return result;
	}

	public double getGroundForce() {
		double result = 0.0;
		for (Planet planet : this.getPlanets()) {
			result += planet.getForces();
		}
		return result;
	}

	public int getSpaceForce() {
		int result = 0;
		for (Fleet fleet : this.getFleets()) {
			result += fleet.getForce();
		}
		return result;
	}

	public double getFullForce() {
		return getGroundForce() + getSpaceForce();
	}

	// Universe
	public double now() {
		return universe.getNow();
	}

	public double getStepInterval() {
		return Universe.speedOfLight;
	}

	// Players
	public List<Player> getOtherPlayers() {
		return universe.getOtherPlayers(this);
	}

	public boolean isNonePlayer(Player player) {
		return player.equals(Player.NonePlayer);
	}

	// Planets
	public List<Planet> getPlanets() {
		return universe.getPlanetsOf(this);
	}

	public List<Planet> getAllPlanets() {
		return universe.getAllPlanets();
	}

	public List<Planet> getFreePlanets() {
		return universe.getPlanetsOf(Player.NonePlayer);
	}

	public List<Planet> getPlanetsOf(Player player) {
		return universe.getPlanetsOf(player);
	}

	public boolean isMyPlanet(Planet planet) {
		return planet.getPlayer().equals(this);
	}

	public List<Planet> getAllPlanetsButMine() {
		List<Planet> result = new ArrayList<Planet>();
		for (Planet planet : getAllPlanets())
			if (!isMyPlanet(planet))
				result.add(planet);
		return result;
	}

	public List<Planet> getAllPlanetsButThis(Planet planet) {
		List<Planet> result = getAllPlanets();
		result.remove(planet);
		return result;
	}

	public Planet getRandomPlanet(Planet start) {
		return getAllPlanetsButThis(start).get(getRandomInt(getAllPlanetsButThis(start).size()));
	}

	public static void sortByForceCount(List<Planet> planets) {
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return Double.compare(planet2.getForces(), planet1.getForces());
			}
		});
	}
	
	public Planet getNearestPlanet(Planet planet) {
		List<Planet> planets = this.getAllPlanetsButThis(planet);
		sortByDistanceTo(planets, planet);
		return planets.get(0);
	}
	
	public Planet getNearestFreeOrEnemyPlanet(Planet planet) {
		List<Planet> planets = this.getAllPlanetsButMine();
		if (planets.size() == 0) {
			return null;
		}

		sortByDistanceTo(planets, planet);
		return planets.get(0);
	}

	public static void sortByDistanceTo(List<Planet> planets, final Planet planet) {
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return Double.compare(planet.distanceTo(planet1), planet.distanceTo(planet2));
			}
		});
	}

	public static void sortByProductivity(List<Planet> planets) {
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return Double.compare(planet2.getProductionRatePerSecond(), planet1.getProductionRatePerSecond());
			}
		});
	}

	// Fleets
	public List<Fleet> getFleets() {
		return universe.getFleetsOf(this);
	}

	public List<Fleet> getAllFleets() {
		return universe.getAllFleets();
	}

	public List<Fleet> getFleetsOf(Player player) {
		return universe.getFleetsOf(player);
	}

	public boolean isMyFleet(Fleet fleet) {
		return fleet.getPlayer().equals(this);
	}

	public List<Fleet> getAllEnemyFleets() {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : getFleets())
			if (!isMyFleet(fleet))
				result.add(fleet);
		return result;
	}

	public List<Fleet> getFleetsWithTarget(Planet target) {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : this.getFleets()) {
			if (fleet.getTarget().equals(target)) {
				result.add(fleet);
			}
		}
		return result;
	}

	public static void sortByArrivalTime(List<Fleet> fleets) {
		Collections.sort(fleets, new Comparator<Fleet>() {
			@Override
			public int compare(Fleet one, Fleet other) {
				return Double.compare(one.timeToTarget(), other.timeToTarget());
			}
		});
	}

	public Fleet sendFleet(Planet planet, int force, Planet target) {
		return universe.SendFleet(this, planet, force, target);
	}

	public Fleet sendFleetUpTo(Planet planet, int force, Planet target) {
		int forcesToSend = (int) Math.min(force, planet.getForces());
		if (forcesToSend > 0) {
			return universe.SendFleet(this, planet, forcesToSend, target);
		} else {
			return null;
		}
	}

	// random
	public double getRandomDouble() {
		return universe.getRandomDouble();
	}

	public int getRandomInt(int max) {
		return universe.getRandomInt(max);
	}
}
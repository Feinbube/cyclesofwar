package cyclesofwar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Player {
	
	protected static Player NonePlayer = new NonePlayer();

	protected abstract void think();

	protected abstract Color getPlayerBackColor();

	protected abstract Color getPlayerForeColor();

	protected abstract String getCreatorsName();

	double getFullForce() {
		double result = 0.0;
		for (Planet planet : getPlanets()) {
			result += planet.forces;
		}
		for (Fleet fleet : getFleets()) {
			result += fleet.force;
		}
		return result;
	}

	String getName() {
		return getCreatorsName() + "'s " + this.getClass().getSimpleName();
	}

	// Universe
	protected double now() {
		return Universe.INSTANCE.now;
	}

	protected double getStepInterval() {
		return Universe.speedOfLight;
	}

	// Players
	protected List<Player> getOtherPlayers() {
		return Universe.INSTANCE.OtherPlayers(this);
	}

	// Planets
	public List<Planet> getPlanets() {
		return Universe.INSTANCE.PlanetsOfPlayer(this);
	}

	protected List<Planet> getAllPlanets() {
		return Universe.INSTANCE.AllPlanets();
	}

	protected List<Planet> getFreePlanets() {
		return Universe.INSTANCE.PlanetsOfPlayer(Player.NonePlayer);
	}

	protected List<Planet> getPlanetsOf(Player player) {
		return Universe.INSTANCE.PlanetsOfPlayer(player);
	}

	protected boolean isMyPlanet(Planet planet) {
		return planet.getPlayer().equals(this);
	}

	protected List<Planet> getAllPlanetButMine() {
		List<Planet> result = new ArrayList<Planet>();

		for (Planet planet : getAllPlanets())
			if (!isMyPlanet(planet))
				result.add(planet);

		return result;
	}
	
	protected List<Planet> getAllPlanetsButThis(Planet planet) {
		List<Planet> result = getAllPlanets();
		result.remove(planet);
		return result;
	}
	
	protected Planet getRandomPlanet(Planet start) {
		return getAllPlanetsButThis(start).get(getRandomInt(getAllPlanetsButThis(start).size()));
	}

	protected static void sortByForceCount(List<Planet> planets) {
		Collections.sort(planets, new Comparator<Planet>() {

			@Override
			public int compare(Planet planet1, Planet planet2) {
				return (int) (planet2.getForces() - planet1.getForces());
			}
		});
	}

	protected static void sortByDistanceTo(List<Planet> planets, final Planet planet) {
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return Double.compare(planet.distanceTo(planet1), planet.distanceTo(planet2));
			}
		});
	}

	protected static void sortByProductivity(List<Planet> planets) {
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return (int) (planet2.getProductionRatePerSecond() - planet1.getProductionRatePerSecond());
			}
		});
	}

	// Fleets
	protected List<Fleet> getFleets() {
		return Universe.INSTANCE.FleetsOfPlayer(this, this);
	}

	protected List<Fleet> getAllFleets() {
		return Universe.INSTANCE.AllFleets(this);
	}

	protected List<Fleet> getFleetsOf(Player player) {
		return Universe.INSTANCE.FleetsOfPlayer(this, player);
	}

	protected boolean isMyFleet(Fleet fleet) {
		return fleet.getPlayer().equals(this);
	}

	protected List<Fleet> getAllEnemyFleets() {
		List<Fleet> result = new ArrayList<Fleet>();

		for (Fleet fleet : getAllFleets())
			if (!isMyFleet(fleet))
				result.add(fleet);

		return result;
	}

	protected List<Fleet> getFleetsWithTarget(Planet target) {
		List<Fleet> result = new ArrayList<Fleet>();
		
		for(Fleet fleet : this.getAllFleets()) {
			if(fleet.getTarget().equals(target)) {
				result.add(fleet);
			}
		}
		
		return result;
	}
	
	protected static void sortByArrivalTime(List<Fleet> fleets) {
		Collections.sort(fleets, new Comparator<Fleet>() {

			@Override
			public int compare(Fleet one, Fleet other) {
				return Double.compare(one.timeToTarget(), other.timeToTarget());
			}
		});
	}	
	
	protected void sendFleet(Planet planet, int force, Planet target) {
		Universe.INSTANCE.SendFleet(this, planet, force, target);
	}
	
	protected Fleet sendNewFleet(Planet planet, int force, Planet target) {
		return Universe.INSTANCE.SendFleet(this, planet, force, target);
	}

	protected Fleet sendFleetUpTo(Planet planet, int force, Planet target) {
		int forcesToSend = (int) Math.min(force, planet.getForces());
		if (forcesToSend > 0) {
			return Universe.INSTANCE.SendFleet(this, planet, forcesToSend, target);
		} else {
			return null;
		}
	}

	// random
	protected double getRandomDouble() {
		return Universe.INSTANCE.getRandomDouble();
	}

	protected int getRandomInt(int max) {
		return Universe.INSTANCE.getRandomInt(max);
	}
}
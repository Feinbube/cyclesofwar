package cyclesofwar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public abstract class Player {
	
	private Universe universe;
	
	public static Player NonePlayer = new NonePlayer();
	
	protected abstract void think();
	
	public abstract Color getPlayerBackColor();
	public abstract Color getPlayerForeColor();
	
	public abstract String getCreatorsName();
	
	public String getName() {
		return getCreatorsName() + "'s " + this.getClass().getSimpleName();
	}
	
	public Player freshOne(){
		for(Player other : Arena.registeredPlayers()){
			if(other.isEqualTo(this)) {
				return other;
			}
		}
		
		return null;
	}

	public boolean isEqualTo(Player other) {
		return this.getName().equals(other.getName());
	}
	
	public boolean isInList(List<Player> players) {
		for(Player candidate : players) {
			if(candidate.isEqualTo(this)) {
				return true;
			}
		}
		
		return false;
	}
	
	public double getGroundForce() {
		double result = 0.0;
		for (Planet planet : universe.PlanetsOfPlayer(this)) {
			result += planet.getForces();
		}
		return result;
	}
	
	public int getVisibleSpaceForce() {
		int result = 0;
		for (Fleet fleet : universe.FleetsOfPlayer(NonePlayer, this)) {
			result += fleet.force;
		}
		return result;
	}
	
	public double getVisibleFullForce() {
		return getGroundForce() + getVisibleSpaceForce();
	}

	// Universe
	protected double now() {
		return universe.getNow();
	}

	protected double getStepInterval() {
		return Universe.speedOfLight;
	}

	// Players
	protected List<Player> getOtherPlayers() {
		return universe.OtherPlayers(this);
	}
	
	protected boolean isNonePlayer(Player player) {
		return player.equals(Player.NonePlayer);
	}

	// Planets
	public List<Planet> getPlanets() {
		return universe.PlanetsOfPlayer(this);
	}

	protected List<Planet> getAllPlanets() {
		return universe.AllPlanets();
	}

	protected List<Planet> getFreePlanets() {
		return universe.PlanetsOfPlayer(Player.NonePlayer);
	}

	protected List<Planet> getPlanetsOf(Player player) {
		return universe.PlanetsOfPlayer(player);
	}

	protected boolean isMyPlanet(Planet planet) {
		return planet.getPlayer().equals(this);
	}

	protected List<Planet> getAllPlanetsButMine() {
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
		return universe.FleetsOfPlayer(this, this);
	}
	
	public List<Fleet> getVisibleFleets() {
		return universe.FleetsOfPlayer(NonePlayer, this);
	}

	protected List<Fleet> getAllFleets() {
		return universe.AllFleets(this);
	}

	protected List<Fleet> getFleetsOf(Player player) {
		return universe.FleetsOfPlayer(this, player);
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
	
	protected Fleet sendFleet(Planet planet, int force, Planet target) {
		return universe.SendFleet(this, planet, force, target);
	}

	protected Fleet sendFleetUpTo(Planet planet, int force, Planet target) {
		int forcesToSend = (int) Math.min(force, planet.getForces());
		if (forcesToSend > 0) {
			return universe.SendFleet(this, planet, forcesToSend, target);
		} else {
			return null;
		}
	}

	// random
	protected double getRandomDouble() {
		return universe.getRandomDouble();
	}

	protected int getRandomInt(int max) {
		return universe.getRandomInt(max);
	}

	void setUniverse(Universe universe) {
		this.universe = universe;
	}
}
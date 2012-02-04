package cyclesofwar.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet.Formation;

public class Bean extends Player {

	List<Fleet> fleetsHandled = new ArrayList<Fleet>();

	int nothingHappend = 0;

	@Override
	public void think() {
		if (this.getPlanets().size() == 0)
			return;

		List<Fleet> enemyFleets = unhandledFleets(this.getAllEnemyFleets());
		if (this.getAllEnemyFleets().size() == 0) {
			nothingHappend++;
			if (nothingHappend > 10) {
				fireOverproduction(10);
				nothingHappend = 0;
			}
		} else {
			sortByValue(enemyFleets);
			for (Fleet fleet : enemyFleets) {
				handleEnemyFleet(fleet);
			}
		}
		
		if(getGroundForceOf(this) > allForces() * 2) {
			showDown();
		}

		for (Fleet fleet : this.getFleets()) {
			fleet.setFormation(Formation.ARROW);
		}
	}

	private int allForces() {
		int result = 0;
		List<Player> enemies = this.getOtherPlayers();
		for(Player enemy : enemies) {
			result += getForceOf(enemy);				
		}
		return result;
	}

	private void showDown() {
		List<Planet> enemyPlanets = this.getAllPlanetButMine();
		sortByForceCount(enemyPlanets);
		
		for(Planet target : enemyPlanets) {
			double currentForces = target.getForces();
			
			List<Planet> myPlanets = this.getPlanets();
			sortByDistanceTo(myPlanets, target);
			
			for(Planet planet : myPlanets) {
				currentForces -= attackForce(target, planet);
				if(currentForces < 0) {
					break;
				}
			}
		}
	}

	private int attackForce(Planet target, Planet planet) {
		int force = getFleetsToSend(planet, target);
		return sendFleetUpTo(planet, force, target);
	}

	private int getGroundForceOf(Player player) {
		int force = 0;
		for(Planet planet : getPlanetsOf(player)) {
			force += (int)planet.getForces();
		}
		return force;
	}
	
	private int getSpaceForceOf(Player player) {
		int force = 0;
		for(Fleet fleet : getFleetsOf(player)) {
			force += (int)fleet.getForce();
		}
		return force;
	}
	
	private int getForceOf(Player player) {
		return getGroundForceOf(player) + getSpaceForceOf(player);
	}

	private void fireOverproduction(int rounds) {
		List<Planet> planets = this.getAllPlanetButMine();
		removePlanetsOnTrack(planets);
		sortByForceCount(planets);
		if(planets.size() == 0) {
			return;
		}
		for(Planet planet : getPlanets()) {
			this.sendFleetUpTo(planet, (int)(planet.getProductionRatePerSecond()*rounds), planets.get(planets.size()-1));
		}	
	}

	private void removePlanetsOnTrack(List<Planet> planets) {
		for(Fleet fleet : this.getFleets()) {
			if(planets.contains(fleet.getTarget())) {
				planets.remove(fleet.getTarget());
			}
		}
	}

	private void sortByValue(List<Fleet> enemyFleets) {
		Collections.sort(enemyFleets, new Comparator<Fleet>() {
			@Override
			public int compare(Fleet fleet1, Fleet fleet2) {
				return (int) (troopsOnTargetAfterEncounter(fleet1) - troopsOnTargetAfterEncounter(fleet2));
			}
		});
	}

	private List<Fleet> unhandledFleets(List<Fleet> enemyFleets) {
		List<Fleet> fleetsHandledSlim = new ArrayList<Fleet>();

		for (Fleet fleet : fleetsHandled)
			if (enemyFleets.contains(fleet))
				fleetsHandledSlim.add(fleet);

		fleetsHandled = fleetsHandledSlim;

		List<Fleet> result = new ArrayList<Fleet>();

		for (Fleet fleet : enemyFleets)
			if (!fleetsHandledSlim.contains(fleet))
				result.add(fleet);

		return result;
	}

	private void handleEnemyFleet(Fleet enemyFleet) {

		List<Planet> planets = this.getPlanets();
		sortByDistanceTo(planets, enemyFleet.getTarget());

		// TODO TEAMWORK!
		for (Planet planet : planets) {
			if (planet.timeTo(enemyFleet.getTarget()) > enemyFleet.timeToTarget()) {
				int forcesToSend = getFleetsToSend(planet, enemyFleet);
				if (planet.getForces() > forcesToSend) {
					this.sendFleetUpTo(planet, forcesToSend, enemyFleet.getTarget());
					fleetsHandled.add(enemyFleet);
					return;
				}
			}
		}
	}

	private double troopsOnTargetAfterEncounter(Fleet enemyFleet) {
		Planet target = enemyFleet.getTarget();
		int time = (int) enemyFleet.timeToTarget() + 1;

		double troopsOnPlanet = target.getForces();
		if (!target.getPlayer().equals(NonePlayer)) {
			troopsOnPlanet += time * target.getProductionRatePerSecond();
		}

		if (target.getPlayer().equals(enemyFleet.getPlayer())) {
			troopsOnPlanet += enemyFleet.getForce();
		} else {
			troopsOnPlanet -= enemyFleet.getForce();
		}

		if (target.getPlayer().equals(this)) {
			return -troopsOnPlanet;
		} else {
			return Math.abs(troopsOnPlanet);
		}
	}
	
	private int getFleetsToSend(Planet planet, Fleet enemyFleet) {
		double troopsOnPlanet = troopsOnTargetAfterEncounter(enemyFleet);

		double late = (int) planet.timeTo(enemyFleet.getTarget()) + 1 - (int) (enemyFleet.timeToTarget());
		troopsOnPlanet += late * enemyFleet.getTarget().getProductionRatePerSecond();

		return (int) (troopsOnPlanet + 2);
	}
	
	private int getFleetsToSend(Planet planet, Planet target) {
		double troopsOnPlanet = target.getForces();
		
		double time = (int) planet.timeTo(target) + 1;
		if (!target.getPlayer().equals(NonePlayer)) {
			troopsOnPlanet += time * target.getProductionRatePerSecond();
		}
		return (int) (troopsOnPlanet + 2);
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

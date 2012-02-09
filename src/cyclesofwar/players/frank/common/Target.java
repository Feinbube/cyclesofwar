package cyclesofwar.players.frank.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Target {
	private int forcesToConquer;
	private int forcesToKeep;
	private Planet planet;
	private double value;

	public int getForcesToConquer() {
		return forcesToConquer;
	}

	public int getForcesToKeep() {
		return forcesToKeep;
	}

	public Planet getPlanet() {
		return planet;
	}

	Target(Jeesh player, int forcesToConquer, int forcesToKeep, Planet planet) {
		this.forcesToConquer = forcesToConquer;
		this.forcesToKeep = forcesToKeep;
		this.planet = planet;
		this.value = player.valueOf(this);
	}

	public static List<Target> removePlanetsThatAreFine(List<Target> targets, Player player) {
		List<Target> result = new ArrayList<Target>();
		for (Target target : targets) {
			if (target.forcesToConquer > 0 || target.forcesToKeep > 0) {
				result.add(target);
			}
		}

		return result;
	}

	public static Target bestTarget(Jeesh player, Planet origin) {
		return bestTarget(player, player.getAllPlanets(), origin);
	}

	public static Target bestTarget(Jeesh player, List<Planet> targetPlanets, Planet origin) {
		List<Target> targets = bestTargets(player, origin);
		if(targets == null || targets.isEmpty()) {
			return null;
		} else {
			return targets.get(0);
		}
	}

	public static List<Target> bestTargets(Jeesh player, Planet origin) {
		return bestTargets(player, player.getAllPlanets(), origin);
	}

	public static List<Target> bestTargets(Jeesh player, List<Planet> targetPlanets, Planet origin) {
		List<Target> targets = new ArrayList<Target>();
		for (Planet targetPlanet : targetPlanets) {
			Prediction predictionAtArrivalTime = Prediction.getPrediction(player, targetPlanet, origin.getTimeTo(targetPlanet));

			int fleetsToConquer = (int) predictionAtArrivalTime.getForces() + 1;
			if (predictionAtArrivalTime.getPlayer() == player) {
				fleetsToConquer = -fleetsToConquer;
			}

			int fleetsToKeep = getFleetsToKeep(player, predictionAtArrivalTime, fleetsToConquer, origin.getTimeTo(targetPlanet));

			if (!targetAlreadyHandled(player, targetPlanet, origin.getTimeTo(targetPlanet))) {
				targets.add(new Target(player, fleetsToConquer, fleetsToKeep, targetPlanet));
			}
		}

		targets = Target.removePlanetsThatAreFine(targets, player);
		targets = mostValueByDistance(player, targets, origin);

		if (targets.isEmpty()) {
			return null;
		} else {
			return targets;
		}
	}
	
	protected static List<Target> mostValueByDistance(Jeesh player, List<Target> targets, Planet planet) {
		List<Target> result = new ArrayList<Target>();

		while (targets.size() > 0) {
			List<Target> next = mostValued(player, targets);
			if (planet != null) {
				sortByDistanceTo(next, planet);
			}

			for (Target productivePlanet : next) {
				result.add(productivePlanet);
				targets.remove(productivePlanet);
			}
		}
		return result;
	}

	private static void sortByDistanceTo(List<Target> targets, final Planet planet) {
		Collections.sort(targets, new Comparator<Target>() {
			@Override
			public int compare(Target one, Target other) {
				return Double.compare(planet.getDistanceTo(one.planet), planet.getDistanceTo(other.planet));
			}
		});
	}

	protected static List<Target> mostValued(Jeesh player, List<Target> targets) {
		sortByValue(player, targets);
		double maxValue = targets.get(0).value;

		List<Target> result = new ArrayList<Target>();
		for (Target target : targets) {
			if (target.value < maxValue) {
				break;
			} else {
				result.add(target);
			}
		}
		return result;
	}
	
	private static boolean targetAlreadyHandled(Player player, Planet target, double arrivalTime) {
		for (Fleet fleet : player.getFleets()) {
			if (fleet.getTarget() == target && fleet.getTimeToTarget() > arrivalTime) {
				return true;
			}
		}

		return false;
	}

	private static void sortByValue(final Jeesh player, List<Target> targets) {
		Collections.sort(targets, new Comparator<Target>() {
			@Override
			public int compare(Target one, Target other) {
				return Double.compare(player.valueOf(other), player.valueOf(one));
			}
		});
	}

	private static int getFleetsToKeep(Jeesh player, Prediction prediction, int fleetsToConquer, double time) {
		prediction.update(player, fleetsToConquer, time);

		List<Fleet> fleets = player.getFleetsWithTarget(prediction.getPlanet());
		Fleet.sortByArrivalTime(fleets);

		for (Fleet fleet : fleets) {
			if (fleet.getTimeToTarget() < time) {
				continue;
			}
			prediction.update(fleet);
		}

		if (prediction.getPlayer() == player) {
			return -(int) prediction.getForces();
		} else {
			return (int) prediction.getForces();
			// MAYBE THE BEST WAY: always return 0;
		}
	}
}

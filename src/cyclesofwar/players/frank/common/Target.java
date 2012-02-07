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

	public int getForcesToConquer() {
		return forcesToConquer;
	}

	public int getForcesToKeep() {
		return forcesToKeep;
	}

	public Planet getPlanet() {
		return planet;
	}

	Target(int forcesToConquer, int forcesToKeep, Planet planet) {
		this.forcesToConquer = forcesToConquer;
		this.forcesToKeep = forcesToKeep;
		this.planet = planet;
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
		return bestTargets(player, origin).get(0);
	}

	public static Target bestTarget(Jeesh player, List<Planet> targetPlanets, Planet origin) {
		return bestTargets(player, targetPlanets, origin).get(0);
	}

	public static List<Target> bestTargets(Jeesh player, Planet origin) {
		return bestTargets(player, player.getAllPlanets(), origin);
	}

	public static List<Target> bestTargets(Jeesh player, List<Planet> targetPlanets, Planet origin) {
		List<Target> targets = new ArrayList<Target>();
		for (Planet targetPlanet : targetPlanets) {
			Prediction predictionAtArrivalTime = Prediction.getPrediction(player, targetPlanet, origin.timeTo(targetPlanet));

			int fleetsToConquer = (int) predictionAtArrivalTime.getForces() + 1;
			if (predictionAtArrivalTime.getPlayer().equals(player)) {
				fleetsToConquer = -fleetsToConquer;
			}

			int fleetsToKeep = getFleetsToKeep(player, predictionAtArrivalTime, fleetsToConquer, origin.timeTo(targetPlanet));

			if (!targetAlreadyHandled(player, targetPlanet, origin.timeTo(targetPlanet))) {
				targets.add(new Target(fleetsToConquer, fleetsToKeep, targetPlanet));
			}
		}

		targets = Target.removePlanetsThatAreFine(targets, player);
		sortByValue(player, targets);

		if (targets.size() == 0) {
			return null;
		} else {
			return targets;
		}
	}

	private static boolean targetAlreadyHandled(Player player, Planet target, double arrivalTime) {
		for (Fleet fleet : player.getFleets()) {
			if (fleet.getTarget().equals(target) && fleet.timeToTarget() > arrivalTime) {
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
		Player.sortByArrivalTime(fleets);

		for (Fleet fleet : fleets) {
			if (fleet.timeToTarget() < time) {
				continue;
			}
			prediction.update(fleet);
		}

		if (prediction.getPlayer().equals(player)) {
			return -(int) prediction.getForces();
		} else {
			return (int) prediction.getForces();
			// MAYBE THE BEST WAY: always return 0;
		}
	}
}

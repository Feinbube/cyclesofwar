package cyclesofwar.players.frank;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet.Formation;

public class Alai extends Player {

	static class Target {
		int forcesToConquer;
		int forcesToKeep;
		Planet planet;

		public Target(int forcesToConquer, int forcesToKeep, Planet planet) {
			super();
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
	}

	static class Situation {
		Player owner;
		Planet planet;
		double forces;
		double lastFleetArrivedAt;

		public Situation(Planet planet) {
			super();
			this.owner = planet.getPlayer();
			this.planet = planet;
			this.forces = planet.getForces();
			this.lastFleetArrivedAt = 0.0;
		}

		static void sortByForceCount(List<Situation> situations) {
			Collections.sort(situations, new Comparator<Situation>() {

				@Override
				public int compare(Situation one, Situation other) {
					return Double.compare(one.forces, other.forces);
				}
			});
		}

		static List<Situation> enemyPlanetsOnly(Player player, List<Situation> targets) {
			List<Situation> result = new ArrayList<Situation>();
			for (Situation target : targets) {
				if (!target.owner.equals(player)) {
					result.add(target);
				}
			}

			return result;
		}

		void update(Fleet fleet) {
			update(fleet.getPlayer(), fleet.getForce(), fleet.timeToTarget());
		}

		void update(Player player, int force, double timeToTarget) {
			double late = timeToTarget - lastFleetArrivedAt;
			this.forces += producedForces(late);

			if (this.owner.equals(player)) {
				this.forces += force;
			} else {
				this.forces -= force;
				if (this.forces < 0) {
					this.owner = player;
					this.forces *= -1;
				}
			}

			lastFleetArrivedAt = timeToTarget;
		}

		double producedForces(double duration) {
			if (this.owner.equals(NonePlayer)) {
				return 0;
			} else {
				return duration * this.planet.getProductionRatePerSecond();
			}
		}
	}

	@Override
	public void think() {
		if (this.getPlanets().size() == 0)
			return;

		for (Planet planet : this.getPlanets()) {
			List<Planet> targets = this.getAllPlanets();
			Target target = bestTarget(targets, planet);

			if (target == null) {
				return; // already won :D
			}

			if ((int) target.forcesToConquer > 0 && planet.getForces() >= target.forcesToConquer) {
				this.sendFleet(planet, target.forcesToConquer, target.planet);
			}

			// TODO send fleets to keep as well?
			/*
			 * if (target.forcesToKeep > 0 && planet.getForces() >=
			 * target.forcesToKeep) { this.sendNewFleet(planet,
			 * (int)(target.forcesToKeep + 1), target.planet); }
			 */
		}

		// TODO Teamwork
		// List<Planet> targets = this.getAllPlanets();
		// Target target = bestTarget(targets, planet);
		if (this.getAllFleets().size() == 0) {
			Planet home = this.getPlanets().get(0);
			List<Planet> enemies = this.getAllPlanetsButMine();
			sortByDistanceTo(enemies, home);
			for (Planet planet : this.getPlanets()) {
				if (planet.getForces() / 2 >= 1) {
					this.sendFleet(planet, (int) (planet.getForces() / 2), enemies.get(0));
				}
			}
		}

		for (Fleet fleet : this.getFleets()) {
			fleet.setFormation(Formation.ARROW);
		}
	}

	private Target bestTarget(List<Planet> targetPlanets, Planet orgin) {
		List<Target> targets = new ArrayList<Target>();
		for (Planet targetPlanet : targetPlanets) {
			Situation situationAtArrivalTime = situationAtArrivalTime(targetPlanet, orgin.timeTo(targetPlanet));

			int fleetsToConquer = (int) situationAtArrivalTime.forces + 1;
			if (situationAtArrivalTime.owner.equals(this)) {
				fleetsToConquer = -fleetsToConquer;
			}

			int fleetsToKeep = getFleetsToKeep(situationAtArrivalTime, fleetsToConquer, orgin.timeTo(targetPlanet));

			if (!targetAlreadyHandled(targetPlanet, orgin.timeTo(targetPlanet))) {
				targets.add(new Target(fleetsToConquer, fleetsToKeep, targetPlanet));
			}
		}

		targets = Target.removePlanetsThatAreFine(targets, this);
		sortByValue(targets);

		if (targets.size() == 0) {
			return null;
		} else {
			return targets.get(0);
		}
	}

	private boolean targetAlreadyHandled(Planet target, double arrivalTime) {
		for (Fleet fleet : this.getFleets()) {
			if (fleet.getTarget().equals(target) && fleet.timeToTarget() > arrivalTime) {
				return true;
			}
		}

		return false;
	}

	private void sortByValue(List<Target> targets) {
		Collections.sort(targets, new Comparator<Target>() {
			@Override
			public int compare(Target one, Target other) {
				return Double.compare(valueOf(other), valueOf(one));
			}
		});
	}

	private double valueOf(Target target) {
		return -target.forcesToConquer - target.forcesToKeep;
		// TODO: consider nearestPlanet in the FUTURE!!
		// + target.planet.timeTo(nearestPlanet(target.planet)) *
		// target.planet.getProductionRatePerSecond();
	}

	private int getFleetsToKeep(Situation situationAtArrivalTime, int fleetsToConquer, double arrivalTime) {
		situationAtArrivalTime.update(this, fleetsToConquer, arrivalTime);

		List<Fleet> fleets = getFleetsWithTarget(situationAtArrivalTime.planet);
		sortByArrivalTime(fleets);

		for (Fleet fleet : fleets) {
			if (fleet.timeToTarget() < arrivalTime) {
				continue;
			}
			situationAtArrivalTime.update(fleet);
		}

		if (situationAtArrivalTime.owner.equals(this)) {
			return -(int) situationAtArrivalTime.forces;
		} else {
			return (int) situationAtArrivalTime.forces; // MAYBE THE BEST WAY:
														// always return 0;
		}
	}

	Situation situationAtArrivalTime(Planet planet, double arrivalTime) {
		Situation situation = new Situation(planet);

		List<Fleet> fleets = getFleetsWithTarget(planet);
		sortByArrivalTime(fleets);

		for (Fleet fleet : fleets) {
			if (fleet.timeToTarget() > arrivalTime) {
				break;
			}
			situation.update(fleet);
		}

		situation.update(this, 0, arrivalTime);

		return situation;
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

package cyclesofwar.players.frank.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet.Formation;

public abstract class Jeesh extends Player {

	@Override
	public void think() {
		if (this.getPlanets().isEmpty())
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

	protected double valueOf(Target target) {
		Planet planet = Player.firstOrNull(this.othersOnly(target.getPlanet().getOthersByDistance()));
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

	public void spreadTheWordStrategy() {
		for (Planet planet : getPlanets()) {
			if (planet.getForces() < 1) {
				continue;
			}
			
			List<Prediction> predictions = Prediction.getAllPredictions(this);
			Prediction.sortByDistanceTo(predictions, planet);

			Prediction localPrediction = predictions.get(0);
			predictions.remove(localPrediction);

			for (Prediction prediction : predictions) {
				if (prediction.getPlayer() == this && prediction.getForces() < localPrediction.getForces()) {
					sendFleetUpTo(planet, (int) (localPrediction.getForces() - prediction.getForces()), prediction.getPlanet());
				} else if (prediction.getPlayer() != this) {
					sendFleetUpTo(planet, (int) (prediction.getForces() + 1), prediction.getPlanet());
				}
				if (planet.getForces() < 1) {
					break;
				}
			}
		}
	}
	
	public void alwaysTheSecondStrategy() {
		while (true) {
			boolean send = false;

			for (Planet planet : this.getPlanets()) {
				Target target = Target.bestTarget(this, planet);
				if (target == null) {
					return; // already won :D
				} else if ((int) target.getForcesToConquer() > 0 && planet.getForces() >= target.getForcesToConquer()) {
					this.sendFleet(planet, target.getForcesToConquer(), target.getPlanet());
					send = true;
				}
				
				// TODO consider keeping the planet as well
			}

			if (!send) {
				break;
			}
		}

		// TODO Teamwork
		if (this.getFleets().size() == 0) {
			attackFromAll(firstOrNull(othersOnly((this.getPlanets().get(0).getOthersByDistance()))), 0.5);
		}
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
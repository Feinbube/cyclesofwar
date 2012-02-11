package cyclesofwar.players.frank.common;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Fleet.Formation;

public abstract class Jeesh extends BattleSchoolStudent {

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
			fleet.setFormation(Formation.V);
		}
	}

	protected void evacutatePlanetsThatAreLost(TargetSelector selector) {
		for (Planet planet : this.getPlanets()) {
			if (isLost(planet)) {
				sendFleetUpTo(planet, planet.getForces(), firstOrNull(selector.getTargets(planet)));
			}
		}
	}

	protected void colonizeFree(PlanetSelector from) {
		for (Planet planet : from.getPlanets(this)) {
			for (Planet target : NearestFree.getTargets(planet)) {
				if ((int) planet.getForces() > target.getForces() + 1) {
					sendFleet(planet, (int) target.getForces() + 1, target);
				}
			}
		}
	}

	// -FULL-STRATEGIES----------------------------------------------------------------------------

	protected void strategyRabbit(int count) {
		strategyBraveRabbit(count); // jump around
		evacutatePlanetsThatAreLost(NearestEnemy); // flee
	}

	protected void strategyBraveRabbit(int count) {
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		for (Planet planet : this.getPlanets()) {
			Planet target = this.hostileOnly(planet.getOthersByDistance()).get(0);

			// jump around
			if (planet.getForces() >= count) {
				sendFleet(planet, count, target);
			}
		}
	}

	protected void spreadTheWordStrategy(int barrier) {
		for (Planet planet : getPlanets()) {
			if (planet.getForces() < barrier) {
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
				if (planet.getForces() < barrier) {
					break;
				}
			}
		}
	}

	protected void strategyAlwaysTheSecond() {
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
		if (this.getFleets().isEmpty()) {
			attackFromAll(firstOrNull(hostileOnly((this.getPlanets().get(0).getOthersByDistance()))), 0.5);
		}
	}

	protected void strategyAlwaysTheSecond2() {
		for (Planet planet : this.getPlanets()) {
			Target target = Target.bestTarget(this, planet);
			if (target == null) {
				return; // already won :D
			} else if ((int) target.getForcesToConquer() > 0 && planet.getForces() >= target.getForcesToConquer()) {
				this.sendFleet(planet, target.getForcesToConquer(), target.getPlanet());
			}

			// TODO send fleets to keep as well?
			/*
			 * if (target.forcesToKeep > 0 && planet.getForces() >=
			 * target.forcesToKeep) { this.sendNewFleet(planet,
			 * (int)(target.forcesToKeep + 1), target.planet); }
			 */
		}

		// TODO Teamwork
		if (this.getFleets().isEmpty()) {
			attackFromAll(firstOrNull(hostileOnly((this.getPlanets().get(0).getOthersByDistance()))), 0.5);
		}
	}

	protected void strategyAttackMostProductiveByDistance() {
		boolean attacked = false;
		for (Planet planet : this.getPlanets()) {
			List<Planet> targets = mostProductiveByDistance(planet);
			if (targets.isEmpty())
				continue;

			attacked = attack(attacked, planet, targets, 0, 1.5, 0.3);
			attacked = attack(attacked, planet, targets, 1, 2, 0.3);
			attacked = attack(attacked, planet, targets, 2, 3, 0.3);
		}

		if (attacked == false) {
			List<Planet> targets = mostProductiveByDistance(null);
			if (!targets.isEmpty() && this.getGroundForce() > targets.get(0).getForces() * 3) {
				attackFromAll(targets.get(0), 0.5);
			}
		}
	}

	private boolean attack(boolean attacked, Planet planet, List<Planet> targets, int i, double compareFactor, double attacFactor) {
		if (!attacked && targets.size() > i && planet.getForces() > targets.get(i).getForces() * compareFactor) {
			this.sendFleetUpTo(planet, (int) (planet.getForces() * attacFactor), targets.get(i));
			return true;
		} else {
			return false;
		}
	}

	protected void strategyFightMultipleEnemies() {
		double forcesOfLargestEnemy = mostForcefulEnemyPlanet().getForces();
		double myForce = this.getGroundForce();

		Planet target = null;
		if (myForce > forcesOfLargestEnemy * 3) {
			target = mostProductiveByDistance(this.getPlanets().get(0)).get(0);

		}

		if (target != null) {
			for (Planet planet : getPlanets()) {
				sendFleetUpTo(planet, (int) (planet.getForces() - forcesOfLargestEnemy), target);
			}
		}
	}

	protected void strategyCollective() {
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() > 30) {
				sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planet.getOthersByDistance())));
				sendFleetUpTo(planet, 5, atIndexOrNull(mineOnly(planet.getOthersByDistance()), 0));
				sendFleetUpTo(planet, 5, atIndexOrNull(mineOnly(planet.getOthersByDistance()), 1));
			}
		}
	}

	protected void strategyClone() {
		if (isEverybodyReady()) {
			for (Planet planet : this.getPlanets()) {
				sendFleetUpTo(planet, (int) planet.getForces() / 3, firstOrNull(hostileOnly(planet.getOthersByDistance())));
			}
		}
	}

	private boolean isEverybodyReady() {
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() < 20) {
				return false;
			}
		}

		return true;
	}

	protected void strategyFair(int factor) {
		List<Planet> targets = this.getAllPlanetsButMine();
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() < targets.size() * factor) {
				return;
			}

			for (Planet target : targets) {
				if (target != planet) {
					sendFleet(planet, factor, target);
				}
			}
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
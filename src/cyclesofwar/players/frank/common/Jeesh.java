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
			fleet.setFormation(Formation.ARROW);
		}
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
			attackFromAll(firstOrNull(hostileOnly((this.getPlanets().get(0).getOthersByDistance()))), 0.5);
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
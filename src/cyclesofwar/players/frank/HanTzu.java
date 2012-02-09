package cyclesofwar.players.frank;

import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.players.frank.common.Jeesh;
import cyclesofwar.players.frank.common.Prediction;

public class HanTzu extends Jeesh {

	@Override
	protected void thinkYourself() {
		List<Player> others = getOtherAlivePlayers();

		if (others.size() > 1) {
			fightMultipleEnemies(others);
		} else {
			fightSingleEnemy();
		}
	}

	private void fightSingleEnemy() {
		// Crazy Toms Strategy
		for (Planet planet : getPlanets()) {
			if (planet.getForces() < 10) {
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
				if (planet.getForces() < 10) {
					break;
				}
			}
		}
	}

	private void fightMultipleEnemies(List<Player> others) {
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
}

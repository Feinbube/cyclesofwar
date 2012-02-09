package cyclesofwar.players.frank;

import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.common.Jeesh;
import cyclesofwar.players.frank.common.Prediction;

public class CrazyTom extends Jeesh {

	@Override
	public void thinkYourself() {
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
}
package cyclesofwar.players.frank;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class CrazyTom extends Player {

	@Override
	public void think() {
		for (Planet planet : getPlanets()) {
			if (planet.getForces() < 10) {
				continue;
			}
			List<Prediction> predictions = getAllPredictions(lastFleetArrivalTime());
			sortByDistanceTo(predictions, planet);

			Prediction localPrediction = predictions.get(0);
			predictions.remove(localPrediction);

			for (Prediction prediction : predictions) {
				if (prediction.player.equals(this) && prediction.forces < localPrediction.forces) {
					sendFleetUpTo(planet, (int) (localPrediction.forces - prediction.forces), prediction.planet);
				} else if (!prediction.player.equals(this)) {
					sendFleetUpTo(planet, (int) (prediction.forces + 1), prediction.planet);
				}
				if (planet.getForces() < 1) {
					break;
				}
			}
		}
	}

	private List<Prediction> getAllPredictions(double lastFleetArrivalTime) {
		List<Prediction> result = new ArrayList<Prediction>();
		for (Planet planet : this.getAllPlanets()) {
			result.add(getPrediction(planet, lastFleetArrivalTime));
		}

		return result;
	}

	double lastFleetArrivalTime() {
		List<Fleet> fleets = getAllFleets();
		if (fleets.size() == 0) {
			return 0;
		}
		sortByArrivalTime(fleets);
		return fleets.get(fleets.size() - 1).timeToTarget();
	}

	private void sortByDistanceTo(List<Prediction> predictions, final Planet planet) {
		Collections.sort(predictions, new Comparator<Prediction>() {
			@Override
			public int compare(Prediction one, Prediction other) {
				return Double.compare(planet.distanceTo(one.planet), planet.distanceTo(other.planet));
			}
		});
	}

	private Prediction getPrediction(Planet planet, double time) {
		Prediction result = new Prediction(planet);

		List<Fleet> fleets = this.getFleetsWithTarget(planet);
		sortByArrivalTime(fleets);

		for (Fleet fleet : fleets) {
			result.update(fleet);
		}

		return result;
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
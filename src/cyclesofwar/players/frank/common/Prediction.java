package cyclesofwar.players.frank.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Prediction {
	private Planet planet;
	private Player player;
	private double forces;
	private double time;
	
	public Planet getPlanet() {
		return planet;
	}

	public Player getPlayer() {
		return player;
	}

	public double getForces() {
		return forces;
	}

	public double getTime() {
		return time;
	}

	public Prediction(Planet planet) {
		this.player = planet.getPlayer();
		this.forces = planet.getForces();
		this.planet = planet;
		this.time = 0.0;
	}

	public void update(double timeToTarget){
		double late = timeToTarget - time;
		this.forces += producedForces(late);
	}
	
	public void update(Fleet fleet) {
		update(fleet.getPlayer(), fleet.getForce(), fleet.getTimeToTarget());
	}

	public void update(Player player, int force, double timeToTarget) {
		update(timeToTarget);

		if (this.player == player) {
			this.forces += force;
		} else {
			this.forces -= force;
			if (this.forces < 0) {
				this.player = player;
				this.forces *= -1;
			}
		}

		time = timeToTarget;
	}

	double producedForces(double duration) {
		if (player == Player.NonePlayer) {
			return 0;
		} else {
			return duration * this.planet.getProductionRatePerSecond();
		}
	}
	
	public static void sortByDistanceTo(List<Prediction> predictions, final Planet planet) {
		Collections.sort(predictions, new Comparator<Prediction>() {
			@Override
			public int compare(Prediction one, Prediction other) {
				return Double.compare(planet.getDistanceToSquared(one.planet), planet.getDistanceToSquared(other.planet));
			}
		});
	}
	
	public static void sortByForceCount(List<Prediction> predictions) {
		Collections.sort(predictions, new Comparator<Prediction>() {
			@Override
			public int compare(Prediction one, Prediction other) {
				return Double.compare(one.forces, other.forces);
			}
		});
	}
	
	public static Prediction getPrediction(Jeesh player, Planet planet, double time) {
		Prediction result = new Prediction(planet);

		List<Fleet> fleets = player.getFleetsWithTarget(planet);
		Fleet.sortByArrivalTime(fleets);
		for (Fleet fleet : fleets) {
			if (fleet.getTimeToTarget() > time) {
				break;
			}
			result.update(fleet);
		}
		
		result.update(time);

		return result;
	}
	
	public static List<Prediction> getAllPredictions(Jeesh player) {
		return getAllPredictions(player, player.getlastFleetArrivalTime());
	}
	
	public static List<Prediction> getAllPredictions(Jeesh player, double time) {
		List<Prediction> result = new ArrayList<Prediction>();
		for (Planet planet : player.getAllPlanets()) {
			result.add(Prediction.getPrediction(player, planet, time));
		}

		return result;
	}
	
	public static List<Prediction> enemyPlanetsOnly(List<Prediction> predictions, Player player) {
		List<Prediction> result = new ArrayList<Prediction>();
		for (Prediction prediction : predictions) {
			if (prediction.player != player) {
				result.add(prediction);
			}
		}
		return result;
	}
}
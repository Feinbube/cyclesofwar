package cyclesofwar.players.frank;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

class Prediction {
	Planet planet;
	Player player;
	double forces;
	double time;

	public Prediction(Planet planet) {
		this.player = planet.getPlayer();
		this.forces = planet.getForces();
		this.planet = planet;
		this.time = 0.0;
	}

	void update(Fleet fleet) {
		update(fleet.getPlayer(), fleet.getForce(), fleet.timeToTarget());
	}

	void update(Player player, int force, double timeToTarget) {
		double late = timeToTarget - time;
		this.forces += producedForces(late);

		if (this.player.equals(player)) {
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
		if (player.equals(Player.NonePlayer)) {
			return 0;
		} else {
			return duration * this.planet.getProductionRatePerSecond();
		}
	}
}
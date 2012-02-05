package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class SharpShooter extends Player {

	@Override
	public void think() {
		if (this.getFleets().size() > 0) {
			return;
		}

		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() / 2 >= 1) {
				sendFleet(planet, (int) (planet.getForces() / 2), getRandomPlanet(planet));
			}
		}
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.white;
	}

	@Override
	public String getCreatorsName() {
		return "Training";
	}
}
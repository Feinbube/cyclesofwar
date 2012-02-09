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
				sendFleet(planet, (int) (planet.getForces() / 2), planet.getOthers().get(getRandomInt(planet.getOthers().size())));
			}
		}
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.yellow;
	}

	@Override
	public String getCreatorsName() {
		return "Training";
	}
}
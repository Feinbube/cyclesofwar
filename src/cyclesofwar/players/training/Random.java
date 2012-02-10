package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Random extends Player {

	@Override
	public void think() {
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() > 20)
				sendFleet(planet, (int) (planet.getForces() / 2), getRandomTarget(planet));
		}
	}

	private Planet getRandomTarget(Planet planet) {
		return planet.getOthers().get(getRandomInt(planet.getOthers().size()));
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
		return "Noob";
	}
}
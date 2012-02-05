package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class RandomPlayer extends Player {

	@Override
	public void think() {
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() > 20)
				sendFleet(planet, (int) (planet.getForces() / 2), getRandomPlanet(planet));
		}
	}
	
	@Override
	public Color getPlayerBackColor() {
		return Color.cyan.darker().darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.cyan;
	}

	@Override
	public String getCreatorsName() {
		return "Training";
	}
}
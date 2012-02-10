package cyclesofwar.players.training;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Fair extends Player {

	@Override
	public void think() {
		List<Planet> targets = this.getAllPlanetsButMine();
		for (Planet planet : this.getPlanets()) {
			if (planet.getForces() < targets.size()) {
				continue;
			}

			for (Planet target : targets) {
				sendFleet(planet, 1, target);
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
		return "Noob";
	}

}

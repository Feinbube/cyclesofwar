package cyclesofwar.players.robert;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;


public class FastExpand extends Player {

	@Override
	protected void think() {
		for (Planet  planet : getPlanets()) {
			List<Planet> targets = getAllPlanetButMine();
			sortByForceCount(targets);
			Collections.reverse(targets);
			
			while (planet.getForces() > 1 && !targets.isEmpty()) {
				Planet target = targets.remove(0);
				sendFleetUpTo(planet, (int)target.getForces() + 1, target);
			}
		}
	}

	@Override
	protected Color getPlayerBackColor() {
		return Color.red;
	}

	@Override
	protected Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	protected String getCreatorsName() {
		return "Robert";
	}

}
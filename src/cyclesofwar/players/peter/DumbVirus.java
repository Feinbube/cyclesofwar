package cyclesofwar.players.peter;
import java.awt.Color;
import java.util.Collections;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;


public class DumbVirus extends Player {

	@Override
	protected void think() {
		for (Planet  planet : getPlanets()) {
			List<Planet> targets = getAllPlanetsButMine();
			if (targets.size() > 0) {
				sortByDistanceTo(targets, planet);
				sendFleetUpTo(planet, 9999, targets.get(0));			
			}
		}
	}
	
	@Override
	public Color getPlayerBackColor() {
		return Color.green;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.black;
	}

	@Override
	public String getCreatorsName() {
		return "Peter";
	}

}

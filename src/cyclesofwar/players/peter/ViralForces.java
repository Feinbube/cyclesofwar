package cyclesofwar.players.peter;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;


public class ViralForces extends Player {

	@Override
	protected void think() {
		int maxNeigbours = 5;
		int looserFactor = 2;
		for (Planet  planet : getPlanets()) {
			List<Planet> targets = getAllPlanetsButMine();
			if (targets.size() >= maxNeigbours) {
				planet.sortOthersByDistance(targets);
				for (int i=0; i<maxNeigbours; i++) {
					Planet target = targets.get(i);		
					if (target.getForces()*looserFactor < planet.getForces()) {
						sendFleetUpTo(planet, 99, target);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public Color getPlayerBackColor() {
		return Color.green.darker().darker();
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

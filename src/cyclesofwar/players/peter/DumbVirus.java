package cyclesofwar.players.peter;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

public class DumbVirus extends Player {

	@Override
	protected void think() {
		// TODO: make this dynamic, according to the number of enemy fleets expected
		double maxSendFleetFactor = 0.5;		// never send more than this part of the home fleet
		double targetOverrideFactor = 0.1;		// to which factor do we want to send more fleets than the target has
		int maxNeighbours = 3;					// restrict on the close environment of these many planets
		// go through my own planets
		for (Planet  home : getPlanets()) {
			long maxToBeMoved = java.lang.Math.round(maxSendFleetFactor*home.getForces());
			List<Planet> neighbours = home.getOthersByDistance();
			for (int i=0; i<maxNeighbours; i++) {
				Planet target = neighbours.get(i);
				long toBeSend = java.lang.Math.round(targetOverrideFactor*target.getForces() + target.getForces());
				Fleet f;
				if (target.isFree() && target.getForces() < maxToBeMoved) {
					f = sendFleetUpTo(home, maxToBeMoved, target);
					f.setFormation(Fleet.Formation.O);
					break;
				}
				else if (toBeSend < maxToBeMoved) {
					f = sendFleetUpTo(home, toBeSend, target);
					// f.setFormation(Fleet.Formation.ARROW);    Leads to NULL Pointer Exception
					break;
				}
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
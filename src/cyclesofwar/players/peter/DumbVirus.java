package cyclesofwar.players.peter;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

public class DumbVirus extends Player {

	private void targetStats(Planet home, Planet target) {
		System.out.println("Target distance: "+target.distanceTo(home));
		System.out.println("Target forces: "+target.getForces());
		System.out.println("My forces: "+home.getForces());
	}

	@Override
	protected void think() {
		// TODO: make this dynamic, according to the number of enemy fleets expected
		double maxSendFleetFactor = 0.7;		// never send more than this part of the home fleet
		double targetOverrideFactor = 0.6;		// to which factor do we want to send more fleets than the target has
		int maxNeighbours = 3;					// restrict on the close environment of these many planets
		// go through my own planets
		for (Planet  home : getPlanets()) {
			int maxToBeMoved = (int)java.lang.Math.round(maxSendFleetFactor*home.getForces());
			List<Planet> neighbours = home.getOthersByDistance();
			// Traveling more than half of the max distance is far away
			double farAway = neighbours.get(neighbours.size() - 1).getDistanceTo(home) / 2.0;
			boolean checkNext = true;
			boolean didAction = false;
			int i=0;
			while (checkNext) {
				Planet target = neighbours.get(i);
				int toBeSend;
				Fleet f;
				if (target.isFree()) {
					toBeSend = maxToBeMoved;
				} else {
					toBeSend = (int)java.lang.Math.round(targetOverrideFactor*target.getForces() + target.getForces());
				}
				if (target.getDistanceTo(home) < farAway && toBeSend > 0 && toBeSend <= maxToBeMoved && toBeSend < home.getForces()) {
					//targetStats(home, target);
					f = sendFleet(home, toBeSend, target);
					didAction=true;
				}
				i=i+1;
				// Stop sending people to the neigbour planets from this home if
				// 1.) We have no more neigbours to check, OR
				// 2.) If we checked more than 'maxNeigbours' and already sent some people, OR
				// 3.) If we sent some people and the current neigbour is already 'far away', so the next one will be even more far away
				if (i>=neighbours.size() || (i>=maxNeighbours && didAction) || (target.distanceTo(home) > farAway && didAction))
					checkNext = false;
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
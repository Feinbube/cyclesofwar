package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * This bot waits for you to attack it. If you try to attack it, though, it's going to run from you. 
 * Chase it down ;)
 */
public class B01_ChaseMe extends Player {

	@Override
	public void think() {
		// for each of my planets
		for (Planet planet : this.getPlanets()) {
			
			// if I am going to be attacked next round
			if (0 < forcesArrivingNextRound(planet)) {
				
				// look for another planet close by
				Planet target = planet.getOthersByDistance().get(this.getRandomInt(5));
				
				// we flee with half of the forces
				int forces = (int)(planet.getForces() / 2);
				
				// sendFleet is more restrictive than sendFleetUpTo, so we have to ensure we have enough troops
				if(forces >= 1){
					// send our forces to the target
					this.sendFleet(planet, forces, target);
				}
			}
		}
	}

	private int forcesArrivingNextRound(Planet planet) {
		int result = 0;
		
		// look at all hostile fleets heading towards this planet
		for (Fleet fleet : this.hostileOnly(this.getFleetsWithTargetSortedByArrivalTime(planet))) {
			
			// if they arrive within the next round
			if (fleet.getRoundsToTarget() <= 1) {
				
				// add up their force
				result += fleet.getForce();
			}
		}
		
		return result;
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
		return null;
	}
}

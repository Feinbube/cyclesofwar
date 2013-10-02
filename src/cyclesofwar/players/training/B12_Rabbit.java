package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * For each planet, this bot attacks with it's full force if it has at least 31 forces there.
 * If the planet is going to be attacked in the next round, it 'flees' with all the forces it's got.
 * The weird thing is, that it always flees towards the closest hostile planet. 
 */
public class B12_Rabbit extends Player {

	@Override
	public void think() {
		
		// I own all planets. Let's give everybody else some time to catch up ^^
		if (getAllPlanetsButMine().isEmpty()) {
			return;
		}

		// for each planet
		for (Planet planet : this.getPlanets()) {
			
			// get closest hostile planet
			Planet target = this.hostileOnly(planet.getOthersByDistance()).get(0);

			// jump around
			if (planet.getForces() >= 31) {
				sendFleet(planet, 31, target);
			}

			// if I am going to loose this planet next round
			if (planet.getForces() + planet.getProductionRatePerRound() < forcesArrivingNextRound(planet)) {
				// flee (towards the enemy... you could name that 'attacking' as well ;) )
				sendFleetUpTo(planet, (int) planet.getForces(), target);
			}
		}
	}
	
	private int forcesArrivingNextRound(Planet planet) {
		int result = 0;
		
		// look at all hostile fleets heading towards this planet
		for (Fleet fleet : this.hostileOnly(this.getFleetsWithTarget(planet))) {
			
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
		return "B";
	}
}

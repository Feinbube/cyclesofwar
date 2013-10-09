package cyclesofwar.players.training;

import java.awt.Color;
import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * For each planet this bot waits until it has a population of 64 . 
 * Then it sends fleets to the neighboring planets.
 * Fleet sizes are 32 for the closest planet. 16 for the second closest. 8 for the third one. And so on. 
 */
public class B06_PowersOf2 extends Player {

	@Override
	public void think() {
		
		// for all my planets
		for(Planet planet : this.getPlanets())
		{
			// if I have enough forces 
			if(planet.getForces() < 64)
				continue;
			
			// current fleet size. this value is getting halved each round
			int fleet = 32;
			
			// for all the other planets ordered by the distance (closest planets first)
			for(Planet other : planet.getOthersByDistance())
			{
				// send a fleet there
				this.sendFleet(planet, fleet, other);
				
				// reduce the fleet size by 50%
				fleet = fleet / 2;
				
				// go on with the next of my planets, if the forces of this one are depleted
				if(fleet == 0)
					break;
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
		return null;
	}
}

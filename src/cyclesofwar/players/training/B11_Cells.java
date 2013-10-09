package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * This bot waits until all it's planets have enough forces. Then they all attack simultaneously.
 * Each of them attack it's the hostile planet closest to them.
 */
public class B11_Cells extends Player {

	@Override
	public void think() {
		
		// if everyone has enough troops to attack
		if(isEverybodyReady()) {
			
			// each of my planet
			for (Planet planet : this.getPlanets()) {
				
				// send a fleet to the closest hostile target.
				sendFleetUpTo(planet, (int)planet.getForces() / 2, 
						firstOrNull(hostileOnly(planet.getOthersByDistance())));
			}
		}
	}

	private boolean isEverybodyReady() {
		
		// for each of my planets
		for (Planet planet : this.getPlanets()) {
			
			// if there are not enough troops on this planet, we are not ready yet
			if(planet.getForces() < 20) {
				return false;
			}
		}
		
		// we only end up here, when everyone is ready :)
		return true;
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

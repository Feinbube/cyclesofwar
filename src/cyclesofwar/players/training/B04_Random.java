package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * For each of it's planets, this bot fires fleets of 10 to random other planets.
 */
public class B04_Random extends Player {

	@Override
	public void think() {
		
		// for all of my planets
		for (Planet planet : this.getPlanets()) {
			
			// if there are enough forces to attack
			if (planet.getForces() > 20) {
				
				// send half the forces to some randomly chosen planet
				sendFleet(planet, (int) (planet.getForces() / 2), this.randomOrNull(planet.getOthers()));
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
		return "B";
	}
}
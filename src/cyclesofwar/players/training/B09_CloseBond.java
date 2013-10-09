package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * For each planet, this bot waits until he has 30 forces. Then he keeps 10. 
 * Sends 10 to its nearest hostile neighbor. And distributes 5 to each of it's two closest allies.
 */
public class B09_CloseBond extends Player {

	@Override
	public void think() {

		// for each of my planets
		for(Planet planet : this.getPlanets()) {
			
			// if I have enough forces to act
			if(planet.getForces() > 30) {
				
				// send 10 attacking forces to my closest enemy
				sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planet.getOthersByDistance())));
				
				// send 5 defending forces to each of my closest allies
				sendFleetUpTo(planet, 5, atIndexOrNull(mineOnly(planet.getOthersByDistance()),0));
				sendFleetUpTo(planet, 5, atIndexOrNull(mineOnly(planet.getOthersByDistance()),1));
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

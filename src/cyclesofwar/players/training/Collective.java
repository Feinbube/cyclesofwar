package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Collective extends Player {

	@Override
	public void think() {
		if (this.getPlanets().isEmpty())
			return;
		
		for(Planet planet : this.getPlanets()) {
			if(planet.getForces() > 30) {
				sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planet.getOthersByDistance())));
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
		return "Noob";
	}

}

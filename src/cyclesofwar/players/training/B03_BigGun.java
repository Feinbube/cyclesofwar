package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Fleet.Formation;
import cyclesofwar.Planet;
import cyclesofwar.Player;

/**
 * The core idea of this bot is a home planet that acts like a huge canon that fires onto enemy planets.
 * All planets that are conquered this way send fleets to the home planet to increase it's firing frequency.
 */
public class B03_BigGun extends Player {

	@Override
	public void think() {
		
		// my homeplanet is the oldest one I got
                Planet homeplanet = Player.lastOrNull(Planet.sortedBy(Planet.OwnershipChangeTimeComparator, this.getPlanets()));
                if (homeplanet == null)
			return;
                
		// if I have enough forces on the home planet
		if(homeplanet.getForces() > 70) {
			
			// choose the closest hostile planet
			Planet target = firstOrNull(hostileOnly(homeplanet.getOthersByDistance()));
			if(target != null){
				// and attack it
				Fleet attackFleet = sendFleet(homeplanet, 50, target);
				
				// set a nice formation to ensure I looks awesome (only for the visuals)
				attackFleet.setFormation(Formation.V);
			}
		}
		
		// for each of my other planets
		for(Planet planet : mineOnly(homeplanet.getOthers())) {
			
			// if I have enough forces to support the home planet
			if(planet.getForces() > 20) {
				
				// send them there
				Fleet defendFleet = sendFleet(planet, 10, homeplanet);
				
				// set a nice formation to ensure I looks awesome (only for the visuals)
				defendFleet.setFormation(Formation.O);
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

package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Fleet.Formation;
import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * The core idea of this bot is a home planet that acts like a huge canon that fires onto enemy planets.
 * All planets that are conquered this way send fleets to the home planet to increase it's firing frequency.
 */
public class B03_BigGun extends Player {

	// the home planet: target of the support of all colonies, root of all attacking forces
	Planet homeplanet = null;
	
	@Override
	public void think() {
		
		// if I have no planet, I have nothing to do (let's hope, I have some next round)
		if (this.getPlanets().isEmpty())
			return;
		
		// if I lost my home planet, I need to chose a new one
		if(homeplanet == null || homeplanet.getPlayer() != this){
			homeplanet = this.getPlanets().get(0);
		}
		
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
		return "B";
	}
}

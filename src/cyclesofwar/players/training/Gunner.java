package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Gunner extends Player {

	Planet homeplanet = null;
	
	@Override
	public void think() {
		if (this.getPlanets().isEmpty())
			return;
		
		if(homeplanet == null || homeplanet.getPlayer() != this){
			homeplanet = this.getPlanets().get(this.getPlanets().size()-1);
		}
		
		if(homeplanet.getForces() > 50) {
			sendFleetUpTo(homeplanet, 50, getTarget(homeplanet));
		}
		
		for(Planet planet : mineOnly(homeplanet.getOthers())) {
			if(planet.getForces() > 20) {
				sendFleetUpTo(planet, 10, homeplanet);
			}
		}
	}

	private Planet getTarget(Planet planet) {
		return firstOrNull(hostileOnly(planet.getOthersByDistance()));
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

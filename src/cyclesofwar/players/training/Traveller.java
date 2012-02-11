package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Traveller extends Player {
	
	@Override
	public void think() {
		if (this.getPlanets().isEmpty())
			return;
		
		Planet latestPlanet = this.getPlanets().get(this.getPlanets().size()-1);
		Planet target = getTarget(latestPlanet);
		
		for(Planet planet : this.getPlanets()) {
			if(planet.getForces() > 20) {
				sendFleetUpTo(planet, 10, target);
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

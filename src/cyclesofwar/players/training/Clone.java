package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Clone extends Player {

	@Override
	public void think() {
		if(isEverybodyReady()) {
			for (Planet planet : this.getPlanets()) {
				sendFleetUpTo(planet, (int)planet.getForces() / 2, getTarget(planet));
			}
		}
	}

	private Planet getTarget(Planet planet) {
		return firstOrNull(hostileOnly(planet.getOthersByDistance()));
	}

	private boolean isEverybodyReady() {
		for (Planet planet : this.getPlanets()) {
			if(planet.getForces() < 20) {
				return false;
			}
		}
		
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
		return "Noob";
	}

}

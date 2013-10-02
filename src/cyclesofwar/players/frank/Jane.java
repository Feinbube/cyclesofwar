package cyclesofwar.players.frank;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Jane extends Player {

	@Override
	protected void think() {
		for (Planet planet : this.getPlanets()) {
			for (Planet other : planet.getOthersByDistance()) {
				
				if(planet.getForces() < 1)
					break;
				
				double defendBalance = this.getPrediction(planet, planet.getTimeTo(other) + getRoundDuration()).getBalance();				
				if (defendBalance < 0)
					break;

				double attackBalance = this.getPrediction(other, planet.getTimeTo(other) + getRoundDuration()).getBalance();
				if (attackBalance < 0)
				{
					int force = (int) Math.min(-attackBalance, planet.getForces());				
					this.sendFleet(planet,force, other);
				}
			}
		}
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.blue.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	public String getCreatorsName() {
		return "Frank";
	}
}

package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * For each planet, this bot waits until it can effort to send one fleet to each other planet.
 * Then it sends them :)
 */
public class B02_Wave extends Player {

	@Override
	public void think() {
		
		// for each of my planets
		for (Planet planet : this.getPlanets()) {
			
			// if I have enough inhabitants to send one to each other planet
			if (planet.getForces() > this.getAllPlanets().size()) {
		
				// send one ship to each other planet
				for (Planet target : planet.getOthers()) {
					sendFleet(planet, 1, target);
				}
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

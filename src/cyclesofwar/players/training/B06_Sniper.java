package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * Every time there this bot has no active fleet, each of it's planets shoot half their forces
 * onto a random planet. (All of the planets of this bot shoot at the same time.)
 */
public class B06_Sniper extends Player {

	@Override
	public void think() {
		
		// if I still have active fleets, I pass
		if (this.getFleets().size() > 0) {
			return;
		}
		
		// no active fleets -> let's shoot all we got! :D
		
		// for all my planets
		for (Planet planet : this.getPlanets()) {
			
			// I want to send half my forces
			int force = (int)(planet.getForces() / 2);
			
			// towards a randomly chosen hostile planet
			Planet target = this.randomOrNull(this.getAllPlanetsButMine());
			
			// and go
			if (force >= 1 && target != null) {
				sendFleet(planet, force, target);
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
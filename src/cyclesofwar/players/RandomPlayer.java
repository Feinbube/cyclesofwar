package cyclesofwar.players;
import java.awt.Color;
import java.util.Random;

import cyclesofwar.Planet;
import cyclesofwar.Player;


public class RandomPlayer extends Player {
	
	Random random = new Random();

	@Override
	public void think() {	
		for(Planet planet : this.getPlanets()) {
			if(planet.getForces() > 20)
				sendFleet(planet, (int)(planet.getForces()/2), getRandomTarget());
		}
	}


	Planet getRandomTarget() {
		return getAllPlanets().get(random.nextInt(getAllPlanets().size()));
	}


	@Override
	public Color getPlayerBackColor() {
		return Color.cyan.darker().darker();
	}


	@Override
	public Color getPlayerForeColor() {
		return Color.cyan;
	}


	@Override
	public String getCreatorsName() {
		return "Frank";
	}
}
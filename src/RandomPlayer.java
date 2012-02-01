import java.awt.Color;


class RandomPlayer extends Player {
	
	public RandomPlayer(Color color, Planet starterPlanet) {
		super(color, starterPlanet);
	}


	@Override
	public void think() {	
		for(Planet planet : getPlanets()) {
			if(planet.forces > 20)
				Universe.INSTANCE.SendFleet(this, planet, (int)(planet.forces/2), getRandomTarget());
		}
	}


	Planet getRandomTarget() {
		return Universe.INSTANCE.AllPlanets().get(Universe.INSTANCE.random.nextInt(Universe.INSTANCE.AllPlanets().size()));
	}
}
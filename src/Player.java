import java.awt.Color;
import java.util.ArrayList;


class Player {
	
	public static Player NonePlayer = new Player (Color.gray);
	
	Color c;
	
	public Player(Color c){
		this.c = c;
	}
	
	public Player(Color c, Planet planet){
		this.c = c;
		planet.player = this;
	}
	
	public void think()
	{
		if(this.equals(NonePlayer))
			return;
		
		ArrayList<Planet> myPlanets = Universe.INSTANCE.PlanetsOfPlayer(this);
		for(Planet planet : myPlanets) {
			if(planet.forces > 20)
				Universe.INSTANCE.SendFleet(this, planet, (int)(planet.forces/2), findTarget());
		}
	}

	private Planet findTarget() {
		return Universe.INSTANCE.AllPlanets().get(Universe.INSTANCE.random.nextInt(Universe.INSTANCE.AllPlanets().size()));
	}
}
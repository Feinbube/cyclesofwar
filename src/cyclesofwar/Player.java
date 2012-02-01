package cyclesofwar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Player {
	
	public static Player IdlePlayer = new IdlePlayer(Color.gray);

	
	protected Color color;

	
	public Player(Color color, Planet planet){
		this.color = color;
		if (planet != null)
			planet.player = this;
	}

	public Player() {
		super();
	}

	public abstract void think();
	
	protected List<Planet> getPlanets() {
		return Universe.INSTANCE.PlanetsOfPlayer(this);
	}

}
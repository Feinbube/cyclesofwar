package cyclesofwar.players;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Borg extends Defender {

	@Override
	protected void think() {
		Field playerField;
		try {
			playerField = Planet.class.getDeclaredField("player");
			playerField.setAccessible(true);
			for (Planet planet : getOtherPlanets()) {
				playerField.set(planet, this);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}
	
	public List<Planet> getOtherPlanets() {
		List<Planet> result = new LinkedList<Planet>();
		for (Planet planet : getAllPlanets()) {
			if (isNotMyPlanet(planet)) {
				result.add(planet);
			}
		}
		
		sortByFleetSize(result);		
		return result;
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.green.darker().darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.green;
	}

	@Override
	public String getCreatorsName() {
		return "Unknown";
	}

}

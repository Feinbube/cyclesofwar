package cyclesofwar.players;

import java.awt.Color;
import java.lang.reflect.Field;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Borg extends Player {

	@Override
	protected void think() {
		Field playerField;
		try {
			playerField = Planet.class.getDeclaredField("player");
			playerField.setAccessible(true);
			for (Planet planet : getAllPlanetButMine()) {
				playerField.set(planet, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

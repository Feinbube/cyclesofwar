package cyclesofwar.players.robert;

import java.awt.Color;
import java.lang.reflect.Field;

import cyclesofwar.Planet;

public class Borg extends Player {

	@Override
	protected void think() {
		Field playerField;
		try {
			playerField = Planet.class.getDeclaredField("player");
			playerField.setAccessible(true);
			for (Planet planet : getAllPlanetsButMine()) {
				playerField.set(planet, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

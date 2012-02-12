package cyclesofwar.players.training;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Borderline extends Player {

	@Override
	public void think() {
		if (this.getPlanets().isEmpty() || this.getAllPlanetsButMine().isEmpty()) {
			return;
		}

		for (Planet planet : getPlanets()) {
			if (planet.getForces() > 12) {
				Planet frontPlanet = getFrontPlanet(planet);
				if (planet != frontPlanet) {
					sendFleetUpTo(planet, planet.getForces() / 2, frontPlanet);
				} else {
					sendFleetUpTo(frontPlanet, frontPlanet.getForces() / 2, hostileOnly(planet.getOthersByDistance()).get(0));
				}
			}
		}
	}

	private Planet getFrontPlanet(Planet planet) {
		List<Planet> planets = firstElements(mineOnly(planet.getOthersByDistance()), 3);
		planets.add(planet);
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet one, Planet other) {
				return Double.compare(distanceToNextEnemy(one), distanceToNextEnemy(other));
			}

			private double distanceToNextEnemy(Planet planet) {
				return planet.getDistanceTo(hostileOnly(planet.getOthersByDistance()).get(0));
			}
		});

		return planets.get(0);
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
		return "Noob";
	}

}

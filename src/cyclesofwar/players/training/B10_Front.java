package cyclesofwar.players.training;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/**
 */
public class B10_Front extends Player {

	@Override
	public void think() {
		// if there is no hostile planet, I pass
		if(this.getAllPlanetsButMine().isEmpty())
			return;
		
		// for each of my planets
		for (Planet planet : this.getPlanets()) {
			
			// if I have enough forces to act
			if (planet.getForces() > 12) {
				
				// select the planet that is closest to the enemy
				// only considering this one and it's 3 closest neighbors
				Planet frontPlanet = getFrontPlanet(planet, 3);
				
				// if I am not the front planet
				if (planet != frontPlanet) {
					
					// send half of my troops to support it
					sendFleetUpTo(planet, planet.getForces() / 2, frontPlanet);
				} else {
					
					// if I am the front planet, send half of my troops towards the closest enemy
					sendFleetUpTo(frontPlanet, frontPlanet.getForces() / 2, 
							hostileOnly(planet.getOthersByDistance()).get(0));
				}
			}
		}
	}

	private Planet getFrontPlanet(Planet planet, int maxNumberNeighborsConsidered) {
		
		// consider only the current planet and some of it's neighbors
		List<Planet> planets = firstElements(mineOnly(planet.getOthersByDistance()), maxNumberNeighborsConsidered);
		planets.add(planet);
		
		// sort them by distance to the next enemy
		Collections.sort(planets, new Comparator<Planet>() {
			@Override
			public int compare(Planet one, Planet other) {
				return Double.compare(distanceToNextEnemy(one), distanceToNextEnemy(other));
			}

			private double distanceToNextEnemy(Planet planet) {
				return planet.getDistanceTo(hostileOnly(planet.getOthersByDistance()).get(0));
			}
		});

		// return the one that is closest to an enemy
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
		return null;
	}
}

package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class Universe {
	static Universe INSTANCE = new Universe();

	List<Planet> planets = new ArrayList<Planet>();
	List<Fleet> fleets = new ArrayList<Fleet>();

	List<Player> players = new ArrayList<Player>();

	Random random = new Random();

	double size;
	
	double nothingHappendCounter = 0;
	boolean gameOver = true;
	Player winner = null;
	
	Player currentPlayer = null;

	SortedMap<Double, Fleet> fleetsAtDestination = new TreeMap<Double, Fleet>();
	List<Fleet> newFleets = new ArrayList<Fleet>();

	private Universe() {
		reInitialize();
	}

	void reInitialize() {
		gameOver = true;
		
		List<Player> combatants = Arena.Combatants();

		this.size = Math.sqrt(combatants.size());
		
		long seed = new Random().nextLong();
		random.setSeed(seed);

		planets.clear();
		for (int i = 0; i < Arena.PlanetCountPerPlayer * combatants.size(); i++) {
			planets.add(suiteablePlanet(-1));
		}

		fleets.clear();
		fleetsAtDestination.clear();

		players.clear();
		for (Player player : combatants) {
			players.add(player);
			createStarterPlanet(player);
		}
		
		gameOver = false;
	}

	private Planet createStarterPlanet(Player player) {
		Planet planet = suiteablePlanet(5);
		planet.player = player;
		planets.add(planet);
		return planet;
	}

	private Planet suiteablePlanet(double productionRate) {
		while (true) {
			Planet planet = new Planet(random, size, productionRate);
			if (planetFits(planet))
				return planet;
		}
	}

	private boolean planetFits(Planet planet) {
		for (Planet other : planets) {
			if (toClose(planet, other))
				return false;
		}

		return true;
	}

	private boolean toClose(Planet planet, Planet other) {
		return planet.distanceTo(other) < (planet.productionRatePerSecond + other.productionRatePerSecond) * 2.1
				* GamePanel.planetSizingFactor;
	}

	void update(double elapsedSeconds) {
		if(gameOver) {
			return;
		}
		
		nothingHappendCounter += elapsedSeconds;
		
		for (Planet planet : planets) {
			planet.update(elapsedSeconds);
		}

		for (Fleet fleet : fleets) {
			fleet.update(elapsedSeconds);
		}

		for (Fleet fleet : fleetsAtDestination.values()) {
			fleet.land();
			fleets.remove(fleet);
		}
		fleetsAtDestination.clear();
		
		for (Planet planet : planets) {
			planet.newForces = planet.forces;
		}

		if (justOnePlayerLeft()) {
			gameOver = true;
			winner = bestPlayer();
			return;
		}

		for (Player player : players) {
			currentPlayer = player;
			currentPlayer.think();
		}
		
		if (nothingHappendCounter > 30) {
			gameOver = true;
			winner = NonePlayer.NonePlayer;
			return;
		}

		for (Fleet newFleet : newFleets) {
			fleets.add(newFleet);
		}
		newFleets.clear();
		
		for (Planet planet : planets) {
			planet.commit();
		}
	}

	boolean justOnePlayerLeft() {
		int playersAlive = 0;
		for (Player player : players) {
			if (player.getFullForce() > 0) {
				playersAlive++;
			}
		}

		return playersAlive <= 1;
	}
	
	Player bestPlayer() {
		Collections.sort(players, new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return (int)(player2.getFullForce() - player1.getFullForce());
			}
		});
		
		return players.get(0);
	}

	List<Player> OtherPlayers(Player player) {
		ArrayList<Player> result = new ArrayList<Player>();
		for (Player other : players) {
			if (!other.equals(player)) {
				result.add(other);
			}
		}

		return result;
	}

	List<Planet> AllPlanets() {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			result.add(planet);
		}

		return result;
	}

	List<Planet> PlanetsOfPlayer(Player player) {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			if (planet.player.equals(player)) {
				result.add(planet);
			}
		}

		return result;
	}

	List<Fleet> AllFleets(Player player) {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			result.add(fleet);
		}

		for (Fleet fleet : newFleets) {
			if (fleet.player.equals(player)) {
				result.add(fleet);
			}
		}

		return result;
	}

	List<Fleet> FleetsOfPlayer(Player player, Player target) {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			if (fleet.player.equals(target)) {
				result.add(fleet);
			}
		}
		if (player.equals(target)) {
			for (Fleet fleet : newFleets) {
				if (fleet.player.equals(player)) {
					result.add(fleet);
				}
			}
		}

		return result;
	}

	void SendFleet(Player player, Planet planet, int force, Planet target) {
		if (force == 0) {
			return;
		}

		if (force < 0 || force > planet.newForces) {
			System.out.println("Player " + player.getCreatorsName() + " wants to cheat with " + player.getClass().getSimpleName()
					+ " by sending a fleet of size " + force + " from a planet with " + planet.newForces + " troops!");
			return;
		}

		if (!planet.player.equals(player)) {
			System.out.println("Player " + player.getCreatorsName() + " wants to cheat with " + player.getClass().getSimpleName()
					+ " by sending a fleet from a planet owned by " + planet.player.getCreatorsName() + "!");
			return;
		}

		planet.newForces -= force;
		newFleets.add(new Fleet(player, force, planet, target));
		nothingHappendCounter = 0.0;
	}

	void fleetArrived(Fleet fleet, double distance) {
		fleetsAtDestination.put(distance, fleet);
	}

	public double getRandomDouble() {
		return random.nextDouble();
	}

	public int getRandomInt(int max) {
		return random.nextInt(max);
	}
}

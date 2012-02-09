package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class Universe {

	private static final double speedOfLight = 0.05;
	public static final int planetCountPerPlayer = 10;

	private List<Planet> planets = new ArrayList<Planet>();
	private List<Fleet> fleets = new ArrayList<Fleet>();
	private List<Player> players = new ArrayList<Player>();

	private Random random = new Random();

	private double now;
	private double size;

	private long seed;

	private long currentRound = 0;
	private int nothingHappenedCounter = 0;

	private boolean gameOver = true;
	private Player winner = null;

	private Player currentPlayer = Player.NonePlayer;

	private SortedMap<Double, Fleet> fleetsAtDestination = new TreeMap<Double, Fleet>();
	private List<Fleet> newFleets = new ArrayList<Fleet>();

	public Universe(long seed, List<Player> combatants) {
		gameOver = true;

		now = 0;
		currentRound = 0;
		nothingHappenedCounter = 0;

		size = Math.sqrt(combatants.size());

		this.seed = seed;
		random.setSeed(seed);

		int planetId = 0;
		planets.clear();
		for (int i = 0; i < planetCountPerPlayer * combatants.size(); i++) {
			planets.add(suiteablePlanet(planetId++, -1));
		}

		fleets.clear();
		fleetsAtDestination.clear();

		players.clear();
		for (Player player : combatants) {
			Player freshOne = player.freshOne();
			freshOne.setUniverse(this);
			createStarterPlanet(planetId++, freshOne);
			players.add(freshOne);
		}

		gameOver = false;

		for(Planet planet : planets) {
			planet.calculateDistances();
		}
	}

	private Planet createStarterPlanet(int planetId, Player player) {
		Planet planet = suiteablePlanet(planetId, 5);
		planet.setPlayer(player);
		planets.add(planet);
		return planet;
	}

	private Planet suiteablePlanet(int planetId, double productionRate) {
		while (true) {
			Planet planet = new Planet(planetId, this, random, getSize(), productionRate);
			if (planet.fits(planets))
				return planet;
		}
	}

	public void update(double elapsedSeconds) {
		if (gameOver) {
			return;
		}

		now += elapsedSeconds;
		nothingHappenedCounter++;
		currentRound++;

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
			planet.prepare();
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
		currentPlayer = NonePlayer.NonePlayer;

		if (nothingHappenedCounter > 1000 || currentRound > 100000) {
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

	private boolean justOnePlayerLeft() {
		int playersAlive = 0;
		for (Player player : players) {
			if (player.isAlive()) {
				playersAlive++;
			}
		}

		return playersAlive <= 1;
	}

	private Player bestPlayer() {
		Collections.sort(players, new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return (int) (player2.getFullForce() - player1.getFullForce());
			}
		});

		return players.get(0);
	}

	// for Player
	public List<Planet> getAllPlanets() {
		return new ArrayList<Planet>(planets);
	}

	public List<Fleet> getAllFleets() {
		return new ArrayList<Fleet>(fleets);
	}

	List<Fleet> getFleetsOf(Player player) {
		List<Fleet> result = filterFleetsOf(fleets, player);
		if (currentPlayer == player) {
			result.addAll(filterFleetsOf(newFleets, player));
		}

		return result;
	}

	private List<Fleet> filterFleetsOf(List<Fleet> fleets, Player player) {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			if (fleet.getPlayer() == player) {
				result.add(fleet);
			}
		}
		return result;
	}

	Fleet sendFleet(Planet origin, int force, Planet target) {
		if (force > origin.getForces()) {
			throw new IllegalArgumentException("fleet size exceeds planetary forces");
		}

		if (!currentPlayer.isMyPlanet(origin)) {
			throw new IllegalArgumentException("fleet must be send from owned planet");
		}

		Fleet newFleet = new Fleet(this, currentPlayer, force, origin, target);
		newFleets.add(newFleet);
		origin.setNewForces(origin.getNewForces() - newFleet.getForce());
		nothingHappenedCounter = 0;

		return newFleet;
	}

	public double getRandomDouble() {
		return random.nextDouble();
	}

	public int getRandomInt(int max) {
		return random.nextInt(max);
	}

	void fleetArrived(Fleet fleet, double distance) {
		fleetsAtDestination.put(distance, fleet);
	}

	public Player getWinner() {
		return winner;
	}

	public long getSeed() {
		return seed;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public double getSize() {
		return size;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public double getNow() {
		return now;
	}

	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}

	public static double getFlightSpeed() {
		return speedOfLight;
	}

	public static double getRoundDuration() {
		return speedOfLight;
	}

	public static double getRoundsPerSecond() {
		return 1.0 / speedOfLight;
	}

	public boolean inhabitedByPlayer(Player player) {
		return players.contains(player);
	}
}

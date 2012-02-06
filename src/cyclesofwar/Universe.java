package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class Universe {

	public static final double speedOfLight = 0.05;
	public static final int planetCountPerPlayer = 10;

	private List<Planet> planets = new ArrayList<Planet>();
	private List<Fleet> fleets = new ArrayList<Fleet>();

	private List<Player> players = new ArrayList<Player>();

	private Random random = new Random();

	private double now;
	private double size;

	private long seed;

	private long currentRound = 0;
	private double nothingHappenedCounter = 0;

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

		planets.clear();
		for (int i = 0; i < planetCountPerPlayer * combatants.size(); i++) {
			planets.add(suiteablePlanet(-1));
		}

		fleets.clear();
		fleetsAtDestination.clear();

		players.clear();
		for (Player player : combatants) {
			Player freshOne = player.freshOne();
			freshOne.setUniverse(this);
			createStarterPlanet(freshOne);
			players.add(freshOne);
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
			Planet planet = new Planet(this, random, getSize(), productionRate);
			if (planetFits(planet))
				return planet;
		}
	}

	private boolean planetFits(Planet planet) {
		for (Planet other : planets) {
			if (tooClose(planet, other))
				return false;
		}

		return true;
	}

	private boolean tooClose(Planet planet, Planet other) {
		return planet.distanceTo(other) < (planet.productionRatePerSecond + other.productionRatePerSecond) / 2 * speedOfLight;
	}

	public void update(double elapsedSeconds) {
		if (gameOver) {
			return;
		}

		now += elapsedSeconds;
		nothingHappenedCounter += elapsedSeconds;
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
		currentPlayer = NonePlayer.NonePlayer;

		if (nothingHappenedCounter > 60 || currentRound > 100000) {
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
			if (player.getFleets().size() > 0 || player.getPlanets().size() > 0) {
				playersAlive++;
			}
		}

		return playersAlive <= 1;
	}

	Player bestPlayer() {
		Collections.sort(players, new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return (int) (player2.getVisibleFullForce() - player1.getVisibleFullForce());
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

	public List<Planet> AllPlanets() {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			result.add(planet);
		}

		return result;
	}

	public List<Planet> PlanetsOfPlayer(Player player) {
		List<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			if (planet.player.equals(player)) {
				result.add(planet);
			}
		}

		return result;
	}

	public List<Fleet> AllFleets() {
		List<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			result.add(fleet);
		}

		return result;
	}
	
	List<Fleet> AllFleets(Player player) {
		List<Fleet> result = AllFleets();

		for (Fleet fleet : newFleets) {
			if (fleet.player.equals(player)) {
				result.add(fleet);
			}
		}

		return result;
	}

	List<Fleet> FleetsOfPlayer(Player asker, Player player) {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			if (fleet.player.equals(player)) {
				result.add(fleet);
			}
		}
		if (asker.equals(player)) {
			for (Fleet fleet : newFleets) {
				if (fleet.player.equals(asker)) {
					result.add(fleet);
				}
			}
		}

		return result;
	}

	Fleet SendFleet(Player player, Planet origin, int force, Planet target) {
		if (force > origin.getForces()) {
			throw new IllegalArgumentException("fleet size exceeds planetary forces");
		}

		if (!player.isMyPlanet(origin)) {
			throw new IllegalArgumentException("fleet must be send from owned planet");
		}

		Fleet newFleet = new Fleet(this, player, force, origin, target);
		newFleets.add(newFleet);
		origin.newForces -= newFleet.getForce();
		nothingHappenedCounter = 0.0;

		return newFleet;
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

	public boolean inhabitesPlayer(Player player) {
		return player.isInList(players);
	}

	public List<Player> getPlayers() {
		return players;
	}
}

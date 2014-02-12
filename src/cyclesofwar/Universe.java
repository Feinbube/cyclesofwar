package cyclesofwar;

import cyclesofwar.rules.*;
import java.util.*;

public class Universe {

    private static final double speedOfLight = 0.05;
    private final int planetCountPerPlayer;
    private final double universeSizeFactor;

    private final List<Planet> planets = new ArrayList<>();
    private final List<Fleet> fleets = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    private final Random random = new Random();

    private double now;
    private final double size;

    private final long seed;

    private long currentRound = 0;
    private int nothingHappenedCounter = 0;

    private boolean gameOver = true;
    private Player winner = null;

    private Player currentPlayer = Player.GoldenPlayer;

    private final SortedMap<Double, Fleet> fleetsAtDestination = new TreeMap<>();
    private final List<Fleet> newFleets = new ArrayList<>();

    private double lastFleetArrivalTime = 0.0;
    
    RuleEngine ruleEngine;
    
    public Universe(long seed, List<Player> combatants, int planetCountPerPlayer, double universeSizeFactor) {
        gameOver = true;

        now = 0;
        currentRound = 0;
        nothingHappenedCounter = 0;
        
        this.ruleEngine = new RuleEngine(this, new Rule[]{});
        
        this.universeSizeFactor = universeSizeFactor;
        this.planetCountPerPlayer = planetCountPerPlayer;

        size = Math.sqrt(combatants.size()) * universeSizeFactor;

        if(seed < 0) {
            seed = -seed;
        }
        
        this.seed = seed;
        random.setSeed(seed);

        int planetId = 0;
        planets.clear();
        for (int i = 0; i < planetCountPerPlayer * combatants.size(); i++) {
            planets.add(suiteablePlanet(planetId++, -1));
        }

        fleets.clear();
        fleetsAtDestination.clear();

        lastFleetArrivalTime = 0.0;
        
        players.clear();
        for (Player player : combatants) {
            Player freshOne = player.freshOne();
            freshOne.setUniverse(this);
            createStarterPlanet(planetId++, freshOne);
            players.add(freshOne);
        }

        gameOver = false;

        for (Planet planet : planets) {
            planet.calculateDistancesAndTimes();
        }
    }

    private Planet createStarterPlanet(int planetId, Player player) {
        Planet planet = suiteablePlanet(planetId, Planet.MaximumProductionRatePerSecond);
        planet.setPlayer(player);
        planets.add(planet);
        return planet;
    }

    private Planet suiteablePlanet(int planetId, double productionRate) {
        while (true) {
            Planet planet = new Planet(planetId, this, random, getSize(), productionRate);
            if (planet.fits(planets)) {
                return planet;
            }
        }
    }

    public void update(double elapsedSeconds) throws PlayerDisqualifiedException {
        if (gameOver) {
            return;
        }

        currentPlayer = Player.GoldenPlayer;
        
        now += elapsedSeconds;
        nothingHappenedCounter++;
        currentRound++;

        for (Fleet newFleet : newFleets) {
            fleets.add(newFleet);
        }
        newFleets.clear();

        for (Planet planet : planets) {
            planet.commit();
        }
        
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

        lastFleetArrivalTime = getlastFleetArrivalTime(fleets);
        
        for (Planet planet : planets) {
            planet.prepare();
        }

        if (justOnePlayerLeft()) {
            gameOver = true;
            winner = bestPlayer();
            return;
        }

        long roundSeed = this.random.nextLong();
        
        for (Player player : players) {
            this.random.setSeed(roundSeed);
            currentPlayer = player;
//            try {
            currentPlayer.think();
//            } catch (Exception ex) {
//                throw new PlayerDisqualifiedException(currentPlayer, ex);
//            }
        }
        
        this.random.setSeed(roundSeed);
        currentPlayer = Player.GoldenPlayer;

        if (nothingHappenedCounter > 1000 || currentRound > 100000) {
            gameOver = true;
            winner = Player.NonePlayer;
            return;
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
        List<Player> sortedPlayers = new ArrayList<>(players);
        Collections.sort(sortedPlayers, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return (int) (player2.getFullForce() - player1.getFullForce());
            }
        });

        return sortedPlayers.get(0);
    }

    // for Player
    public List<Planet> getAllPlanets() {
        return new ArrayList<>(planets);
    }

    public List<Fleet> getAllFleets() {
        List<Fleet> result = new ArrayList<>(fleets);
        result.addAll(filterFleetsOf(newFleets, currentPlayer));

        return result;
    }

    List<Fleet> getFleetsOf(Player player) {
        List<Fleet> result = filterFleetsOf(fleets, player);
        if (currentPlayer.equals(player)) {
            result.addAll(filterFleetsOf(newFleets, player));
        }

        return result;
    }

    private List<Fleet> filterFleetsOf(List<Fleet> fleets, Player player) {
        List<Fleet> result = new ArrayList<>();
        for (Fleet fleet : fleets) {
            if (fleet.getPlayer().equals(player)) {
                result.add(fleet);
            }
        }
        return result;
    }

    public Fleet sendPlayerFleet(Planet planet, int force, Planet target) {
        if(!planet.getPlayer().equals(Player.GoldenPlayer)) {
            return null;
        }

        return sendFleet(planet, force, target);
    }

    Fleet sendFleetUpTo(Planet planet, double force, Planet target) {
        if (target == null) {
            return null;
        }
        
        int forcesToSend = (int) Math.min(force, planet.getForces());
        if (forcesToSend > 0) {
            return sendFleet(planet, forcesToSend, target);
        } else {
            return null;
        }
    }
    
    Fleet sendFleet(Planet origin, int force, Planet target) {
        if (!currentPlayer.isMyPlanet(origin)) {
            throw new IllegalArgumentException("Fleet must be send from owned planet. (Sender: " + currentPlayer + "; Owner: " + origin.getPlayer());
        }
        
        if (target == null) {
            throw new IllegalArgumentException("Target planet must not be null!");
        }

        if (force > origin.getForces()) {
            throw new IllegalArgumentException("Fleet size (" + force + ") exceeds planetary forces (" + origin.getForces() + ")");
        }

        if (force < 1) {
            throw new IllegalArgumentException("Fleet size must be at least 1, was: " + force);
        }        

        Fleet newFleet = new Fleet(this, origin.getPlayer(), force, origin, target);
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

    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, random);
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
        return new ArrayList<>(players);
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

    public double getlastFleetArrivalTime() {
        return Math.max(lastFleetArrivalTime, getlastFleetArrivalTime(newFleets));
    }
    
    private double getlastFleetArrivalTime(List<Fleet> fleets) {
        return fleets.isEmpty() ? 0 : Fleet.max(Fleet.ArrivalTimeComparator, fleets).getTimeToTarget();
    }
    
    public Player getNonePlayer() {
        Player result = Player.NonePlayer;
        result.setUniverse(this);
        return result;
    }
    
    public Player getGoldenPlayer() {
        for(Player player : players) {
            if(player.equals(Player.GoldenPlayer)) {
                return player;
            }
        }
        
        return null;
    }

    public int getPlanetsPerPlayer() {
        return planetCountPerPlayer;
    }

    public double getSizeFactor() {
        return universeSizeFactor;
    }

    RuleEngine getRuleEngine() {
        return ruleEngine;
    }
}

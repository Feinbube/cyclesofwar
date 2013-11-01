package cyclesofwar;

import java.util.*;

/*
 * the game is all about planets. get as much as you can ;)
 * 
 * every planet has a position in the 2-dimensional universe. (position can be acquired using getX() and getY())
 * every round a planet produces some troops for its owner. (numbers can be acquired using getForces() and getProductionRatePerRound())
 * the distance to the next planet is always so big that you need at least one round to send a fleet there
 */
public class Planet extends GameObject {

    private final int id;

    private double forces;
    private double newForces;
    private final double productionRatePerSecond;

    private double[] distances;

    private double lastTimeOwnershipChanged;

    /*
     * gets the unique id of a planet
     */
    public int getId() {
        return id;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    /*
     * gets the forces currently residing on the planet
     */
    public double getForces() {
        if (universe.getCurrentPlayer() == player) {
            return newForces;
        } else {
            return forces;
        }
    }

    /*
     * gets the amount of forces that are added to the planet by second
     */
    public double getProductionRatePerSecond() {
        return productionRatePerSecond;
    }

    /*
     * gets the amount of forces that are added to the planet by round
     */
    public double getProductionRatePerRound() {
        return productionRatePerSecond * Universe.getRoundDuration();
    }
    
    public double getActualProductionPerSecond() {
    	return this.getPlayer().equals(Player.NonePlayer) ? 0.0 : this.getProductionRatePerSecond();
    }
    
    public double getActualProductionPerRound() {
    	return this.getActualProductionPerSecond() * Universe.getRoundDuration();
    }

    /*
     * gets the latest point in time when the ownership of that planet was changed
     */
    public double getLastTimeOwnershipChanged() {
        return lastTimeOwnershipChanged;
    }

    double getNewForces() {
        return newForces;
    }

    void setNewForces(double newForces) {
        this.newForces = newForces;
    }

    Planet(int id, Universe universe, Random random, double size, double productionRatePerSecond) {
        super(universe, Player.NonePlayer, random.nextDouble() * size, random.nextDouble() * size);

        this.id = id;

        if (productionRatePerSecond <= 0) {
            productionRatePerSecond = random.nextInt(3) + 1;
        }

        this.productionRatePerSecond = productionRatePerSecond;
        forces = productionRatePerSecond * 10;
        newForces = forces;
        lastTimeOwnershipChanged = universe.getNow();
    }

    @Override
    protected void update(double elapsedSeconds) {
        if (player != Player.NonePlayer) {
            forces += productionRatePerSecond * elapsedSeconds;
        }
    }

    void prepare() {
        newForces = forces;
    }

    void commit() {
        forces = newForces;
    }

    void land(Fleet fleet) {
        if (player == fleet.getPlayer()) {
            forces += fleet.getForce();
        } else {
            forces -= fleet.getForce();
        }

        if (forces < 0) {
            forces = -forces;
            this.player = fleet.getPlayer();
            lastTimeOwnershipChanged = universe.getNow();
        }
    }

    /*
     * Deprecated. use getDistanceTo instead
     */
    @Deprecated
    public double distanceTo(Planet other) {
        return getDistanceTo(other);
    }

    /*
     * distance from this planet to the other planet
     */
    public double getDistanceTo(Planet other) {
        return distances[other.getId()];
    }

    /*
     * Deprecated. use getTimeTo instead
     */
    @Deprecated
    public double timeTo(Planet other) {
        return getTimeTo(other);
    }

    /*
     * time needed to go from this planet to the other planet in seconds
     */
    public double getTimeTo(Planet other) {
        return getDistanceTo(other) / Fleet.getFlightSpeed();
    }

    /*
     * rounds needed to get from this planet to the other planet
     */
    public int getRoundsTo(Planet other) {
        return (int) Math.ceil(getTimeTo(other) * Universe.getRoundsPerSecond());
    }

    /*
     * all other planets in the universe (ascending)
     */
    public List<Planet> getOthers() {
        List<Planet> result = universe.getAllPlanets();
        result.remove(this);
        return result;
    }

    private List<Planet> getOthersByDistanceCache;

    /*
     * all other planets ordered by distance (ascending)
     */
    public List<Planet> getOthersByDistance() {
        if (getOthersByDistanceCache == null) {
            getOthersByDistanceCache = getOthers();
            sortByDistanceTo(getOthersByDistanceCache, this);
        }
        return getOthersByDistanceCache;
    }

    /*
     * sort others by distance to planet (ascending) for performance reasons it
     * is recommended to use filter(planet.getOthersByDistance())
     */
    public static void sortByDistanceTo(List<Planet> others, final Planet planet) {
        Collections.sort(others, new Comparator<Planet>() {
            @Override
            public int compare(Planet planet1, Planet planet2) {
                return Double.compare(planet.getDistanceTo(planet1), planet.getDistanceTo(planet2));
            }
        });
    }

    /*
     * sort others by distance to this planet (ascending)
     */
    public void sortOthersByDistance(List<Planet> others) {
        List<Planet> newOthers = Player.inBothLists(getOthersByDistance(), others);
        others.clear();
        others.addAll(newOthers);
    }
    
    /*
     * used to sort planets by force count (descending)
     * 
     * use Planet.sortBy(planets, ForceCountComparator)
     * or Planet.sortedBy(planets, ForceCountComparator)
     */
    public static Comparator<Planet> ForceCountComparator = new Comparator<Planet>() {
        @Override
        public int compare(Planet planet1, Planet planet2) {
            return Double.compare(planet2.getForces(), planet1.getForces());
        }
    };
    
    /*
     * used to sort planets by production rate (descending)
     * 
     * use Planet.sortBy(planets, ProductivityComparator)
     * or Planet.sortedBy(planets, ProductivityComparator)
     */
    public static Comparator<Planet> ProductivityComparator = new Comparator<Planet>() {
        @Override
        public int compare(Planet planet1, Planet planet2) {
            return Double.compare(planet2.getProductionRatePerSecond(), planet1.getProductionRatePerSecond());
        }
    };
    
    /*
     * used to sort planets by the most recent time the ownership has changed (descending)
     * 
     * use Planet.sortBy(planets, OwnershipChangeTimeComparator)
     * or Planet.sortedBy(planets, OwnershipChangeTimeComparator)
     */
    public static Comparator<Planet> OwnershipChangeTimeComparator = new Comparator<Planet>() {
        @Override
        public int compare(Planet planet1, Planet planet2) {
            return Double.compare(planet2.getLastTimeOwnershipChanged(), planet1.getLastTimeOwnershipChanged());
        }
    };

    /*
     * returns true if the planet is not owned by no player
     */
    public boolean isFree() {
        return player == Player.NonePlayer;
    }

    void calculateDistances() {
        List<Planet> planets = universe.getAllPlanets();
        distances = new double[planets.size()];
        for (int i = 0; i < planets.size(); i++) {
            if (i != planets.get(i).getId()) {
                throw new IllegalStateException();
            }
            distances[i] = calculateDistance(planets.get(i));
        }
    }

    double calculateDistance(Planet other) {
        double xDiff = other.x - this.x;
        double yDiff = other.y - this.y;

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    boolean fits(List<Planet> others) {
        for (Planet other : others) {
            if (istooCloseTo(other)) {
                return false;
            }
        }

        return true;
    }

    private boolean istooCloseTo(Planet other) {
        return calculateDistance(other) < (other.getProductionRatePerSecond() + this.getProductionRatePerSecond()) / 2
                * Fleet.getFlightSpeed();
    }
}

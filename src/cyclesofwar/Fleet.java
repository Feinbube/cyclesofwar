package cyclesofwar;

import java.util.Comparator;

/**
 * the only way to actively interact with the universe is sending fleets from
 * planets you own to other planets
 *
 * every fleet has a position in the 2-dimensional universe. (position can be
 * acquired using getX() and getY()) every fleet consists of a static number of
 * troops. (use getForce() to see their number) every round a fleet is moved
 * closer to the target. (numbers can be acquired using roundsToTarget()) if a
 * fleet arrives at a planet, the following happens to the forces at that
 * planet: planet and fleet belong to the same player: fleet forces join planet
 * forces planet and fleet belong to different players: planet forces are
 * reduced by fleet forces. planet may change owner. the distance to the next
 * planet is always so big that you need at least one round to send a fleet
 * there
 *
 * you can also chose between various formations (which is just a rendering
 * gimmick and does not affect game logic at all)
 */
public class Fleet extends GameObject implements Comparable<Fleet> {

    /**
     * chose a formation to make your fleet look unique just for rendering. does
     * not affect game logic at all
     */
    public enum Formation {
        SWARM, O, EYE, ARROW, V, SPIRAL
    }

    private final Planet target;
    private final int force;

    private double distanceToTarget;
    private double timeToTarget;

    private final double flightSpeed;

    private Formation formation = Formation.SWARM;

    Fleet(Universe universe, Player player, int force, Planet start, Planet target) {
        super(universe, player, start.getX(), start.getY());
        this.target = target;
        this.force = checkForce(start, target, force);

        double xDiff = target.getX() - this.x;
        double yDiff = target.getY() - this.y;

        flightSpeed = getFlightSpeed();

        distanceToTarget = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        timeToTarget = distanceToTarget / flightSpeed;
    }

    private static int checkForce(Planet start, Planet target, int force) {
        if (force < 1) {
            throw new IllegalArgumentException("force must be at least 1 but was " + force);
        }

        if (target == null) {
            throw new IllegalArgumentException("fleet must have a target");
        }

        if (target.equals(start)) {
            throw new IllegalArgumentException("start and target are not allowed to be identical");
        }

        return force;
    }

    /**
     * get the planet this fleet is traveling to
     */
    public Planet getTarget() {
        return target;
    }

    /**
     * gets the strength of the fleet
     */
    public int getForce() {
        return force;
    }

    @Override
    protected void update(double elapsedSeconds) {
        distanceToTarget = ruleEngine.calculateDistance(universe.getSize(), target, this);
        timeToTarget = distanceToTarget / flightSpeed;

        if (distanceToTarget < flightSpeed * elapsedSeconds) {
            hit(distanceToTarget);
        }
        
        x = ruleEngine.getNewPositionX(universe, this, elapsedSeconds);
        y = ruleEngine.getNewPositionY(universe, this, elapsedSeconds);
    }

    private void hit(double distance) {
        universe.fleetArrived(this, distance);
    }

    void land() {
        this.target.land(this);
    }

    /**
     * Deprecated. use getDistanceToTarget()
     */
    @Deprecated
    public double distanceToTarget() {
        return getDistanceToTarget();
    }

    /**
     * current distance of the fleet to the target planet
     */
    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    /**
     * Deprecated. use getTimeToTarget() instead
     */
    @Deprecated
    public double timeToTarget() {
        return getTimeToTarget();
    }

    /**
     * time till the fleet arrives at the target planet in seconds
     */
    public double getTimeToTarget() {
        return timeToTarget;
    }

    /**
     * rounds till the fleet arrives at the target planet in seconds
     */
    public int getRoundsToTarget() {
        return (int) (getTimeToTarget() * Universe.getRoundsPerSecond());
    }

    /**
     * speed of the fleet in distance per second
     */
    public static double getFlightSpeed() {
        return Universe.getFlightSpeed();
    }

    /**
     * chose a formation to make your fleet look unique just for rendering. does
     * not affect game logic at all
     */
    public Formation getFormation() {
        return this.formation;
    }

    /**
     * chose a formation to make your fleet look unique just for rendering. does
     * not affect game logic at all
     */
    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    /**
     * used to sort fleets by arrival time. (ascending)
     *
     * use Fleet.sortBy(fleets, ArrivalTimeComparator) or Fleet.sortedBy(fleets,
     * ArrivalTimeComparator)
     */
    public static Comparator<Fleet> ArrivalTimeComparator = new Comparator<Fleet>() {
        @Override
        public int compare(Fleet one, Fleet other) {
            return Double.compare(one.getTimeToTarget(), other.getTimeToTarget());
        }
    };

    @Override
    public int compareTo(Fleet other) {
        return ArrivalTimeComparator.compare(this, other);
    }
}

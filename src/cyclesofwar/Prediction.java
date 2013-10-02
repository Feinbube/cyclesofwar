package cyclesofwar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 *  This is a prediction of the state of a planet at the given time, given no new fleet is created.
 *  
 *  It takes into account all existing fleets and simulates what happens to the planet, when they arrive.
 */
public class Prediction extends GameObject {

    private final Planet planet;
    private Player planetOwner;
    private double forces;
    private double lastTimeOwnershipChanged;

    private double time;

    /* 
     * the planet this prediction is foretelling
     */
    public Planet getPlanet() {
        return planet;
    }

    /*
     * the owner of the planet at the considered time (use getTime()) 
     */
    public Player getPlanetOwner() {
        return planetOwner;
    }

    /*
     * the forces of the planet at the considered time (use getTime())
     */
    public double getForces() {
        return forces;
    }

    /*
     * returns a positive value equal to the number of forces of the player 
     * if the planet belongs to the player at the considered time (use getTime()) 
     * 
     * returns a negative value equal to the number of forces that are needed to conquer that planet 
     * if the planet belongs to another player at the considered time (use getTime()) 
     *  
     */
    public double getBalance() {
        if (this.planetOwner.equals(this.player)) {
            return forces;
        } else {
            return -forces - 1;
        }
    }

    /*
     * get the latest point in when the ownership of the planet has changed
     */
    public double getLastTimeOwnershipChanged() {
        return lastTimeOwnershipChanged;
    }

    /*
     * the time for which this prediction is foretelling the state of the planet
     */
    public double getTime() {
        return time;
    }

    Prediction(Universe universe, Player player, Planet planet, double time) {
        super(universe, player, planet.getX(), planet.getY());
        this.planet = planet;

        update(time);
    }

    private void reset() {
        this.planetOwner = planet.getPlayer();
        this.forces = planet.getForces();
        this.time = 0.0;
    }

    @Override
    protected void update(double elapsedSeconds) {
        reset();

        List<Fleet> fleets = player.getFleetsWithTarget(planet);
        Fleet.sortBy(Fleet.ArrivalTimeComparator, fleets);
        for (Fleet fleet : fleets) {
            if (fleet.getTimeToTarget() > elapsedSeconds) {
                break;
            }
            this.updateFleet(fleet);
        }

        this.updateTime(elapsedSeconds);
    }

    private void updateFleet(Fleet fleet) {
        update(fleet.getPlayer(), fleet.getForce(), fleet.getTimeToTarget());
    }

    private void update(Player player, int force, double timeToTarget) {
        updateTime(timeToTarget);

        if (this.planetOwner == player) {
            this.forces += force;
        } else {
            this.forces -= force;
            if (this.forces < 0) {
                this.planetOwner = player;
                this.forces *= -1;
            }
        }

        time = timeToTarget;
    }

    private void updateTime(double timeToTarget) {
        double late = timeToTarget - time;
        this.forces += producedForces(late);
    }

    private double producedForces(double duration) {
        if (planetOwner == Player.NonePlayer) {
            return 0;
        } else {
            return duration * this.planet.getProductionRatePerSecond();
        }
    }

    /*
     * sort predictions by distance to specified planet
     */
    public static void sortByDistanceTo(List<Prediction> predictions, final Planet planet) {
        Collections.sort(predictions, new Comparator<Prediction>() {
            @Override
            public int compare(Prediction one, Prediction other) {
                return Double.compare(planet.getDistanceTo(one.planet), planet.getDistanceTo(other.planet));
            }
        });
    }
}

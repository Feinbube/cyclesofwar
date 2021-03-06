package cyclesofwar.players.frank;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  This is a prediction of the state of a planet at the given time, given no new fleet is created.
 *  
 *  It takes into account all existing fleets and simulates what happens to the planet, when they arrive.
 */
public class Prediction {

    private final Player player;
    private final Planet planet;
    private final double productionRate;
    
    private Player planetOwner;
    private double forces;
    private double lastTimeOwnershipChanged;

    private double time;

    /** 
     * the planet this prediction is foretelling
     */
    public Planet getPlanet() {
        return planet;
    }

    /**
     * the owner of the planet at the considered time (use getTime()) 
     */
    public Player getPlanetOwner() {
        return planetOwner;
    }

    /**
     * the forces of the planet at the considered time (use getTime())
     */
    public double getForces() {
        return forces;
    }

    /**
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

    /**
     * get the latest point in when the ownership of the planet has changed
     */
    public double getLastTimeOwnershipChanged() {
        return lastTimeOwnershipChanged;
    }

    /**
     * the time for which this prediction is foretelling the state of the planet
     */
    public double getTime() {
        return time;
    }

    Prediction(Player player, Planet planet, double time) {
        this.player = player;
        this.planet = planet;
        this.productionRate = planet.getProductionRatePerRound();

        update(time);
    }

    private void reset() {
        this.planetOwner = planet.getPlayer();
        this.forces = planet.getForces();
        this.time = 0.0;
    }

    protected void update(double elapsedSeconds) {
        reset();

        double roundDuration = Universe.getRoundDuration();
        
        List<Fleet> fleets = arrivingBefore(player.getFleetsWithTarget(planet), elapsedSeconds + roundDuration);
        
        Fleet.sortBy(Fleet.ArrivalTimeComparator, fleets);

        while (time < elapsedSeconds) {
            updatePlanet();
            updateFleets(fleets, time);

            time += roundDuration;
        }

        updateFleets(fleets, time + roundDuration);
    }

    private void updatePlanet() {
        if (planetOwner != Player.NonePlayer) {
            this.forces += this.productionRate;
        }
    }

    private void updateFleets(List<Fleet> fleets, double time) {
        for (Fleet fleet : arrivingBefore(fleets, time)) {
            this.updateFleet(fleet.getPlayer(), fleet.getForce());
            fleets.remove(fleet);
        }
    }

    private List<Fleet> arrivingBefore(List<Fleet> fleets, double time) {
        List<Fleet> activeFleets = new ArrayList<>();
        for (Fleet fleet : fleets) {
            if (fleet.getTimeToTarget() > time) {
                break;                
            }
            activeFleets.add(fleet);
        }
        return activeFleets;
    }
    
    private void updateFleet(Player player, int force) {

        if (this.planetOwner.equals(player)) {
            this.forces += force;
        } else {
            this.forces -= force;
            if (this.forces < 0) {
                this.planetOwner = player;
                this.forces = -this.forces;
            }
        }
    }

    /**
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

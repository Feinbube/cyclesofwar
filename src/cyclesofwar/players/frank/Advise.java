package cyclesofwar.players.frank;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;

import java.util.ArrayList;
import java.util.List;

/**
 *  This is an advise how to keep planet until a the given point in time, given no new fleet is created.
 *  
 *  It takes into account all existing fleets and simulates what happens to the planet, when they arrive.
 */
public class Advise {

    private double forcesToKeep;
    
    private final Player player;
    private final Planet planet;
    private final double productionRate;
    private final Player planetOwner;
    
    private double startTime;
    private double endTime;
    
    private double forces;
    private double time;
   
    /** 
     * the planet this prediction is foretelling
     */
    public Planet getPlanet() {
        return planet;
    }
    
    /**
     * the forces that are needed on this planet at startTime to survive until endTime
     */
    public double getForcesToKeep() {
        return forcesToKeep;
    }
    
    /**
     * the start time that this advise is based on 
     * (it is assumed that the planet is owned by you at that time)
     */
    public double getStartTime() {
        return startTime;
    }
    
    /**
     * the end time that this advise is based on 
     * (if no new fleets are created and you had the required forces at start time,
     * the planet will still be yours at this point in time)
     */
    public double getEndTime() {
        return endTime;
    }

    Advise(Player player, Planet planet, double startTime, double endTime) {
        this.player = player;
        this.planet = planet;
        this.productionRate = this.planet.getProductionRatePerRound();
        this.startTime = startTime;
        this.planetOwner = planet.getPlayer();

        update(endTime);
    }
    
    Advise(Player player, Planet planet, double time) {
        this(player, planet, 0.0, time);
    }

    private void reset() {
        this.forces = 0.0;
        this.time = startTime;
        this.forcesToKeep = 0.0;
    }

    protected void update(double elapsedSeconds) {
        reset();

        double roundDuration = Universe.getRoundDuration();
        elapsedSeconds += roundDuration;
        
        List<Fleet> fleets = arrivingBefore(player.getFleetsWithTarget(planet), elapsedSeconds);
        
        if(!this.player.containsHostileItem(fleets))
            return;
        
        Fleet.sortBy(Fleet.ArrivalTimeComparator, fleets);

        while (time < elapsedSeconds) {
            updatePlanet();
            updateFleets(fleets, time);
    
            time += roundDuration;
        }
    }

    private void updatePlanet() {
        if (planetOwner != Player.NonePlayer) {
            this.forces += this.productionRate;
        }
    }

    private List<Fleet> removeEarlyFleets(List<Fleet> fleets, double time){
        List<Fleet> result = new ArrayList<>();
        for (Fleet fleet : fleets) {
            if (fleet.getTimeToTarget() >= time) {
                result.add(fleet);
            }
        }
        return result;
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
            if (fleet.getTimeToTarget() > time + 0.2) {
                break;                
            }
            activeFleets.add(fleet);
        }
        return activeFleets;
    }

    private void updateFleet(Player player, int force) {

        if (this.planet.getPlayer().equals(player)) {
            this.forces += force;
        } else {
            this.forces -= force;
            if (this.forces < 0) {
                this.forcesToKeep -= this.forces;
                this.forces = 0;
            }
        }
    }
}

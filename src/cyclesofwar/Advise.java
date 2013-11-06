package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

/*
 *  This is an advise how to keep planet until a the given point in time, given no new fleet is created.
 *  
 *  It takes into account all existing fleets and simulates what happens to the planet, when they arrive.
 */
public class Advise extends GameObject {

    private double forcesToKeep;
    
    private final Planet planet;
    
    private double startTime;
    private double endTime;
    
    private double forces;
    private double time;

    /* 
     * the planet this prediction is foretelling
     */
    public Planet getPlanet() {
        return planet;
    }
    
    /*
     * the forces that are needed on this planet at startTime to survive until endTime
     */
    public double getForcesToKeep() {
        return forcesToKeep;
    }
    
    /*
     * the start time that this advise is based on 
     * (it is assumed that the planet is owned by you at that time)
     */
    public double getStartTime() {
        return startTime;
    }
    
    /*
     * the end time that this advise is based on 
     * (if no new fleets are created and you had the required forces at start time,
     * the planet will still be yours at this point in time)
     */
    public double getEndTime() {
        return endTime;
    }

    Advise(Universe universe, Player player, Planet planet, double startTime, double endTime) {
        super(universe, player, planet.getX(), planet.getY());
        this.planet = planet;
        this.startTime = startTime;

        update(endTime);
    }
    
    Advise(Universe universe, Player player, Planet planet, double time) {
        this(universe, player, planet, 0.0, time);
    }

    private void reset() {
        this.forces = 0.0;
        this.time = startTime;
        this.forcesToKeep = 0.0;
    }

    @Override
    protected void update(double elapsedSeconds) {
        reset();

        double roundDuration = Universe.getRoundDuration();
        List<Fleet> fleets = removeEarlyFleets(player.getFleetsWithTargetSortedByArrivalTime(planet), this.startTime);
        
        if(!this.player.containsHostileItem(fleets))
            return;

        while (time < elapsedSeconds) {
            updatePlanet();
            updateFleets(fleets, time);

            time += roundDuration;
        }

        updateFleets(fleets, time + roundDuration);
    }

    private void updatePlanet() {
        if (this.planet.getPlayer() != Player.NonePlayer) {
            this.forces += this.planet.getProductionRatePerRound();
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
        List<Fleet> activeFleets = new ArrayList<>();
        for (Fleet fleet : fleets) {
            if (fleet.getTimeToTarget() <= time) {
                activeFleets.add(fleet);
            }
        }

        for (Fleet fleet : activeFleets) {
            this.updateFleet(fleet.getPlayer(), fleet.getForce(), fleet.getTimeToTarget());
            fleets.remove(fleet);
        }
    }

    private void updateFleet(Player player, int force, double timeToTarget) {

        if (this.planet.getPlayer() == player) {
            this.forces += force;
        } else {
            this.forces -= force;
            if (this.forces < 0) {
                this.forcesToKeep -= this.forces;
                this.forces = 0;
            }
        }

        time = timeToTarget;
    }
}

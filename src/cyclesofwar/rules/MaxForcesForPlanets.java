
package cyclesofwar.rules;

import cyclesofwar.Planet;

public class MaxForcesForPlanets extends RuleProxy {
    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {  
        double max = planet.getProductionRatePerSecond() * 20;
        
        return planet.getForces() >= max ? planet.getForces() : Math.min(this.getNext().getNewForces(planet, elapsedSeconds), max);
    }
}

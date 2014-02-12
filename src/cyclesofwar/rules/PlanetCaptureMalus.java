
package cyclesofwar.rules;

import cyclesofwar.Planet;
import cyclesofwar.Universe;

public class PlanetCaptureMalus extends RuleProxy {
    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {
        
        double rounds = (ruleEngine.getUniverse().getNow() - planet.getLastTimeOwnershipChanged()) / Universe.getRoundDuration();
        
        // first 100 rounds after capture: no production
        if(rounds < 100) {
            return planet.getForces();
        }
        
        // next 400 rounds: linear increasing production
        if(rounds < 500) {
            double factor = (rounds - 100) / 400.0;
            double intendedForceGain = this.getNext().getNewForces(planet, elapsedSeconds) - planet.getForces();
            return planet.getForces() + intendedForceGain * factor;
        }
        
        return this.getNext().getNewForces(planet, elapsedSeconds);
    }
}

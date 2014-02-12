
package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

public class Veterans extends RuleProxy {

    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        double intendedForces = this.getNext().getForcesAfterLanding(planet, fleet);
        
        // no fight -> no vets
        if(intendedForces > planet.getForces()) {
            return intendedForces;
        }
        
        // fight -> everyone who fought and survived counts for 1.5 soldiers now ;)
        if(intendedForces < 0){ // attacker won 
            return Math.min(-intendedForces, fleet.getForce() + intendedForces) * -0.5 + intendedForces;
        } else { // defender won
            return Math.min(intendedForces, planet.getForces() - intendedForces) * 0.5 + intendedForces;
        }
    }
}

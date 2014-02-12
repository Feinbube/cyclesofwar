
package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

public class TotalWinOrLoose extends RuleProxy {

    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        return this.getNext().getForcesAfterLanding(planet, fleet) < 0 ? -fleet.getForce() : planet.getForces();
    }
}

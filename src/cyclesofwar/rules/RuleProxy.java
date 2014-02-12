package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

public class RuleProxy extends Rule {

    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {
        return this.getNext().getNewForces(planet, elapsedSeconds);
    }

    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        return this.getNext().getForcesAfterLanding(planet, fleet);
    }
}

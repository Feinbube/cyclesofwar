package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.GameObject;
import cyclesofwar.Planet;
import cyclesofwar.Universe;

public class RuleProxy extends Rule {

    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {
        return this.getNext().getNewForces(planet, elapsedSeconds);
    }

    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        return this.getNext().getForcesAfterLanding(planet, fleet);
    }

    @Override
    public double calculateDistance(double universeSize, GameObject start, GameObject destination) {
        return this.getNext().calculateDistance(universeSize, start, destination);
    }

    @Override
    public double getNewPositionX(Universe universe, Fleet fleet, double elapsedSeconds) {
        return this.getNext().getNewPositionX(universe, fleet, elapsedSeconds);
    }

    @Override
    public double getNewPositionY(Universe universe, Fleet fleet, double elapsedSeconds) {
        return this.getNext().getNewPositionY(universe, fleet, elapsedSeconds);
    }
}

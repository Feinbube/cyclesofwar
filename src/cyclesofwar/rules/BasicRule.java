package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class BasicRule extends Rule {
    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {
        return planet.getPlayer() != Player.NonePlayer ? planet.getForces() + planet.getProductionRatePerSecond() * elapsedSeconds : planet.getForces();
    }
    
    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        return planet.getPlayer().equals(fleet.getPlayer()) ? planet.getForces() + fleet.getForce() : planet.getForces() - fleet.getForce();
    }
}

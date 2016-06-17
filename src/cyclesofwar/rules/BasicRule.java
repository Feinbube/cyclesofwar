package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.GameObject;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;

public class BasicRule extends Rule {
    
    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {
        return planet.getPlayer() != Player.NonePlayer ? planet.getForces() + planet.getProductionRatePerSecond() * elapsedSeconds : planet.getForces();
    }
    
    @Override
    public double getForcesAfterLanding(Planet planet, Fleet fleet) {
        return planet.getPlayer().equals(fleet.getPlayer()) ? planet.getForces() + fleet.getForce() : planet.getForces() - fleet.getForce();
    }
    
    @Override
    public double calculateDistance(double universeSize, GameObject start, GameObject destination) {
        double xDiff = Math.abs(destination.getX() - start.getX());
        double yDiff = Math.abs(destination.getY() - start.getY());

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    @Override
    public double getNewPositionX(Universe universe, Fleet fleet, double elapsedSeconds) {
        return fleet.getX() - Fleet.getFlightSpeed() * elapsedSeconds * (fleet.getX() - fleet.getTarget().getX()) / fleet.getDistanceToTarget();
    }

    @Override
    public double getNewPositionY(Universe universe, Fleet fleet, double elapsedSeconds) {
        return fleet.getY() - Fleet.getFlightSpeed() * elapsedSeconds * (fleet.getY() - fleet.getTarget().getY()) / fleet.getDistanceToTarget();
    }
}

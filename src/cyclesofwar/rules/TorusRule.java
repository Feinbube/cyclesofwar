package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.GameObject;
import cyclesofwar.Universe;

public class TorusRule extends RuleProxy {
    
    @Override
    public double calculateDistance(double universeSize, GameObject start, GameObject destination) {
        double xDiff = Math.abs(destination.getX() - start.getX());
        double yDiff = Math.abs(destination.getY() - start.getY());

        // lets make it a torus
        xDiff = Math.min(xDiff, universeSize - xDiff);
        yDiff = Math.min(yDiff, universeSize - yDiff);

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    
    @Override
    public double getNewPositionX(Universe universe, Fleet fleet, double elapsedSeconds){
        return getNewPosition(universe, fleet, elapsedSeconds, fleet.getX(), fleet.getTarget().getX());
    }
    
    @Override
    public double getNewPositionY(Universe universe, Fleet fleet, double elapsedSeconds){
        return getNewPosition(universe, fleet, elapsedSeconds, fleet.getY(), fleet.getTarget().getY());
    }
    
    private double getNewPosition(Universe universe, Fleet fleet, double elapsedSeconds, double resultXY, double targetXY)
    {
        double diffXY = Math.abs(resultXY - targetXY);
        
        if (diffXY < universe.getSize() - diffXY) {
            resultXY -= Fleet.getFlightSpeed() * elapsedSeconds * (resultXY - targetXY) / fleet.getDistanceToTarget();
        } else {
            resultXY -= Fleet.getFlightSpeed() * elapsedSeconds * (targetXY - resultXY) / fleet.getDistanceToTarget();
        }

        if (resultXY < 0) {
            resultXY += universe.getSize();
        }
        if (resultXY > universe.getSize()) {
            resultXY -= universe.getSize();
        }
        
        return resultXY;
    }
}


package cyclesofwar.rules;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class MaxForcesTotal extends RuleProxy {
    @Override
    public double getNewForces(Planet planet, double elapsedSeconds) {          
        return planet.getPlayer().equals(Player.NonePlayer) || planet.getPlayer().getFullForce() >= ruleEngine.universe.getPlayers().size() * 100 
                ? planet.getForces() : this.getNext().getNewForces(planet, elapsedSeconds);
    }
}

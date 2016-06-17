package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.GameObject;
import cyclesofwar.Planet;
import cyclesofwar.Universe;

public abstract class Rule {
    RuleEngine ruleEngine;
    
    public void setRuleEngine(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }
    
    Rule getNext() {
        return ruleEngine.getNext(this);
    }
    
    abstract public double getNewForces(Planet planet, double elapsedSeconds);
    abstract public double getForcesAfterLanding(Planet planet, Fleet fleet);
    abstract public double calculateDistance(double universeSize, GameObject start, GameObject destination);
    abstract public double getNewPositionX(Universe universe, Fleet fleet, double elapsedSeconds);
    abstract public double getNewPositionY(Universe universe, Fleet fleet, double elapsedSeconds);
}

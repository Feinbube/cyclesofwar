package cyclesofwar.rules;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;

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
}

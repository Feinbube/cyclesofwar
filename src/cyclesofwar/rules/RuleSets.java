package cyclesofwar.rules;

import cyclesofwar.Universe;

public class RuleSets {

    public static RuleEngine Basic(Universe universe) {
        return new RuleEngine(universe, new Rule[]{});
    }
    
    public static RuleEngine PlanetCaptureMalus(Universe universe) {
        return new RuleEngine(universe, new Rule[]{new PlanetCaptureMalus()});
    }
    
    public static RuleEngine MaxForcesForPlanets(Universe universe) {
        return new RuleEngine(universe, new Rule[]{new MaxForcesForPlanets()});
    }
    
    public static RuleEngine MaxForcesTotal(Universe universe) {
        return new RuleEngine(universe, new Rule[]{new MaxForcesTotal()});
    }
    
     public static RuleEngine Veterans(Universe universe) {
        return new RuleEngine(universe, new Rule[]{new Veterans()});
    }
}

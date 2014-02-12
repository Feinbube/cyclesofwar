package cyclesofwar.rules;

import cyclesofwar.Universe;
import java.util.ArrayList;
import java.util.List;

public class RuleEngine extends RuleProxy {
    List<Rule> rules;
    Universe universe;

    public RuleEngine(Universe universe, Rule[] list) {
        rules = new ArrayList<>();
        this.universe = universe;
        for(Rule rule : list) {
            rule.setRuleEngine(this);
            rules.add(rule);
        }
        Rule basicRule = new BasicRule();
        basicRule.setRuleEngine(this);
        rules.add(basicRule);
    }

    Universe getUniverse() {
        return universe;
    }
    
    Rule getFirst() {
        return rules.get(0);
    }
    
    @Override
    Rule getNext() {
        return getFirst();
    }
    
    Rule getNext(Rule rule) {
        return rules.get(rules.indexOf(rule)+1);
    }
}

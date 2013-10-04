package cyclesofwar.players.frank.gene;

import cyclesofwar.Planet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ValueEntry {

    Planet local;
    Value localValue;
    Planet target;
    Value targetValue;

    ValueEntry(Planet local, Value localValue, Planet target, Value targetValue) {
        this.local = local;
        this.localValue = localValue;

        this.target = target;
        this.targetValue = targetValue;
    }

    double getPriority() {
        return targetValue.getPriority();
    }

    int getForces() {
        return targetValue.getForces();
    }
    
    Planet getPlanet(){
        return local;
    }
    
    Planet getTarget(){
        return target;
    }

    boolean isValueable() {
        if (targetValue.getPriority() > localValue.getPriority()) {
            return true;
        } else if (targetValue.getForces() <= local.getForces() - localValue.getForces()) {
            return true;
        }

        return false;
    }
}

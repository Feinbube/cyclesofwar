package cyclesofwar.players.frank.gene.training;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.gene.Gene;
import cyclesofwar.players.frank.gene.Value;

public class G04_Random extends Gene {

    @Override
    protected Value getValue(Planet local) {
        return new Value(2, local.getForces()*0.5);
    }

    @Override
    protected Value getValue(Planet local, Planet other) {
        if (local.getForces() > 20) {
            return new Value(1, local.getForces()*0.5);
        }

        return null;
    }
}

package cyclesofwar.players.frank.gene.training;

import cyclesofwar.Planet;
import cyclesofwar.players.frank.gene.Gene;
import cyclesofwar.players.frank.gene.Value;

public class G02_Wave extends Gene {

    @Override
    protected Value getValue(Planet local) {
        return new Value(1, 0);
    }

    @Override
    protected Value getValue(Planet local, Planet other) {
        if(local.getForces() > this.getAllPlanets().size())
            return new Value(2, 1);

        return null;
    }
}

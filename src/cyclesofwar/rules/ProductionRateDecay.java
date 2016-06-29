
package cyclesofwar.rules;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.PlayerDisqualifiedException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductionRateDecay extends RuleProxy {
    @Override
    public void preUniverseRoundUpdate(double elapsedSeconds) {
        for (Planet planet : this.ruleEngine.universe.getAllPlanets()) {
            double newRate = planet.getProductionRatePerSecond();
            if(newRate == 0)
                continue;
            try {
                if(!planet.getPlayer().equals(Player.NonePlayer))
                    this.ruleEngine.universe.setProductionRatePerSecond(planet, Math.max(1, newRate - 0.1 * elapsedSeconds));
            } catch (PlayerDisqualifiedException ex) {
                Logger.getLogger(ProductionRateDecay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

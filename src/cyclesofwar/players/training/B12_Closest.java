package cyclesofwar.players.training;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * Each planet of this bot that has more than 20 forces will attack the closest hostile planet.
 */
public class B12_Closest extends Player {

    @Override
    public void think() {

        // for each of my planets
        for (Planet planet : this.getPlanets()) {

            // if a planet has enough forces to attack
            if (planet.getForces() > 20) {

                // attack the hostile planet that is closest to this planet
                sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planet.getOthersByDistance())));
            }
        }
    }

    @Override
    public Color getPlayerBackColor() {
        return Color.gray.darker();
    }

    @Override
    public Color getPlayerForeColor() {
        return Color.yellow;
    }

    @Override
    public String getCreatorsName() {
        return "B";
    }
}

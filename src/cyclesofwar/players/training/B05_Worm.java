package cyclesofwar.players.training;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/**
 * This bot is winding itself through the galaxy like a worm.
 * The head decides for new targets and the tails is just following the rest of the body. 
 */
public class B05_Worm extends Player {

    @Override
    public void think() {

        // sort my planets by latest ownership change (index=0 will be my oldest, index=size-1 my newest planet)
        List<Planet> planets = Planet.sortedBy(Planet.OwnershipChangeTimeComparator, this.getPlanets());

        // for each of my planets
        for (Planet planet : planets) {

            // if a planet has enough forces to attack
            if (planet.getForces() > 20) {

                // get its index to see if it is the head
                int index = planets.indexOf(planet);

                // if it is the head
                if (index == 0) {
                    // attack the closest hostile planet
                    sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planet.getOthersByDistance())));
                } else {
					// if it is part of the tail, send troops to the next part of the tail
                    // that is closer to the head
                    sendFleetUpTo(planet, 10, planets.get(index - 1));
                }
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
        return null;
    }
}

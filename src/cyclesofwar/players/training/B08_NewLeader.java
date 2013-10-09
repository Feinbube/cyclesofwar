package cyclesofwar.players.training;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * This bot is very similar to the worm bot. It's winding through the galaxy as well,
 * but the tail planets also attack the target of the head instead of following the rest of the body.
 */
public class B08_NewLeader extends Player {

    @Override
    public void think() {

        // sort my planets by latest ownership change (index=0 will be my oldest, index=size-1 my newest planet)
        List<Planet> planets = Planet.sortedBy(Planet.OwnershipChangeTimeComparator, this.getPlanets());

        // for each of my planets
        for (Planet planet : planets) {

            // if a planet has enough forces to attack
            if (planet.getForces() > 20) {

                // attack the hostile planet that is closest to the front planet
                sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(planets.get(0).getOthersByDistance())));
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

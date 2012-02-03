package cyclesofwar.players;

import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Defender extends Player {

	@Override
	protected void think() {
		List<Planet> myPlanets = this.getPlanets();
		sortByForceCount(myPlanets);

		if (myPlanets.size() == 0)
			return;

		Planet capital = myPlanets.get(0);
		myPlanets.remove(capital);

		List<Planet> other = getAllPlanetButMine();
		sortByForceCount(other);
		if (other.size() > 0) {
			Planet target = other.get(other.size() - 1);
			int requierdForce = (int)target.getForces() + 1;
			if (requierdForce >= capital.getForces()) {
				sendFleetUpTo(capital, requierdForce, target);
			}
		}

		for (Planet planet : myPlanets) {
			if (planet.getForces() > 5) {
				int requierdForce = (int)planet.getForces() / 4;
				if (requierdForce >= planet.getForces()) {
					sendFleet(planet, requierdForce, capital);
				}
			}
		}

	}

	@Override
	public Color getPlayerBackColor() {
		return Color.red;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	public String getCreatorsName() {
		return "Robert";
	}

}

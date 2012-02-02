package cyclesofwar.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Petra extends Player {

	List<Fleet> fleetsHandled = new ArrayList<Fleet>();

	@Override
	public void think() {
		for (Planet planet : this.getPlanets()) {
			Planet target = getTarget(planet);
			if (target == null)
				continue;

			if (planet.getForces() > target.getForces() * 1.5)
				this.sendFleet(planet, (int) (planet.getForces() * 0.8), target);
		}
	}

	private Planet getTarget(final Planet planet) {
		List<Planet> targets = mostProductive(notOwnedByMe(getOtherPlanets(planet)));
		if (targets.size() == 0)
			return null;

		Collections.sort(targets, new Comparator<Planet>() {
			@Override
			public int compare(Planet planet1, Planet planet2) {
				return (int) (planet.distanceTo(planet1) - planet.distanceTo(planet2));
			}
		});

		return targets.get(0);
	}

	private List<Planet> mostProductive(List<Planet> planets) {
		double maxProductionRate = 0.0;
		for (Planet planet : planets) {
			if (planet.getProductionRatePerSecond() > maxProductionRate) {
				maxProductionRate = planet.getProductionRatePerSecond();
			}
		}

		List<Planet> result = new ArrayList<Planet>();
		
		for (Planet planet : planets) {
			if (planet.getProductionRatePerSecond() == maxProductionRate) {
				result.add(planet);
			}
		}

		return result;
	}

	private List<Planet> notOwnedByMe(List<Planet> planets) {
		List<Planet> result = new ArrayList<Planet>();

		for (Planet planet : planets)
			if (!planet.getPlayer().equals(this))
				result.add(planet);

		return result;
	}

	private List<Planet> getOtherPlanets(Planet planet) {
		List<Planet> result = this.getAllPlanets();
		result.remove(planet);

		return result;
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.blue.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange;
	}

	@Override
	public String getCreatorsName() {
		return "Frank";
	}

}

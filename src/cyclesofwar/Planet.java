package cyclesofwar;

import java.util.List;
import java.util.Random;

public class Planet {
	private static int nextid;
	private int id;

	private Universe universe;

	private double x;
	private double y;

	private Player player;
	private double forces;
	private double newForces;

	private double productionRatePerSecond;

	public int getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	void setPlayer(Player player) {
		this.player = player;
	}

	public double getForces() {
		if (universe.getCurrentPlayer().equals(player)) {
			return newForces;
		} else {
			return forces;
		}
	}

	public double getProductionRatePerSecond() {
		return productionRatePerSecond;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	double getNewForces() {
		return newForces;
	}

	void setNewForces(double newForces) {
		this.newForces = newForces;
	}

	Planet(Universe universe, Random random, double size, double productionRatePerSecond) {
		this.universe = universe;

		nextid++;
		id = nextid;
		this.player = Player.NonePlayer;

		if (productionRatePerSecond <= 0) {
			productionRatePerSecond = random.nextInt(3) + 1;
		}

		this.productionRatePerSecond = productionRatePerSecond;
		forces = productionRatePerSecond * 10;
		newForces = forces;

		x = random.nextDouble() * size;
		y = random.nextDouble() * size;
	}

	void update(double elapsedSeconds) {
		if (!player.equals(Player.NonePlayer)) {
			forces += productionRatePerSecond * elapsedSeconds;
		}
	}

	void prepare() {
		newForces = forces;
	}

	void commit() {
		forces = newForces;
	}

	void land(Fleet fleet) {
		if (player.equals(fleet.getPlayer())) {
			forces += fleet.getForce();
		} else {
			forces -= fleet.getForce();
		}

		if (forces < 0) {
			forces = -forces;
			this.player = fleet.getPlayer();
		}
	}

	boolean fits(List<Planet> others) {
		for (Planet other : others) {
			if (tooCloseTo(other))
				return false;
		}

		return true;
	}

	private boolean tooCloseTo(Planet other) {
		return distanceTo(other) < (other.productionRatePerSecond + this.productionRatePerSecond) / 2 * Universe.speedOfLight;
	}

	// for players

	public double distanceTo(Planet other) {
		double xDiff = other.x - this.x;
		double yDiff = other.y - this.y;

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

	public double timeTo(Planet other) {
		return distanceTo(other) / Universe.speedOfLight;
	}
}
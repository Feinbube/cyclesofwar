package cyclesofwar;

import java.util.Random;

public class Planet {

	double size;
	double x;
	double y;

	Player player;
	double forces;
	double newForces;

	double productionRatePerSecond;

	public Player getPlayer() {
		return player;
	}

	public double getForces() {
		if (Universe.INSTANCE.currentPlayer.equals(player)) {
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

	Planet(Random random, double size, double productionRatePerSecond) {
		this.size = size;
		this.player = Player.NonePlayer;

		if (productionRatePerSecond <= 0) {
			productionRatePerSecond = random.nextInt(3) + 1;
		}

		this.productionRatePerSecond = productionRatePerSecond;
		forces = productionRatePerSecond * 10;

		x = random.nextDouble() * size;
		y = random.nextDouble() * size;
	}

	void update(double elapsedSeconds) {
		if (!player.equals(Player.NonePlayer))
			forces += productionRatePerSecond * elapsedSeconds;
	}

	public double distanceTo(Planet other) {
		double xDiff = other.x - this.x;
		double yDiff = other.y - this.y;

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

	public double timeTo(Planet other) {
		return distanceTo(other) / Universe.speedOfLight;
	}
	
	void commit() {
		forces = newForces;
	}
}
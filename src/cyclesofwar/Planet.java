package cyclesofwar;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

public class Planet extends Drawable {
	Player player;
	double forces;

	double productionRatePerSecond;

	public Player getPlayer() {
		return player;
	}

	public int getForces() {
		return (int)forces;
	}

	public double getProductionRatePerSecond() {
		return productionRatePerSecond;
	}

	public double getX() {
		return (double) x;
	}

	public double getY() {
		return (double) y;
	}

	Planet(Random random, Dimension size, int d) {
		this.player = Player.IdlePlayer;

		if (d <= 0) {
			d = random.nextInt(3) + 1;
		}

		productionRatePerSecond = d;
		forces = d * 10;

		d = d * 15;

		x = random.nextInt(size.width - d) + d / 2;
		y = random.nextInt(size.height - d) + d / 2;

		this.d = d;
	}

	Planet(Random random, Dimension size) {
		this(random, size, -1);
	}

	protected void draw(Graphics g) {

		super.c = player.getPlayerBackColor();

		super.draw(g);

		drawText(g, ((int) forces) + "", player.getPlayerForeColor());
	}

	boolean collidesWith(Planet other) {
		double xDiff = this.x - other.x;
		double yDiff = this.y - other.y;
		double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

		if (distance <= this.d / 2 + other.d / 2)
			return true;
		else
			return false;
	}

	void update(double elapsedSeconds) {
		if (!player.equals(Player.IdlePlayer))
			forces += productionRatePerSecond * elapsedSeconds;
	}

	public double distanceTo(Planet other) {
		double xDiff = other.x - this.x;
		double yDiff = other.y - this.y;
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
}
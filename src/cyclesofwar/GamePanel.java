package cyclesofwar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	Dimension size = new Dimension(0, 0);
	double universeSize = 0.0;

	final int StarCount = 1000;
	List<Star> stars = new ArrayList<Star>();

	int borderSize = 20;

	static final double planetSizingFactor = 0.015;

	Random random = new Random();

	MainThread mainThread = new MainThread(this);

	public GamePanel() {
		new Thread(mainThread).start();
	}

	protected void paintComponent(Graphics g) {
		synchronized (mainThread.renderingLock) {
			if (!this.size.equals(getSize())) {
				reInitialize();
			}

			drawUniverse(g);
		}
	}

	private void reInitialize() {
		Universe.INSTANCE.reInitialize();

		stars.clear();
		for (int i = 0; i < StarCount; i++) {
			stars.add(new Star(random, Universe.INSTANCE.size));
		}

		this.universeSize = Universe.INSTANCE.size;
		this.size = getSize();
		this.borderSize = planetSize(size.width, 5.0) / 2;
	}

	void drawUniverse(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

		drawStars(g);

		if (!mainThread.gameStarted) {
			drawTitleScreen(g);
		} else if (!Universe.INSTANCE.gameOver) {
			drawPlanets(g);
			drawFleets(g);
			drawPlayers(g);
		} else {
			drawGameOverScreen(g);

		}
	}

	private void drawTitleScreen(Graphics g) {
		g.setColor(Color.yellow);

		g.setFont(new Font("Courier New", Font.BOLD, 48));

		String s = "Cycles of War";
		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();

		int hStart = g.getClipBounds().height / 2 + h / 2;
		g.drawString(s, g.getClipBounds().width / 2 - w / 2, hStart);

	}

	private void drawGameOverScreen(Graphics g) {
		String playerName = Universe.INSTANCE.winner.getName();

		g.setColor(Color.yellow);

		g.setFont(new Font("Courier New", Font.BOLD, 48));

		String s = "GAME OVER";
		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();

		int hStart = g.getClipBounds().height / 3 + h / 2;
		g.drawString(s, g.getClipBounds().width / 2 - w / 2, hStart);

		g.setFont(new Font("Courier New", Font.PLAIN, 32));

		s = playerName + " has won!";
		w = g.getFontMetrics().stringWidth(s);
		h = g.getFontMetrics().getHeight();

		g.drawString(s, g.getClipBounds().width / 2 - w / 2, hStart + h * 2);
	}

	private void drawStars(Graphics g) {
		for (Star star : stars) {
			g.setColor(star.c);

			int starSize = (int) (star.d * 4 + 1);
			int x = (int) (g.getClipBounds().width * star.x / universeSize);
			int y = (int) (g.getClipBounds().height * star.y / universeSize);
			g.fillOval(x - starSize / 2, y - starSize / 2, starSize, starSize);
		}
	}

	private void drawPlanets(Graphics g) {
		for (Planet planet : Universe.INSTANCE.planets) {
			g.setColor(planet.player.getPlayerBackColor());

			int x = getX(g, planet.x);
			int y = getY(g, planet.y);
			int planetSize = planetSize(g, planet.productionRatePerSecond);
			g.fillOval(x - planetSize / 2, y - planetSize / 2, planetSize, planetSize);

			drawStringCentered(g, ((int) planet.forces) + "", x, y, planet.player.getPlayerForeColor());
		}
	}

	private int planetSize(Graphics g, double productionRatePerSecond) {
		return planetSize(g.getClipBounds().width, productionRatePerSecond);
	}

	private int planetSize(int size, double productionRatePerSecond) {
		return (int) (productionRatePerSecond * 15.0 * size / 1000.0);
	}

	private int getX(Graphics g, double x) {
		return (int) ((g.getClipBounds().width - borderSize * 2) * x / universeSize) + borderSize;
	}

	private int getY(Graphics g, double y) {
		return (int) ((g.getClipBounds().height - borderSize * 2) * y / universeSize) + borderSize;
	}

	private void drawStringCentered(Graphics g, String s, int x, int y, Color c) {
		g.setColor(c);
		g.setFont(new Font("Arial", Font.PLAIN, 10));

		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();

		g.drawString(s, x - w / 2, y + h / 2);
	}

	private void drawFleets(Graphics g) {
		for (Fleet fleet : Universe.INSTANCE.fleets) {
			g.setColor(fleet.player.getPlayerBackColor());

			int x = getX(g, fleet.x);
			int y = getY(g, fleet.y);
			int d = fleet.force;

			for (int i = 0; i < fleet.force; i++) {
				double r = random.nextDouble() * d;

				double localX = random.nextDouble() * r;
				if (random.nextBoolean()) {
					localX = -localX;
				}
				double localY = Math.sqrt(r * r - localX * localX);
				if (random.nextBoolean()) {
					localY = -localY;
				}

				g.fillOval(x + (int) localX / 2, y + (int) localY / 2, 4, 4);
			}

			// g.fillRect(x - d / 2, y - d / 2, d, d);
			// drawStringCentered(g, fleet.force + "", x, y,
			// fleet.player.getPlayerForeColor());
		}
	}

	private void drawPlayers(Graphics g) {
		for (int i = 0; i < Universe.INSTANCE.players.size(); i++) {
			Player player = Universe.INSTANCE.players.get(i);

			String s = statistics(player);

			int x = 5;
			int y = i * 20;
			g.setFont(new Font("Arial", Font.PLAIN, 12));

			int w = g.getFontMetrics().stringWidth(s);
			int h = g.getFontMetrics().getHeight();

			g.setColor(player.getPlayerBackColor());
			g.fillRect(x - 2, y + 3, w + 3, h);

			g.setColor(player.getPlayerForeColor());
			g.drawString(s, x, y + h);
		}
	}

	private String statistics(Player player) {
		String result = "";

		result += player.getName();

		double groundForces = 0;
		for (Planet planet : Universe.INSTANCE.PlanetsOfPlayer(player))
			groundForces += planet.forces;

		result += " P[" + Universe.INSTANCE.PlanetsOfPlayer(player).size() + "/" + ((int) groundForces) + "]";

		int spaceForces = 0;
		for (Fleet fleet : Universe.INSTANCE.FleetsOfPlayer(NonePlayer.NonePlayer, player))
			spaceForces += fleet.force;

		result += " F[" + Universe.INSTANCE.FleetsOfPlayer(NonePlayer.NonePlayer, player).size() + "/" + spaceForces + "]";

		return result;
	}
}
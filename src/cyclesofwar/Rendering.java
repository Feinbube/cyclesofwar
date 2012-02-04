package cyclesofwar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Fleet.Formation;

class Rendering {
	final int StarCount = 1000;
	final int MaxRenderedFleet = 20;
	
	Dimension size = new Dimension(0, 0);
	double universeSize = 0.0;
	int borderSize = 20;
	
	List<Star> stars = new ArrayList<Star>();
	
	Universe universe;
	
	Random random = new Random();
	
	void drawUniverse(Graphics g, Universe universe, Dimension size, boolean titleScreen) {
		this.universe = universe;
		
		if (!this.size.equals(size) || this.universeSize != universe.size) {
			stars.clear();
			for (int i = 0; i < StarCount; i++) {
				stars.add(new Star(random, universe.size));
			}
			
			this.universeSize = universe.size;
			this.size = size;
			this.borderSize = planetSize(size.width, 5.0) / 2;
		}

		g.setColor(Color.black);
		g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

		drawStars(g);

		if (titleScreen) {
			drawTitleScreen(g);
		} else {
			drawUniverse(g);
		}
	}
	
	void drawUniverse(Graphics g) {
		if (!universe.gameOver) {
			drawPlanets(g);
			drawFleets(g);
			drawPlayers(g);
		} else {
			drawGameOverScreen(g);
			
		}
		
		drawControlInfo(g);
	}

	private void drawControlInfo(Graphics g) {
		g.setColor(Color.yellow);
		g.setFont(new Font("Arial", Font.PLAIN, 12));

		String s = "Press F5 to create new universe!";
		int w = g.getFontMetrics().stringWidth(s);

		g.drawString(s, size.width - w -5, size.height-5);
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
		String playerName = universe.winner.getName();

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
		for (Planet planet : universe.planets) {
			g.setColor(planet.player.getPlayerBackColor());

			int x = (int) getX(g, planet.x);
			int y = (int) getY(g, planet.y);
			int planetSize = planetSize(g, planet.productionRatePerSecond);
			g.fillOval(x - planetSize / 2, y - planetSize / 2, planetSize, planetSize);

			drawStringCentered(g, ((int) planet.forces) + "", x, y, planet.player.getPlayerForeColor());
		}
	}

	private int planetSize(Graphics g, double productionRatePerSecond) {
		return planetSize(g.getClipBounds().width, productionRatePerSecond);
	}

	private int planetSize(int size, double productionRatePerSecond) {
		return (int) (productionRatePerSecond * 7.0 * size / 1000.0);
	}

	private double getX(Graphics g, double x) {
		return ((g.getClipBounds().width - borderSize * 2) * x / universeSize) + borderSize;
	}

	private double getY(Graphics g, double y) {
		return ((g.getClipBounds().height - borderSize * 2) * y / universeSize) + borderSize;
	}

	private void drawStringCentered(Graphics g, String s, int x, int y, Color c) {
		g.setColor(c);
		g.setFont(new Font("Arial", Font.PLAIN, 10));

		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();

		g.drawString(s, x - w / 2, y + h / 2);
	}

	private void drawFleets(Graphics g) {
		for (Fleet fleet : universe.fleets) {
			g.setColor(fleet.player.getPlayerBackColor());

			double x = getX(g, fleet.x);
			double y = getY(g, fleet.y);
			int d = fleet.force;

			if (d > MaxRenderedFleet) {
				d = MaxRenderedFleet;
			}

			if (fleet.getFormation().equals(Formation.ARROW)) {
				double xDiff = fleet.target.x - fleet.x;
				double yDiff = fleet.target.y - fleet.y;

				xDiff *= 2.0;

				double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

				double sinAngle = yDiff / dist;
				double cosAngle = xDiff / dist;

				double length = Math.sqrt(d) * 5;
				for (int i = 0; i < d; i++) {
					double localx = random.nextDouble() * length;
					double localy = random.nextDouble() * localx - localx / 2.0;
					localx -= length / 2;

					double renderx = localx * cosAngle + localy * sinAngle;
					double rendery = -localx * sinAngle + localy * cosAngle;

					renderx = x - renderx;
					rendery = y + rendery;

					g.fillOval((int) renderx, (int) rendery, 3, 3);
				}
			} else {
				for (int i = 0; i < fleet.force; i++) {
					double r = random.nextDouble() * Math.sqrt(d) * 5;
					double v = random.nextDouble() * r;
					double localX = r * Math.cos(v);
					double localY = r * Math.sin(v);

					g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 3, 3);
				}
			}

			// g.fillRect(x - d / 2, y - d / 2, d, d);

			if (d == MaxRenderedFleet) {
				drawStringCentered(g, fleet.getForce() + "", (int) x, (int) y, fleet.player.getPlayerForeColor());
			}
		}
	}

	private void drawPlayers(Graphics g) {
		for (int i = 0; i < universe.players.size(); i++) {
			Player player = universe.players.get(i);

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
		for (Planet planet : universe.PlanetsOfPlayer(player))
			groundForces += planet.forces;

		result += " P[" + universe.PlanetsOfPlayer(player).size() + "/" + ((int) groundForces) + "]";

		int spaceForces = 0;
		for (Fleet fleet : universe.FleetsOfPlayer(NonePlayer.NonePlayer, player))
			spaceForces += fleet.force;

		result += " F[" + universe.FleetsOfPlayer(NonePlayer.NonePlayer, player).size() + "/" + spaceForces + "]";

		return result;
	}
}
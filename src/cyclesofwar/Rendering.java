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
	class WinRecordsTag {

		int x;
		int y;
		int w;
		int h;
		List<FightRecord> winRecords;

		WinRecordsTag(int x, int y, int w, int h, List<FightRecord> winRecords) {
			super();
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.winRecords = winRecords;
		}

		public boolean intersects(int x, int y) {
			return x > this.x && x < this.x + this.w && y > this.y && y < this.y + this.h;
		}
	}

	final int StarCount = 1000;
	final int MaxRenderedFleet = 20;

	interface DrawContentProvider {
		String getString(FightChronics fightChronics, int i);
	}

	enum HAlign {
		LEFT, CENTER, RIGHT
	}

	enum VAlign {
		TOP, CENTER, BOTTOM
	}

	Dimension size = new Dimension(800, 480);
	double universeSize = 0.0;
	int borderSize = 20;

	List<Star> stars = new ArrayList<Star>();

	Universe universe;

	Random random = new Random();
	List<WinRecordsTag> tags = new ArrayList<WinRecordsTag>();

	Rendering() {
		stars.clear();
		for (int i = 0; i < StarCount; i++) {
			stars.add(new Star(random));
		}
	}

	public void setSize(Dimension size) {
		if (!this.size.equals(size)) {
			this.size = size;
			this.borderSize = planetSize(size.width, 5.0) / 2;
		}
	}

	void drawUniverse(Graphics g, Universe universe) {
		this.universe = universe;

		if (this.universeSize != universe.size) {
			this.universeSize = universe.size;
		}

		drawUniverse(g);
	}

	private void drawBackground(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, size.width, size.height);

		drawStars(g);
	}

	void drawUniverse(Graphics g) {
		drawBackground(g);

		if (!universe.gameOver) {
			drawPlanets(g);
			drawFleets(g);
			drawPlayers(g);
		} else {
			drawGameOverScreen(g);
		}
	}

	void drawSeed(Graphics g) {
		drawText(g, size.width - 5, 5, "seed: " + universe.seed, Color.yellow, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
	}

	void drawControlInfo(Graphics g, String s) {
		drawText(g, size.width - 5, size.height - 5, s, Color.yellow, null, HAlign.RIGHT, VAlign.TOP, 12);
	}

	void drawTitleScreen(Graphics g) {
		drawBackground(g);
		drawText(g, size.width / 2, size.height / 2, "Cycles of War", Color.yellow, null, HAlign.CENTER, VAlign.CENTER, new Font(
				"Courier New", Font.BOLD, 48));
	}

	private void drawGameOverScreen(Graphics g) {
		String playerName = universe.winner.getName();

		drawText(g, size.width / 2, size.height / 3 + 20, "GAME OVER", Color.yellow, null, HAlign.CENTER, VAlign.CENTER, new Font(
				"Courier New", Font.BOLD, 48));

		drawText(g, size.width / 2, size.height / 3 + 100, playerName + " has won!", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
				new Font("Courier New", Font.PLAIN, 32));
	}

	private void drawStars(Graphics g) {
		for (Star star : stars) {
			g.setColor(star.c);

			int starSize = (int) (star.d * 4 + 1);
			int x = (int) (size.width * star.x);
			int y = (int) (size.height * star.y);
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

			drawText(g, x, y, ((int) planet.forces) + "", planet.player.getPlayerForeColor(), null, HAlign.CENTER, VAlign.CENTER, 10);
		}
	}

	private int planetSize(Graphics g, double productionRatePerSecond) {
		return planetSize(size.width, productionRatePerSecond);
	}

	private int planetSize(int size, double productionRatePerSecond) {
		return (int) (productionRatePerSecond * 7.0 * size / 1000.0);
	}

	private double getX(Graphics g, double x) {
		return ((size.width - borderSize * 2) * x / universeSize) + borderSize;
	}

	private double getY(Graphics g, double y) {
		return ((size.height - borderSize * 2) * y / universeSize) + borderSize;
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
				drawText(g, (int) x, (int) y, fleet.getForce() + "", fleet.player.getPlayerForeColor(), null, HAlign.CENTER, VAlign.CENTER,
						10);
			}
		}
	}

	private void drawPlayers(Graphics g) {
		for (int i = 0; i < universe.players.size(); i++) {
			Player player = universe.players.get(i);

			drawText(g, 5, i * 20 + 5, shortInfo(player), player.getPlayerForeColor(), player.getPlayerBackColor(), HAlign.LEFT,
					VAlign.CENTER, 12);
		}
	}

	private String shortInfo(Player player) {
		String result = "";

		result += player.getName();
		result += " P[" + universe.PlanetsOfPlayer(player).size() + "/" + ((int) player.getGroundForce()) + "]";
		result += " F[" + universe.FleetsOfPlayer(NonePlayer.NonePlayer, player).size() + "/" + player.getSpaceForce() + "]";

		return result;
	}

	void drawStatistics(Graphics g, FightChronics fightChronics) {
		drawBackground(g);

		Font f = new Font("Courier New", Font.PLAIN, 14);

		int marginLeft = 50;
		int padding = 10;
		int marginTop = 100;

		fightChronics = fightChronics.lightWeightClone();

		drawText(g, marginLeft, 30, fightChronics.gamesToPlayCount + " games left, " + fightChronics.gamesPlayedCount + " games played ("
				+ Arena.matchesPerPairing + " matches per pairing)", Color.white, Color.black, HAlign.LEFT, VAlign.BOTTOM, f);

		drawLines(g, fightChronics, f, marginLeft, marginTop);

		int pos = drawRank(g, fightChronics, f, marginLeft, marginTop);
		pos = drawWins(g, fightChronics, f, pos + padding, marginTop);
		pos = drawPerformance(g, fightChronics, f, pos + padding, marginTop);
		pos = drawNames(g, fightChronics, f, pos + padding, marginTop);

		tags.clear();
		for (int i = 0; i < fightChronics.combatants.size(); i++) {
			pos = drawPerformanceAgainst(g, fightChronics, i, f, pos + padding, marginTop);
		}

		for (int i = 0; i < fightChronics.rankings.size(); i++) {
			Player player = fightChronics.rankings.get(i).player;
			if (fightChronics.hasPriority(player)) {
				g.setColor(Color.green);
				g.drawRect(marginLeft - 2, marginTop + 20 * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight());
				g.setColor(Color.black);
				g.drawRect(marginLeft - 1, marginTop + 20 * (i + 1) + 5, size.width - marginLeft * 2 + 3 - 2, g.getFontMetrics(f)
						.getHeight() - 2);
			}
		}
	}

	private String percentage(double value) {
		return (int) (value * 100) + "%";
	}

	private void drawLines(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop) {
		for (int i = 0; i < fightChronics.rankings.size(); i++) {
			Player player = fightChronics.rankings.get(i).player;
			g.setColor(player.getPlayerBackColor());
			g.fillRect(marginLeft - 2, marginTop + 20 * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight());
		}
	}

	private int drawRank(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop) {
		return drawContent(g, fightChronics, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(FightChronics fightChronics, int i) {
				return (i + 1) + ".";
			}
		});
	}

	private int drawWins(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop) {
		return drawContent(g, fightChronics, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(FightChronics fightChronics, int i) {
				return fightChronics.rankings.get(i).wins + "/" + fightChronics.rankings.get(i).games;
			}
		});
	}

	private int drawPerformance(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop) {
		return drawContent(g, fightChronics, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(FightChronics fightChronics, int i) {
				return percentage(fightChronics.rankings.get(i).getRatio());
			}
		});
	}

	private int drawNames(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop) {
		return drawContent(g, fightChronics, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(FightChronics fightChronics, int i) {
				return fightChronics.rankings.get(i).player.getName();
			}
		});
	}

	private int drawPerformanceAgainst(Graphics g, FightChronics fightChronics, int rank, Font f, int marginLeft, int marginTop) {
		Player competitor = fightChronics.rankings.get(rank).player;
		int maxPos = drawText(g, marginLeft, marginTop, (rank + 1) + ".", competitor.getPlayerForeColor(), competitor.getPlayerBackColor(),
				f);

		for (int i = 0; i < fightChronics.rankings.size(); i++) {
			Player player = fightChronics.rankings.get(i).player;

			String s;
			List<FightRecord> winRecords = null;
			if (!player.isEqualTo(competitor)) {
				winRecords = fightChronics.winsOver(player, competitor);
				s = percentage(winRecords.size() / (double) fightChronics.fightsAgainst(player, competitor).size());
			} else {
				s = "--";
			}

			int pos = drawText(g, marginLeft, marginTop + 20 * (i + 1), s, player.getPlayerForeColor(), null, f);
			if (winRecords != null) {
				remember(marginLeft - 2, marginTop + 20 * (i + 1) + 4, pos - marginLeft + 3, g.getFontMetrics(f).getHeight(), winRecords);
			}

			if (pos > maxPos) {
				maxPos = pos;
			}
		}
		return maxPos;
	}

	private void remember(int x, int y, int w, int h, List<FightRecord> winRecords) {
		tags.add(new WinRecordsTag(x, y, w, h, winRecords));
	}

	private int drawContent(Graphics g, FightChronics fightChronics, Font f, int marginLeft, int marginTop,
			DrawContentProvider drawContentProvider) {
		int maxPos = 0;
		for (int i = 0; i < fightChronics.rankings.size(); i++) {
			Player player = fightChronics.rankings.get(i).player;
			int pos = drawText(g, marginLeft, marginTop + 20 * (i + 1), drawContentProvider.getString(fightChronics, i),
					player.getPlayerForeColor(), null, f);
			if (pos > maxPos) {
				maxPos = pos;
			}
		}
		return maxPos;
	}

	private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, Font font) {
		return drawText(g, x, y, s, fc, bc, HAlign.LEFT, VAlign.BOTTOM, font);
	}

	private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, int fontSize) {
		return drawText(g, x, y, s, fc, bc, hAlgin, vAlign, new Font("Arial", Font.PLAIN, fontSize));
	}

	private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, Font font) {
		g.setFont(font);

		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();

		if (hAlgin == HAlign.CENTER) {
			x -= w / 2;
		} else if (hAlgin == HAlign.RIGHT) {
			x -= w;
		}

		if (vAlign == VAlign.CENTER) {
			y -= h / 2;
		} else if (vAlign == VAlign.TOP) {
			y -= h;
		}

		if (bc != null) {
			g.setColor(bc);
			g.fillRect(x - 2, y + 4, w + 3, h);
		}

		g.setColor(fc);
		g.drawString(s, x, y + h);

		return x + w;
	}

	List<FightRecord> getFightRecords(int x, int y) {
		for (WinRecordsTag winRecordTag : this.tags) {
			if (winRecordTag.intersects(x, y)) {
				return winRecordTag.winRecords;
			}
		}

		return null;
	}

	public Player getPlayer(int x, int y, FightChronics fightChronics) {
		if (x < 50 || x > size.width - 50) {
			return null;
		}

		int index = (y - 100) / 20 - 1;
		if (index >= 0 && index < fightChronics.lightWeightClone().rankings.size()) {
			return fightChronics.lightWeightClone().rankings.get(index).player;
		} else {
			return null;
		}
	}
}
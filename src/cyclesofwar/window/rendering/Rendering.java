package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.tournament.Tournament;
import cyclesofwar.tournament.TournamentRecord;

public class Rendering {

	final int StarCount = 1000;
	final int MaxRenderedFleet = 20;

	interface DrawContentProvider {
		String getString(Tournament tournament, int i);
	}

	enum HAlign {
		LEFT, CENTER, RIGHT
	}

	enum VAlign {
		TOP, CENTER, BOTTOM
	}

	private Dimension size = new Dimension(800, 480);
	private double universeSize = 0.0;
	private int borderSize = 20;

	private List<Star> stars = new ArrayList<Star>();

	private Random random = new Random();
	private List<Tag> tags = new ArrayList<Tag>();

	public Rendering() {
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

	public Font getFont(int style, int fontSize) {
		return new Font("Courier New", style, (int) (fontSize * 0.5 + fontSize * 0.5 * size.width / 1000.0));
	}

	public void drawUniverse(Graphics g, Universe universe) {
		if (this.universeSize != universe.getSize()) {
			this.universeSize = universe.getSize();
		}

		drawBackground(g);

		if (!universe.isGameOver()) {
			drawPlanets(g, universe.getAllPlanets());
			drawFleets(g, universe.getAllFleets(), universe.getNow());
			drawPlayers(g, universe.getPlayers());
		} else {
			drawGameOverScreen(g, universe.getWinner().getName());
		}
	}

	private void drawBackground(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, size.width, size.height);

		drawStars(g);
	}

	public void drawSeed(Graphics g, long seed) {
		drawText(g, size.width - 5, 5, "seed: " + seed, Color.yellow, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
	}

	public void drawControlInfo(Graphics g, String s) {
		drawText(g, size.width - 5, size.height - 5, s, Color.yellow, null, HAlign.RIGHT, VAlign.TOP, 12);
	}

	public void drawInfo(Graphics g, String s) {
		drawText(g, size.width / 2, size.height / 2, s, Color.yellow, null, HAlign.CENTER, VAlign.CENTER, getFont(Font.BOLD, 32));
	}

	public void drawTitleScreen(Graphics g) {
		drawBackground(g);
		drawText(g, size.width / 2, size.height / 2, "Cycles of War", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
				getFont(Font.BOLD, 48));
	}

	private void drawGameOverScreen(Graphics g, String winnerName) {
		drawText(g, size.width / 2, size.height / 3 + 20, "GAME OVER", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
				getFont(Font.BOLD, 48));

		drawText(g, size.width / 2, size.height / 3 + 100, winnerName + " has won!", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
				getFont(Font.PLAIN, 32));
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

	private void drawPlanets(Graphics g, List<Planet> planets) {
		for (Planet planet : planets) {
			g.setColor(planet.getPlayer().getPlayerBackColor());

			int x = (int) getX(g, planet.getX());
			int y = (int) getY(g, planet.getY());
			int planetSize = planetSize(g, planet.getProductionRatePerSecond());
			g.fillOval(x - planetSize / 2, y - planetSize / 2, planetSize, planetSize);

			drawText(g, x, y, ((int) planet.getForces()) + "", planet.getPlayer().getPlayerForeColor(), null, HAlign.CENTER, VAlign.CENTER,
					10);
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

	private void drawFleets(Graphics g, List<Fleet> fleets, double time) {
		for (Fleet fleet : fleets) {
			g.setColor(fleet.getPlayer().getPlayerBackColor());

			double x = getX(g, fleet.getX());
			double y = getY(g, fleet.getY());
			int d = fleet.getForce();

			if (d > MaxRenderedFleet) {
				d = MaxRenderedFleet;
			}

			double localTime = time - fleet.getTimeToTarget();

			if (fleet.getFormation() == Fleet.Formation.ARROW) {
				drawArrowFormation(g, fleet, x, y, d, true, localTime);
			} else if (fleet.getFormation() == Fleet.Formation.V) {
				drawArrowFormation(g, fleet, x, y, d, false, localTime);
			}
			if (fleet.getFormation() == Fleet.Formation.SWARM) {
				drawSwarmFormation(g, fleet, x, y, d, true, localTime);
			} else if (fleet.getFormation() == Fleet.Formation.O) {
				drawSwarmFormation(g, fleet, x, y, d, false, localTime);
			} else if (fleet.getFormation() == Fleet.Formation.EYE) {
				drawSwarmFormation(g, fleet, x, y, d, false, localTime);
				if (d < MaxRenderedFleet) {
					g.setColor(fleet.getPlayer().getPlayerForeColor());
					g.fillOval((int) x - 1, (int) y - 1, 5, 5);
				}
			} else if (fleet.getFormation() == Fleet.Formation.SPIRAL) {
				drawSpiralFormation(g, fleet, x, y, d, localTime);
			}

			if (d == MaxRenderedFleet) {
				drawText(g, (int) x, (int) y, fleet.getForce() + "", fleet.getPlayer().getPlayerForeColor(), null, HAlign.CENTER,
						VAlign.CENTER, 10);
			}
		}
	}

	private void drawArrowFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
		double xDiff = fleet.getTarget().getX() - fleet.getX();
		double yDiff = fleet.getTarget().getY() - fleet.getY();

		xDiff *= 2.0;

		double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

		double sinAngle = yDiff / dist;
		double cosAngle = xDiff / dist;

		double length = Math.sqrt(d) * 5;
		for (int i = 0; i < d; i++) {
			double localx = random.nextDouble() * length;
			double localy;
			if (filled) {
				localy = random.nextDouble() * localx - localx / 2.0;
			} else {
				localy = (random.nextDouble() * 0.2 + 0.8) * localx - localx / 2.0;
				localy *= random.nextDouble() > 0.5 ? 1.0 : -1.0;
			}
			localy = localy * Math.sin(time);
			localx -= length / 2;

			double renderx = localx * cosAngle + localy * sinAngle;
			double rendery = -localx * sinAngle + localy * cosAngle;

			renderx = x - renderx;
			rendery = y + rendery;

			g.fillOval((int) renderx, (int) rendery, 3, 3);
		}
	}

	private void drawSwarmFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
		for (int i = 0; i < d; i++) {
			double r;
			if (filled) {
				r = random.nextDouble() * Math.sqrt(d) * 5;
			} else {
				r = (random.nextDouble() * 0.1 + 0.9) * Math.sqrt(d) * 5;
			}
			r = r * 0.5 + r * Math.sin(time);
			double v = random.nextDouble() * r;
			double localX = r * Math.cos(v);
			double localY = r * Math.sin(v);

			g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 3, 3);
		}
	}

	private void drawSpiralFormation(Graphics g, Fleet fleet, double x, double y, int d, double time) {
		for (int i = 0; i < d; i++) {
			double rnd = random.nextDouble();
			double r = rnd * Math.sqrt(d) * 7;
			double v = rnd * -10;

			double localX = r * Math.cos(v);
			double localY = -r * Math.sin(v);

			g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 2, 2);
		}
	}

	private void drawPlayers(Graphics g, List<Player> players) {
		int h = g.getFontMetrics().getHeight();

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);

			drawText(g, 5, i * (h + 4) + 5, shortInfo(player), player.getPlayerForeColor(), player.getPlayerBackColor(), HAlign.LEFT,
					VAlign.CENTER, 12);
		}
	}

	private String shortInfo(Player player) {
		String result = "";

		result += player.getName();
		result += " P[" + player.getPlanets().size() + "/" + ((int) player.getGroundForce()) + "]";
		result += " F[" + player.getFleets().size() + "/" + player.getSpaceForce() + "]";

		return result;
	}

	public void drawPlayerSelection(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers,
			List<Integer> possibleNumbersOfRounds, Integer selectedNumberOfRounds, List<Integer> possiblePlanetsPerPlayer,
			Integer selectedNumberOfPlanetsPerPlayer, List<Double> possibleUniverseSizes, double selectedUniverseSize) {
		drawBackground(g);

		int marginLeft = 60;
		int marginTop = 40;

		tags.clear();

		Font f = getFont(Font.BOLD, 18);
		drawText(g, marginLeft, marginTop - 12, "chose champions:", Color.yellow, null, f);

		drawPlayers(g, selectedPlayers, allPlayers, marginLeft, marginTop);

		int left = 0;
		left = Math.max(
				left,
				drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 190, "chose number of matches:",
						Color.yellow, null, f));
		left = Math.max(
				left,
				drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 150,
						"chose number of planets per player:", Color.yellow, null, f));
		left = Math.max(
				left,
				drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 110, "chose universe size factor:",
						Color.yellow, null, f));

		drawSelection(g, "rounds", possibleNumbersOfRounds, selectedNumberOfRounds, marginLeft, marginTop + 190, left);
		drawSelection(g, "planets", possiblePlanetsPerPlayer, selectedNumberOfPlanetsPerPlayer, marginLeft, marginTop + 150, left);
		drawSelection(g, "sizefactors", possibleUniverseSizes, selectedUniverseSize, marginLeft, marginTop + 110, left);

		drawText(g, marginLeft, size.height - marginTop - 80, "chose game mode:", Color.yellow, null, f);

		drawButton(g, "Live Mode", "Live Mode", 15 + marginLeft, size.height - marginTop, HAlign.LEFT, 22, 20, Color.yellow);
		drawButton(g, "Tournament Mode", "Tournament Mode", size.width / 2, size.height - marginTop, HAlign.CENTER, 22, 20, Color.yellow);
		drawButton(g, "Arena Mode", "Arena Mode", 15 + marginLeft, size.height - marginTop, HAlign.RIGHT, 22, 20, Color.yellow);
	}

	private <T> void drawSelection(Graphics g, String id, List<T> possibleValues, T selectedValue, int marginLeft, int marginTop, int left) {
		int borderSize = (int) (size.width * 7.0 / 1000);
		int w = size.width - marginLeft * 3 - left;
		int tile = (int) (w / (double) (possibleValues.size() - 1));
		for (int i = 0; i < possibleValues.size(); i++) {
			T numberAtI = possibleValues.get(i);
			if (numberAtI.equals(selectedValue)) {
				drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
						borderSize, Color.yellow);
			} else {
				drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
						borderSize, Color.white);
			}
		}
	}

	private void drawPlayers(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers, int marginLeft, int marginTop) {
		Font f = getFont(Font.PLAIN, 16);

		int h = g.getFontMetrics(f).getHeight();
		int posX = marginLeft;
		int posY = marginTop + 24;
		for (Player player : allPlayers) {
			int w = g.getFontMetrics(f).stringWidth(player.getName());
			if (posX + w > size.width - marginLeft) {
				posX = marginLeft;
				posY += h + 6;
			}
			drawText(g, posX, posY, player.getName(), player.getPlayerForeColor(), player.getPlayerBackColor(), f);
			remember(posX, posY, w, h, player);
			if (selectedPlayers.contains(player)) {
				drawBorder(g, posX, posY, w, h);
			}
			posX += w + 12;
		}
	}

	private void drawButton(Graphics g, String id, String caption, int x, int y, HAlign hAlign, int fontSize, int buttonBorderSize, Color c) {
		Font f = getFont(Font.BOLD, fontSize);

		int w = g.getFontMetrics(f).stringWidth(caption);
		int h = g.getFontMetrics(f).getHeight();

		if (hAlign == HAlign.RIGHT) {
			x = size.width - w - x;
		} else {
			if (hAlign == HAlign.CENTER) {
				x = x - w / 2;
			}
		}

		float[] hsv = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);

		drawText(g, x, y - h, caption, Color.black, Color.white, f);
		remember(x - buttonBorderSize, y - h - buttonBorderSize, w + buttonBorderSize * 2, h + buttonBorderSize * 2, id);
		for (int i = 1; i < buttonBorderSize; i++) {
			g.setColor(Color.getHSBColor(hsv[0], hsv[1], (float) ((2.0 * buttonBorderSize) / (i + buttonBorderSize))));
			g.drawRect(x - i - 2, y - i + 4 - h, w + 2 * i + 2, h + 2 * i - 1);
		}
	}

	private void drawBorder(Graphics g, int posX, int posY, int w, int h) {
		g.setColor(Color.white);
		g.drawRect(posX - 3 - 1, posY - 3 + 7, w + 6, h + 6 - 6);
		g.setColor(Color.green);
		g.drawRect(posX - 2 - 1, posY - 2 + 7, w + 4, h + 4 - 6);
		g.setColor(Color.black);
		g.drawRect(posX - 1 - 1, posY - 1 + 7, w + 2, h + 2 - 6);
	}

	public void drawStatistics(Graphics g, Tournament tournament, String s) {
		drawBackground(g);

		Font f = getFont(Font.PLAIN, 14);

		int marginLeft = 50;
		int padding = 10;
		int marginTop = 120;

		drawText(g, marginLeft, marginLeft, s, Color.yellow, null, HAlign.LEFT, VAlign.CENTER, getFont(Font.BOLD, 32));

		tournament = tournament.lightWeightClone();

		drawLines(g, tournament, f, marginLeft, marginTop);

		int pos = drawRank(g, tournament, f, marginLeft, marginTop);
		pos = drawWins(g, tournament, f, pos + padding, marginTop);
		pos = drawPerformance(g, tournament, f, pos + padding, marginTop);
		pos = drawNames(g, tournament, f, pos + padding, marginTop);

		tags.clear();
		for (int i = 0; i < tournament.getChampions().size(); i++) {
			pos = drawPerformanceAgainst(g, tournament, i, f, pos + padding, marginTop);
		}

		int h = g.getFontMetrics(f).getHeight();
		for (int i = 0; i < tournament.getRankings().size(); i++) {
			Player player = tournament.getRankings().get(i).getPlayer();
			if (tournament.hasPriority(player)) {
				drawBorder(g, marginLeft, marginTop + (h + 2) * (i + 1), size.width - marginLeft * 2, h);
			}
		}

		drawText(g, size.width - marginLeft, marginTop + (h + 2) * (tournament.getRankings().size() + 1), tournament.getGamesToPlayCount()
				+ " games left, " + tournament.getGamesPlayedCount() + " games played.", Color.white, Color.black, HAlign.RIGHT,
				VAlign.BOTTOM, f);
	}

	private String percentage(double value) {
		return (int) (value * 100) + "%";
	}

	private void drawLines(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop) {
		int h = g.getFontMetrics(f).getHeight();
		for (int i = 0; i < tournament.getRankings().size(); i++) {
			Player player = tournament.getRankings().get(i).getPlayer();
			g.setColor(player.getPlayerBackColor());
			g.fillRect(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight());
		}
	}

	private int drawRank(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop) {
		return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(Tournament tournament, int i) {
				return (i + 1) + ".";
			}
		});
	}

	private int drawWins(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop) {
		return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(Tournament tournament, int i) {
				return tournament.getRankings().get(i).getWins() + "/" + tournament.getRankings().get(i).getGames();
			}
		});
	}

	private int drawPerformance(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop) {
		return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(Tournament tournament, int i) {
				return percentage(tournament.getRankings().get(i).getRatio());
			}
		});
	}

	private int drawNames(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop) {
		return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
			public String getString(Tournament tournament, int i) {
				return tournament.getRankings().get(i).getPlayer().getName();
			}
		});
	}

	private int drawPerformanceAgainst(Graphics g, Tournament tournament, int rank, Font f, int marginLeft, int marginTop) {
		Player competitor = tournament.getRankings().get(rank).getPlayer();
		int maxPos = drawText(g, marginLeft, marginTop, (rank + 1) + ".", competitor.getPlayerForeColor(), competitor.getPlayerBackColor(),
				f);

		int h = g.getFontMetrics(f).getHeight();

		for (int i = 0; i < tournament.getRankings().size(); i++) {
			Player player = tournament.getRankings().get(i).getPlayer();

			String s;
			List<TournamentRecord> winRecords = null;
			if (player != competitor) {
				winRecords = tournament.winsOver(player, competitor);
				s = percentage(winRecords.size() / (double) tournament.fightsAgainst(player, competitor).size());
			} else {
				s = "--";
			}

			int pos = drawText(g, marginLeft, marginTop + (h + 2) * (i + 1), s, player.getPlayerForeColor(), null, f);
			if (winRecords != null) {
				remember(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, pos - marginLeft + 3, g.getFontMetrics(f).getHeight(),
						winRecords);
			}

			if (pos > maxPos) {
				maxPos = pos;
			}
		}
		return maxPos;
	}

	private void remember(int x, int y, int w, int h, Object tag) {
		tags.add(new Tag(x, y, w, h, tag));
	}

	private int drawContent(Graphics g, Tournament tournament, Font f, int marginLeft, int marginTop,
			DrawContentProvider drawContentProvider) {
		int maxPos = 0;
		int h = g.getFontMetrics(f).getHeight();
		for (int i = 0; i < tournament.getRankings().size(); i++) {
			Player player = tournament.getRankings().get(i).getPlayer();
			int pos = drawText(g, marginLeft, marginTop + (h + 2) * (i + 1), drawContentProvider.getString(tournament, i),
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
		return drawText(g, x, y, s, fc, bc, hAlgin, vAlign, getFont(Font.PLAIN, fontSize));
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

	@SuppressWarnings("unchecked")
	public List<TournamentRecord> getFightRecords(int x, int y) {
		for (Tag winRecordTag : this.tags) {
			if (winRecordTag.intersects(x, y)) {
				return (List<TournamentRecord>) winRecordTag.tag;
			}
		}

		return null;
	}

	public Player getPlayer(int x, int y) {
		for (Tag tag : tags) {
			if (tag.intersects(x, y)) {
				if (Player.class.isInstance(tag.tag)) {
					return (Player) tag.tag;
				}
			}
		}

		return null;
	}

	public String getButtonCaption(int x, int y) {
		for (Tag tag : tags) {
			if (tag.intersects(x, y)) {
				if (String.class.isInstance(tag.tag)) {
					return (String) tag.tag;
				}
			}
		}

		return null;
	}

	// TODO use tags instead
	public Player getPlayer(int x, int y, Tournament tournament) {
		if (x < 50 || x > size.width - 50) {
			return null;
		}

		int index = (y - 120) / 20 - 1;
		if (index >= 0 && index < tournament.lightWeightClone().getRankings().size()) {
			return tournament.lightWeightClone().getRankings().get(index).getPlayer();
		} else {
			return null;
		}
	}
}
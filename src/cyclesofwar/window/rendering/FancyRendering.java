package cyclesofwar.window.rendering;

import cyclesofwar.Fleet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.window.HumanReadableLongConverter;
import cyclesofwar.window.rendering.noise.simplex.NebulaNoise;
import cyclesofwar.window.rendering.textures.ColorTools;
import cyclesofwar.window.rendering.textures.PlanetTexture;
import cyclesofwar.window.rendering.textures.Texture;
import cyclesofwar.window.rendering.textures.BackgroundTexture;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FancyRendering extends SimpleRendering {

    protected final Map<Integer, Texture> backgrounds = new TreeMap<>();
    protected final Map<Integer, PlanetTexture> planetTextures = new TreeMap();

    protected Color fancyTextColor = ColorTools.transparent(Color.cyan, 0.8f);
    protected Color fancyInActiveTextColor = ColorTools.transparent(Color.cyan.darker(), 0.5f);

    protected Color fancyActiveBorderColor = ColorTools.transparent(Color.cyan, 0.5f);
    protected Color fancyInActiveBorderColor = ColorTools.transparent(Color.cyan.darker(), 0.3f);

    protected Color fancyActiveBackColor = ColorTools.transparent(Color.blue, 0.5f);
    protected Color fancyInActiveBackColor = ColorTools.transparent(Color.blue.darker(), 0.3f);

    protected final static HumanReadableLongConverter humanReadableLongConverter = new HumanReadableLongConverter();

    protected final Map<Player, List<Double>> historyPlanets = new TreeMap<>();
    protected final Map<Player, List<Double>> historyFleets = new TreeMap<>();
    protected final Map<Player, List<Double>> historyForces = new TreeMap<>();

    protected boolean resetNeeded = false;

    public FancyRendering() {
        this.textColor = fancyTextColor;
    }

    protected Texture getBackground(long universeSeed) {
        Integer index = new Random(universeSeed).nextInt(10);
        if (!backgrounds.containsKey(index)) {
            backgrounds.put(index, createBackground(universeSeed));
        }

        return backgrounds.get(index);
    }

    protected Texture createBackground(long universeSeed) {
        return new BackgroundTexture(new NebulaNoise((int)universeSeed), size.width, size.height, universeSeed, true);
    }

    @Override
    protected void applyNewSize() {
        backgrounds.clear();
        planetTextures.clear();
    }

    @Override
    public Font getFont(int style, int fontSize) {
        return new Font("Impact", style, (int) getScaled(fontSize));
    }

    @Override
    public void drawSeed(Graphics g, long seed) {
        drawText(g, size.width - 5, 5, "humand readable seed: \"" + humanReadableLongConverter.ToString(seed) + "\"", Color.yellow, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
    }

    @Override
    public void drawBackground(Graphics g, long universeSeed) {
        g.drawImage(this.getBackground(universeSeed).getImage(), 0, 0, null);
    }

    @Override
    protected Color getPlayerTextColor(Player player) {
        return fancyTextColor;
    }

    @Override
    public void drawUniverse(Graphics g, Universe universe) {
        super.drawUniverse(g, universe);

        List<Player> players = universe.getPlayers();
        players.add(universe.getNonePlayer());

        if (!universe.isGameOver()) {
            if (resetNeeded) {
                reset();
            }

            drawCharts(g, players);
            updateHistograms(players);
        } else {
            resetNeeded = true;
        }
    }

    @Override
    public void drawPlanets(Graphics g, List<Planet> planets) {
        for (int i = 0; i < planets.size(); i++) {
            Planet planet = planets.get(i);

            updatePlanetTexture(i, planet);

            PlanetTexture planetTexture = planetTextures.get(i);

            final int x = (int) getX(g, planet.getX());
            final int y = (int) getY(g, planet.getY());
            final int uX = x - (planetTexture.getWidth() >> 1);
            final int uY = y - (planetTexture.getHeight() >> 1);

            g.drawImage(planetTexture.getImage(), uX, uY, null);
            drawText(g, x, y, ((int) planet.getForces()) + "", Color.WHITE, null, HAlign.CENTER, VAlign.CENTER, 10);
        }
    }

	protected void updatePlanetTexture(int i, Planet planet) {
		if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
		    final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
		    planetTextures.put(i, new PlanetTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
		}
	}

    public void drawPlayerNames(Graphics g, List<Player> players) {
        int left = borderSize;
        for (Player player : players) {
            Color bg = player.isAlive() ? player.getPlayerBackColor() : Color.DARK_GRAY;
            bg = ColorTools.transparent(bg, 0.75f);

            Color fg = player.isAlive() ? fancyTextColor : fancyInActiveTextColor;

            Font f = getFont(Font.PLAIN, 12);
            int w = g.getFontMetrics(f).stringWidth(player.getName());
            int h = g.getFontMetrics(f).getHeight();

            g.setColor(bg);
            g.fillPolygon(
                    new int[]{left, left + h, left + w + h, left + w + 2 * h},
                    new int[]{0, h + 5, h + 5, 0},
                    4);

            left = h + drawText(g, left + h, 5, player.getName(), fg, null, HAlign.LEFT, VAlign.CENTER, 12);
        }
    }

    public void drawFancyPlayerNames(Graphics g, List<Player> players) {
        int left = this.borderSize;
        Graphics2D g2 = (Graphics2D) g;

        Font f = getFont(Font.BOLD, 12);
        int h = g2.getFontMetrics(f).getHeight();

        int borderSize = h / 4;

        for (Player player : players) {
            int w = g2.getFontMetrics(f).stringWidth(player.getName()) + h * 2;

            Color c = player.isAlive() ? fancyActiveBackColor : fancyInActiveBackColor;
            g2.setColor(c);
            g2.fillPolygon(
                    new int[]{left + borderSize / 2, left + h + borderSize / 2, left + w + h - borderSize / 2, left + w + 2 * h - borderSize / 2},
                    new int[]{borderSize / 2, h + 10 - borderSize / 2, h + 10 - borderSize / 2, borderSize / 2},
                    4);

            c = player.isAlive() ? fancyActiveBorderColor : fancyInActiveBorderColor;
            g2.setColor(c);
            g2.setStroke(new BasicStroke(borderSize));
            g2.drawPolygon(
                    new int[]{left, left + h, left + w + h, left + w + 2 * h},
                    new int[]{0, h + 10, h + 10, 0},
                    4);

            int r = (int) (h * 0.7);
            g2.setColor(player.getPlayerBackColor());
            //g2.fillOval((int) (left + h * 1.3), 7, r, r);
            g2.fillRoundRect((int) (left + h * 1.3), 7, r, r, 5, 5);

            c = player.isAlive() ? fancyActiveBorderColor : fancyInActiveBorderColor;
            g2.setColor(c);
            g2.setStroke(new BasicStroke(2));
            // g2.drawOval((int) (left + h * 1.3), 7, r, r);
            g2.drawRoundRect((int) (left + h * 1.3), 7, r, r, 5, 5);

            c = player.isAlive() ? fancyTextColor : fancyInActiveTextColor;
            left = (int) (h * 1.75 + drawText(g2, (int) (left + h * 2.5), 10, player.getName(), c, null, HAlign.LEFT, VAlign.CENTER, f));
        }

        g2.setStroke(new BasicStroke(1));
    }

    @Override
    public void drawPlayers(Graphics g, List<Player> players) {
        // drawPlayerNames(g, players)
        drawFancyPlayerNames(g, players);
    }

    public void drawCharts(Graphics g, List<Player> players) {
        int size = (int) getScaled(60);
        double start = 0.95;
        double step = 1.25;

        drawPieChart(g, "Planets", 12, this.size.width - size / 2 - 10, (int) ((start + step * 0) * size), size, planetData(players));
        drawPieChart(g, "Fleets", 12, this.size.width - size / 2 - 10, (int) ((start + step * 1) * size), size, fleetData(players));
        drawPieChart(g, "Forces", 12, this.size.width - size / 2 - 10, (int) ((start + step * 2) * size), size, forceFullData(players));

        start += 4 * step;
        size = (int) getScaled(40);
        start += step * getScaled(40) / getScaled(60);
        drawPieChart(g, "Ground", 10, this.size.width - size / 2 - 10, (int) ((start + step * 0) * size), size, forcePlanetData(players));
        drawPieChart(g, "Space", 10, this.size.width - size / 2 - 10, (int) ((start + step * 1) * size), size, forceFleetData(players));
    }

    protected void drawPieChart(Graphics g, String text, int textSize, int x, int y, int size, List<ValueAndColor> valuesAndColors) {
        Graphics2D g2 = (Graphics2D) g;

        int sum = 0;
        for (ValueAndColor valueAndColor : valuesAndColors) {
            sum += valueAndColor.value;
        }

        x -= size / 2;
        y -= size / 2;

        double angle = 90;
        for (ValueAndColor valueAndColor : valuesAndColors) {
            g2.setColor(ColorTools.transparent(valueAndColor.color, 0.5f));
            double newAngle = valueAndColor.value * 360.0 / sum;
            g2.fillArc(x, y, size, size, (int) angle, (int) newAngle);
            angle += newAngle;
        }

        g2.setColor(fancyActiveBorderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, size, size);

        drawText(g2, x + size / 2, y + size / 2, text, fancyTextColor, null, HAlign.CENTER, VAlign.CENTER, getFont(Font.BOLD, textSize));
        g2.setStroke(new BasicStroke(1));
    }

    @Override
    protected void drawBigButton(Graphics g, String id, String caption, int x, int y, HAlign hAlign, int fontSize, int buttonBorderSize, Color c) {
        int marginLeft = 60;
        int wSteps = (size.width - marginLeft * 2) / 5;
        int w = (int) (wSteps * 0.7);
        int h = 60;
        buttonBorderSize = 5;

        Font f = getFont(Font.BOLD, fontSize);

        if (hAlign == HAlign.RIGHT) {
            x = size.width - w - x;
        } else {
            if (hAlign == HAlign.CENTER) {
                x = x - w / 2;
            }
        }

        g.setColor(fancyActiveBackColor);
        g.fillRect(x, y - h, w, h);

        g.setColor(fancyActiveBorderColor);
        g.fillRect(x, y - h, w, buttonBorderSize);
        g.fillRect(x, y - buttonBorderSize, w, buttonBorderSize);
        g.fillRect(x, y - h + buttonBorderSize, buttonBorderSize, h - buttonBorderSize * 2);
        g.fillRect(x + w - buttonBorderSize, y - h + buttonBorderSize, buttonBorderSize, h - buttonBorderSize * 2);

        g.fillPolygon(
                new int[]{x + buttonBorderSize, x + buttonBorderSize + buttonBorderSize * 4 + buttonBorderSize * 3, x + buttonBorderSize + buttonBorderSize * 4, x + buttonBorderSize},
                new int[]{y - h + buttonBorderSize, y - h + buttonBorderSize, y - h + buttonBorderSize + buttonBorderSize * 2, y - h + buttonBorderSize + buttonBorderSize * 2}, 4);

        g.fillPolygon(
                new int[]{x + w - buttonBorderSize, x + w - buttonBorderSize - buttonBorderSize * 4 - buttonBorderSize * 3, x + w - buttonBorderSize - buttonBorderSize * 4, x + w - buttonBorderSize},
                new int[]{y - buttonBorderSize, y - buttonBorderSize, y - buttonBorderSize - buttonBorderSize * 2, y - buttonBorderSize - buttonBorderSize * 2}, 4);

        drawText(g, x + w / 2, y - h / 2, caption, fancyTextColor, null, HAlign.CENTER, VAlign.CENTER, f);

        remember(x, y - h, w, h, id);
    }

    @Override
    protected int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, Color sc, HAlign hAlgin, VAlign vAlign, Font font) {
        return super.drawText(g, x, y, s, fc, bc, Color.BLACK, hAlgin, vAlign, font);
    }

    @Override
    protected void drawGameOverScreen(Graphics g, String winnerName) {
        drawHistograms(g, winnerName);
    }

    protected void drawHistograms(Graphics g, String winnerName) {
        int w = (size.width - borderSize * 3) / 2;
        int h = (size.height - borderSize * 3) / 2;
        int r = (borderSize + size.width) / 2;
        int b = (borderSize + size.height) / 2;

        drawText(g, borderSize + w / 2, borderSize + h / 2, winnerName + " has won!", fancyTextColor, null, HAlign.CENTER, VAlign.CENTER, 28);

        drawHistogram(g, "Forces", r, borderSize, w, h, historyForces);
        drawHistogram(g, "Planets", borderSize, b, w, h, historyPlanets);
        drawHistogram(g, "Fleets", r, b, w, h, historyFleets);
    }

    protected void drawHistogram(Graphics g, String s, int x, int y, int w, int h, Map<Player, List<Double>> history) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(ColorTools.transparent(Color.BLACK, 0.7));
        g2.fillRect(x, y, w, h);

        double maxX = history.get((Player) history.keySet().toArray()[0]).size();
        double maxY = maxInHistogram(history);

        //g2.setStroke(new BasicStroke(2));
        for (Player player : history.keySet()) {
            g2.setColor(player.getPlayerBackColor());

            for (int i = 1; i < history.get(player).size(); i++) {

                double pX1 = (i - 1) * w / maxX;
                double pY1 = history.get(player).get(i - 1) * h / maxY;

                double pX2 = i * w / maxX;
                double pY2 = history.get(player).get(i) * h / maxY;

                g2.drawLine((int) pX1 + x, y + h - (int) pY1, (int) pX2 + x, y + h - (int) pY2);
            }
        }

        drawText(g2, x + w - 5, y + h - 5, s, fancyTextColor, null, HAlign.RIGHT, VAlign.TOP, 18);
    }

    protected void updateHistograms(List<Player> players) {
        for (Player player : players) {
            updateHistogram(historyPlanets, player, player.getPlanets().size());
            updateHistogram(historyFleets, player, player.getFleets().size());
            updateHistogram(historyForces, player, player.getFullForce());
        }
    }

    protected void updateHistogram(Map<Player, List<Double>> history, Player player, double value) {
        if (!history.containsKey(player)) {
            history.put(player, new ArrayList<Double>());
        }

        history.get(player).add(value);
    }

    protected double maxInHistogram(Map<Player, List<Double>> history) {
        double result = Double.MIN_VALUE;
        for (Player player : history.keySet()) {
            result = Math.max(result, Collections.max(history.get(player)));
        }
        return result;
    }

    protected void reset() {
        resetNeeded = false;

        historyPlanets.clear();
        historyFleets.clear();
        historyForces.clear();
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {
        
        final int base = 10;
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));    
        
        for (Fleet fleet : fleets) {
            
            double x = getX(g, fleet.getX());
            double y = getY(g, fleet.getY());

            // double localTime = time - fleet.getTimeToTarget();

            double xDiff = fleet.getTarget().getX() - fleet.getX();
            double yDiff = fleet.getTarget().getY() - fleet.getY();

            xDiff *= 2.0;

            double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

            double sinAngle = yDiff / dist;
            double cosAngle = xDiff / dist;

            int forces = fleet.getForce();
            int ring = 0;
            while(forces > 0) {
                
                int currentForces = forces % base;
                forces /= base;
                ring++;
                
                double length = ring * ring * 4;

                int[] one = getXY(length / 2, -length / 2, x, y, sinAngle, cosAngle);
                int[] two = getXY(-length / 2, 0, x, y, sinAngle, cosAngle);
                int[] three = getXY(length / 2, length / 2, x, y, sinAngle, cosAngle);

                g.setColor(ColorTools.transparent(fleet.getPlayer().getPlayerBackColor(), 0.4 + 0.6 * ((double)currentForces)/base));
                g.drawPolygon(new int[]{one[0], two[0], three[0]}, new int[]{one[1], two[1], three[1]}, 3);
            }
            
            //drawText(g2, (int) x, (int) y, fleet.getForce() + "", getPlayerTextColor(fleet.getPlayer()), null, HAlign.CENTER, VAlign.CENTER, 10);
        }
        
        g2.setStroke(new BasicStroke(1));
    }

    protected int[] getXY(double localx, double localy, double x, double y, double sinAngle, double cosAngle) {
        return new int[]{(int) (x - (localx * cosAngle + localy * sinAngle)), (int) (y + (-localx * sinAngle + localy * cosAngle))};
    }
}

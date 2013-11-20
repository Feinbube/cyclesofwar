package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.window.HumanReadableLongConverter;
import cyclesofwar.window.rendering.textures.ColorTools;
import cyclesofwar.window.rendering.textures.PlanetTexture;
import cyclesofwar.window.rendering.textures.UniverseTexture;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Map;
import java.util.TreeMap;

public class FancyRendering extends SimpleRendering {

    private final Map<Integer, UniverseTexture> backgrounds = new TreeMap<>();
    private final Map<Integer, PlanetTexture> planetTextures = new TreeMap();

    private final Color fancyTextColor = ColorTools.transparent(Color.cyan, 0.8f);
    private final Color fancyInActiveTextColor = ColorTools.transparent(Color.cyan.darker(), 0.5f);

    private final Color fancyActiveBorderColor = ColorTools.transparent(Color.cyan, 0.5f);
    private final Color fancyInActiveBorderColor = ColorTools.transparent(Color.cyan.darker(), 0.3f);

    private final Color fancyActiveBackColor = ColorTools.transparent(Color.blue, 0.5f);
    private final Color fancyInActiveBackColor = ColorTools.transparent(Color.blue.darker(), 0.3f);

    private final static HumanReadableLongConverter humanReadableLongConverter = new HumanReadableLongConverter();

    public FancyRendering() {
    }

    private UniverseTexture getBackground(long universeSeed) {
        Integer index = new Random(universeSeed).nextInt(10);
        if (!backgrounds.containsKey(index)) {
            backgrounds.put(index, new UniverseTexture(size.width, size.height, universeSeed));
        }

        return backgrounds.get(index);
    }
    
    @Override
    protected void applyNewSize() {
        backgrounds.clear();
        planetTextures.clear();
    }

    @Override
    public Font getFont(int style, int fontSize) {
        return new Font("Arial", style, (int) getScaled(fontSize));
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
    public void drawPlanets(Graphics g, List<Planet> planets) {
        for (int i = 0; i < planets.size(); i++) {
            Planet planet = planets.get(i);

            if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
                final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
                planetTextures.put(i, new PlanetTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
            }

            PlanetTexture planetTexture = planetTextures.get(i);

            final int x = (int) getX(g, planet.getX());
            final int y = (int) getY(g, planet.getY());
            final int uX = x - (planetTexture.getWidth() >> 1);
            final int uY = y - (planetTexture.getHeight() >> 1);

            g.drawImage(planetTexture.getImage(), uX, uY, null);
            drawText(g, x, y, ((int) planet.getForces()) + "", Color.WHITE, null, HAlign.CENTER, VAlign.CENTER, 10);
        }
    }

    public void drawPlayerNames(Graphics g, List<Player> players) {
        int left = borderSize;
        for (Player player : players) {
            Color bg = player.isAlive() ? player.getPlayerBackColor() : Color.DARK_GRAY;
            bg = ColorTools.transparent(bg, 0.75f);

            Color fg = player.isAlive() ? player.getPlayerForeColor() : Color.GRAY;

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
            int w = g2.getFontMetrics(f).stringWidth(player.getName()) + h*2;

            Color c = player.isAlive() ? fancyActiveBackColor : fancyInActiveBackColor;
            g2.setColor(c);
            g2.fillPolygon(
                    new int[]{left + borderSize/2, left + h + borderSize/2, left + w + h - borderSize/2, left + w + 2 * h - borderSize/2},
                    new int[]{borderSize/2, h + 10-borderSize/2, h + 10-borderSize/2, borderSize/2},
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
            g2.fillOval((int) (left + h * 1.3), 7, r, r);

            c = player.isAlive() ? fancyActiveBorderColor : fancyInActiveBorderColor;
            g2.setColor(c);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval((int) (left + h * 1.3), 7, r, r);

            c = player.isAlive() ? fancyTextColor : fancyInActiveTextColor;
            drawText(g2, (int) (left + h * 2.5)+1, 11, player.getName(), c.darker().darker(), null, HAlign.LEFT, VAlign.CENTER, f);
            left = (int) (h * 1.75 + drawText(g2, (int) (left + h * 2.5), 10, player.getName(), c, null, HAlign.LEFT, VAlign.CENTER, f));
        }
        
        g2.setStroke(new BasicStroke(1));
    }

    @Override
    public void drawPlayers(Graphics g, List<Player> players) {
        // drawPlayerNames(g, players)
        drawFancyPlayerNames(g, players);
        drawCharts(g, players);
    }

    public void drawCharts(Graphics g, List<Player> players) {
        int size = (int) getScaled(60);
        drawPieChart(g, "Planets", this.size.width - size / 2 - 10, (int) (0.75 * size), size, planetData(players));
        drawPieChart(g, "Fleets", this.size.width - size / 2 - 10, (int) (2.0 * size), size, fleetData(players));
        drawPieChart(g, "Forces", this.size.width - size / 2 - 10, (int) (3.25 * size), size, forceData(players));
    }

    private void drawPieChart(Graphics g, String text, int x, int y, int size, List<ValueAndColor> valuesAndColors) {
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

        drawText(g2, x + size / 2+1, y + size / 2+1, text, fancyTextColor.darker().darker(), null, HAlign.CENTER, VAlign.CENTER, 12);
        drawText(g2, x + size / 2, y + size / 2, text, fancyTextColor, null, HAlign.CENTER, VAlign.CENTER, 12);
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
}

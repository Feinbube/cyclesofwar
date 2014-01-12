package cyclesofwar.window.rendering;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Universe;
import cyclesofwar.window.rendering.noise.cell.CellNoise;
import cyclesofwar.window.rendering.textures.BackgroundTexture;
import cyclesofwar.window.rendering.textures.CellTexture;
import cyclesofwar.window.rendering.textures.PlanetTexture;
import cyclesofwar.window.rendering.textures.Texture;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

public class OrganicRendering extends FancyRendering {

    double time;

    @Override
    protected double getX(Graphics g, double x) {
        double w = Math.sin(time / 10.0) * size.width / 12.0 + size.width * 11.0 / 12.0;
        return (size.width - w) / 2 + ((w - borderSize * 2) * x / universeSize) + borderSize;
    }

    @Override
    protected double getY(Graphics g, double y) {
        double h = Math.sin(time / 5.0) * size.height / 20.0 + size.height * 19.0 / 20.0;
        return (size.height - h) / 2 + ((h - borderSize * 2) * y / universeSize) + borderSize;
    }

    @Override
    protected Texture createBackground(long universeSeed) {
        return new BackgroundTexture(new CellNoise(), size.width, size.height, universeSeed, false);
    }

    @Override
    protected void updatePlanetTexture(int i, Planet planet) {
        if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
            final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
            planetTextures.put(i, new CellTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
        }
    }

    @Override
    public void drawPlanet(Graphics g, Planet planet, int id) {
        updatePlanetTexture(id, planet);

        PlanetTexture planetTexture = planetTextures.get(id);

        final int x = (int) getX(g, planet.getX());
        final int y = (int) getY(g, planet.getY());
        final int uX = x - (planetTexture.getWidth() >> 1);
        final int uY = y - (planetTexture.getHeight() >> 1);

        BufferedImage planetImage = new BufferedImage(planetTexture.getWidth(), planetTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) planetImage.createGraphics();
        g2.translate(planetTexture.getWidth() / 2, planetTexture.getHeight() / 2);
        g2.rotate(Math.cos(time / (10 + (20 * id) % 21)));
        g2.translate(-planetTexture.getWidth() / 2, -planetTexture.getHeight() / 2);
        g2.drawImage(planetTexture.getImage(), 0, 0, null);

        g.drawImage(planetImage, uX, uY, null);
        drawText(g, x, y, ((int) planet.getForces()) + "", Color.WHITE, null, HAlign.CENTER, VAlign.CENTER, 10);
    }

    @Override
    public void drawUniverse(Graphics g, Universe universe) {
        super.drawUniverse(g, universe);

        time = universe.getNow();
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {
        for (Fleet fleet : fleets) {
            g.setColor(fleet.getPlayer().getPlayerBackColor());

            final int x = (int) getX(g, fleet.getX());
            final int y = (int) getY(g, fleet.getY());

            final int s = 5 + (int) (4 * Math.log10(fleet.getForce()));

            double t = fleet.getTimeToTarget();

            drawStar(g, x, y, s, t);
        }
    }

    private void drawStar(Graphics g, int x, int y, int s, double t) {
        int sides = s % 4 + 5;

        Point[] innerPoints = pointsOnCircle(sides, s, 0 + t / 4.0);
        Point[] outerPoints = pointsOnCircle(sides, s / 2, 0.5 + t / 4.0);

        int[] xPoints = new int[sides * 2];
        for (int i = 0; i < sides; i++) {
            xPoints[2 * i] = x + innerPoints[i].x;
            xPoints[2 * i + 1] = x + outerPoints[i].x;
        }

        int[] yPoints = new int[sides * 2];
        for (int i = 0; i < sides; i++) {
            yPoints[2 * i] = y - innerPoints[i].y;
            yPoints[2 * i + 1] = y - outerPoints[i].y;
        }

        g.fillPolygon(xPoints, yPoints, sides * 2);

        ((Graphics2D) g).setStroke(new BasicStroke(s < 10 ? 0 : s < 20 ? 1 : 2));
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, sides * 2);
    }

    private Point[] pointsOnCircle(int sides, int r, double offset) {
        Point[] result = new Point[sides];

        for (int i = 0; i < sides; i++) {
            double x = Math.cos(2.0 * (i + offset) / sides * Math.PI - Math.PI / 2) * r;
            double y = Math.sin(2.0 * (i + offset) / sides * Math.PI - Math.PI / 2) * r;

            result[i] = new Point((int) x, (int) y);
        }

        return result;
    }

}

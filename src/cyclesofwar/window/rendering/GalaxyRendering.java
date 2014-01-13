package cyclesofwar.window.rendering;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Universe;
import cyclesofwar.window.rendering.textures.AsteroidTexture;
import cyclesofwar.window.rendering.textures.ColorTools;
import cyclesofwar.window.rendering.textures.GalaxyTexture;
import cyclesofwar.window.rendering.textures.PlanetTexture;
import cyclesofwar.window.rendering.textures.Texture;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class GalaxyRendering extends FancyRendering {

    double time;
    double angle;
    double sinAngle;
    double cosAngle;

    double scale = 0.5;
    double sqrtScale;

    @Override
    public void drawUniverse(Graphics g, Universe universe) {
        time = universe.getNow();
        angle = time / 30;
        sinAngle = Math.sin(angle);
        cosAngle = Math.cos(angle);
        scale = 2.0 / (2.0 + time / universe.getAllPlanets().size());
        sqrtScale = Math.pow(scale, 0.7);

        super.drawUniverse(g, universe);
    }

    @Override
    protected Texture createBackground(long universeSeed) {
        return new GalaxyTexture(size.width, size.height, universeSeed, 1.0);
    }

    @Override
    protected void updatePlanetTexture(int i, Planet planet) {
        if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
            final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
            planetTextures.put(i, new AsteroidTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
        }
    }

    @Override
    public void drawPlanet(Graphics g, Planet planet, int id) {
        updatePlanetTexture(id, planet);

        PlanetTexture planetTexture = planetTextures.get(id);

        int x = (int) getX(g, planet.getX());
        int y = (int) getY(g, planet.getY());

        int[] xy = getXY(x, y);

        int uX = xy[0] - (planetTexture.getWidth() >> 1);
        int uY = xy[1] - (planetTexture.getHeight() >> 1);

        BufferedImage planetImage = new BufferedImage(planetTexture.getWidth(), planetTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) planetImage.createGraphics();
        g2.translate(planetTexture.getWidth() / 2, planetTexture.getHeight() / 2);
        g2.scale(sqrtScale, sqrtScale);
        g2.rotate(time / (10 + (20 * id) % 21));
        g2.translate(-planetTexture.getWidth() / 2, -planetTexture.getHeight() / 2);
        g2.drawImage(planetTexture.getImage(), 0, 0, null);

        g.drawImage(planetImage, uX, uY, null);
        drawText(g, uX + planetImage.getWidth() / 2, uY + planetTexture.getHeight() / 2, ((int) planet.getForces()) + "", Color.WHITE, null, HAlign.CENTER, VAlign.CENTER, 10);
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {

        final int base = 10;

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        for (Fleet fleet : fleets) {

            double x = getX(g, fleet.getX());
            double y = getY(g, fleet.getY());

            int[] xy = getXY(x, y);

            x = xy[0];
            y = xy[1];

            double fx = getX(g, fleet.getTarget().getX());
            double fy = getY(g, fleet.getTarget().getY());

            int[] fxy = getXY(fx, fy);

            fx = fxy[0];
            fy = fxy[1];

            // double localTime = time - fleet.getTimeToTarget();
            double xDiff = fx - x;
            double yDiff = fy - y;

            double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

            double sinAngle = yDiff / dist;
            double cosAngle = xDiff / dist;

            int forces = fleet.getForce();
            int ring = 0;
            while (forces > 0) {

                int currentForces = forces % base;
                forces /= base;
                ring++;

                double length = ring * ring * 4;

                int[] one = getXY(length / 2, -length / 2, x, y, sinAngle, cosAngle);
                int[] two = getXY(-length / 2, 0, x, y, sinAngle, cosAngle);
                int[] three = getXY(length / 2, length / 2, x, y, sinAngle, cosAngle);

                g.setColor(ColorTools.transparent(fleet.getPlayer().getPlayerBackColor(), 0.4 + 0.6 * ((double) currentForces) / base));
                g.drawPolygon(new int[]{one[0], two[0], three[0]}, new int[]{one[1], two[1], three[1]}, 3);
            }

            //drawText(g2, (int) x, (int) y, fleet.getForce() + "", getPlayerTextColor(fleet.getPlayer()), null, HAlign.CENTER, VAlign.CENTER, 10);
        }

        g2.setStroke(new BasicStroke(1));
    }

    protected int[] getXY(double x, double y) {
        //double scale = 1.0;
        x = x * scale - size.width / 2 * scale;
        y = y * scale * size.width / (double) size.height - size.width / 2 * scale;

        return new int[]{(int) (size.width / 2 + (x * cosAngle + y * sinAngle)), (int) (size.height / 2 + (-x * sinAngle + y * cosAngle))};
    }
}

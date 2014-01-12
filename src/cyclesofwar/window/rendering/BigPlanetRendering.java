package cyclesofwar.window.rendering;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.window.rendering.textures.BigPlanetTexture;
import cyclesofwar.window.rendering.textures.ColorTools;
import cyclesofwar.window.rendering.textures.PlanetTexture;
import cyclesofwar.window.rendering.textures.SatelliteTexture;
import cyclesofwar.window.rendering.textures.Texture;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

public class BigPlanetRendering extends FancyRendering {

    @Override
    protected Texture createBackground(long universeSeed) {
        return new BigPlanetTexture(size.width, size.height, universeSeed);
    }

    @Override
    protected void updatePlanetTexture(int i, Planet planet) {
        if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
            final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
            planetTextures.put(i, new SatelliteTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
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

        g.drawImage(planetTexture.getImage(), uX, uY, null);
    }

    @Override
    protected int planetSize(int size, double productionRatePerSecond) {
        return (int) ((productionRatePerSecond + 1.0) * 20.0 * size / 1000.0);
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        for (Fleet fleet : fleets) {

            double x = getX(g, fleet.getX());
            double y = getY(g, fleet.getY());

            // double localTime = time - fleet.getTimeToTarget();
            double xDiff = getX(g, fleet.getTarget().getX()) - x;
            double yDiff = getY(g, fleet.getTarget().getY()) - y;

            double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

            double sinAngle = yDiff / dist;
            double cosAngle = xDiff / dist;

            double length = Math.min(fleet.getForce(), dist);

            int[] one = getXY(0, 0, x, y, sinAngle, cosAngle);
            int[] two = getXY(-length, 0, x, y, sinAngle, cosAngle);

            g.setColor(ColorTools.transparent(fleet.getPlayer().getPlayerBackColor(), 0.3 + 0.7 * fleet.getForce() / 100.0));
            g.drawLine(one[0], one[1], two[0], two[1]);

            //drawText(g2, (int) x, (int) y, fleet.getForce() + "", getPlayerTextColor(fleet.getPlayer()), null, HAlign.CENTER, VAlign.CENTER, 10);
        }

        g2.setStroke(new BasicStroke(1));
    }
}

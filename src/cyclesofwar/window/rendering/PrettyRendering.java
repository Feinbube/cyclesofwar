package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.window.rendering.textures.ColorTools;
import cyclesofwar.window.rendering.textures.PrettyPlanetTexture;
import cyclesofwar.window.rendering.textures.PrettyTexture;
import cyclesofwar.window.rendering.textures.Texture;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;

public class PrettyRendering extends FancyRendering {

    public PrettyRendering() {
        gameTitle = "Cycles of Love";

        textColor = new Color(139, 71, 93);

        fancyTextColor = ColorTools.transparent(textColor, 0.8f);
        fancyInActiveTextColor = ColorTools.transparent(textColor.darker(), 0.5f);

        fancyActiveBorderColor = ColorTools.transparent(textColor, 0.5f);
        fancyInActiveBorderColor = ColorTools.transparent(textColor.darker(), 0.3f);

        fancyActiveBackColor = ColorTools.transparent(Color.pink, 0.5f);
        fancyInActiveBackColor = ColorTools.transparent(Color.pink.darker(), 0.3f);
    }

    @Override
    public Font getFont(int style, int fontSize) {
        return new Font("Vivaldi", style, (int) getScaled(fontSize));
    }

    @Override
    protected Texture createBackground(long universeSeed) {
        return new PrettyTexture(size.width, size.height, universeSeed);
    }

    @Override
    protected void updatePlanetTexture(int i, Planet planet) {
        if (!planetTextures.containsKey(i) || !planetTextures.get(i).getColor().equals(planet.getPlayer().getPlayerBackColor())) {
            final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());
            planetTextures.put(i, new PrettyPlanetTexture(planetSize, planetSize, i, planet.getPlayer().getPlayerBackColor()));
        }
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {
        for (Fleet fleet : fleets) {
            g.setColor(fleet.getPlayer().getPlayerBackColor());

            final int x = (int) getX(g, fleet.getX());
            final int y = (int) getY(g, fleet.getY());

            final int s = 5 + (int) (4 * Math.log10(fleet.getForce()));

            double t = fleet.getTimeToTarget();
            
            if (fleet.getFormation() == Fleet.Formation.ARROW || fleet.getFormation() == Fleet.Formation.V) {
                drawHeart(g, x, y, s, t);
            } else if (fleet.getFormation() == Fleet.Formation.O || fleet.getFormation() == Fleet.Formation.EYE) {
                drawSimley(g, x, y, s,t );
            } else if (fleet.getFormation() == Fleet.Formation.SWARM || fleet.getFormation() == Fleet.Formation.SPIRAL) {
                drawStar(g, x, y, s,t);
            }
        }
    }

    private void drawHeart(Graphics g, int x, int y, int s, double t) {
        s = (int)(s*Math.abs(Math.sin(t/3))/2.0 + s/2.0);
        
        g.fillArc(x - s, y - s / 2, s, s, 0, 180);
        g.fillArc(x, y - s / 2, s, s, 0, 180);
        g.fillPolygon(new int[]{x - s, x, x + s}, new int[]{y, y + (int) (1.5 * s), y}, 3);

        ((Graphics2D) g).setStroke(new BasicStroke(s < 10 ? 1 : 2));
        g.setColor(Color.BLACK);
        g.drawArc(x - s, y - s / 2, s, s, 0, 180);
        g.drawArc(x, y - s / 2, s, s, 0, 180);
        g.drawLine(x - s, y, x, y + (int) (1.5 * s));
        g.drawLine(x + s, y, x, y + (int) (1.5 * s));
        ((Graphics2D) g).setStroke(new BasicStroke(1));
    }

    private void drawSimley(Graphics g, int x, int y, int s, double t) {
        s *= 2;
        
        g.fillOval(x - s / 2, y - s / 2, s, s);

        ((Graphics2D) g).setStroke(new BasicStroke(s < 10 ? 1 : 2));
        g.setColor(Color.BLACK);

        g.drawOval(x - s / 2, y - s / 2, s, s);
        
        g.fillOval(x - s / 4 - s / 12, y - s / 4, s / 6, s / 6);
        g.fillOval(x + s / 4 - s / 12, y - s / 4, s / 6, s / 6);

        ((Graphics2D) g).setStroke(new BasicStroke(s / 18 + 1));
        g.drawArc(x - s / 4, y - s / 4, s / 2, s / 2, 200, 140);
        
        ((Graphics2D) g).setStroke(new BasicStroke(1));
    }
    
    private void drawStar(Graphics g, int x, int y, int s, double t) {
        int sides = s % 4 + 5;
        
        Point[] innerPoints = pointsOnCircle(sides, s, 0 + t/4.0);
        Point[] outerPoints = pointsOnCircle(sides, s/2, 0.5 + t/4.0);
        
        int[] xPoints = new int[sides*2];
        for(int i=0; i<sides; i++) {
            xPoints[2*i] = x + innerPoints[i].x;
            xPoints[2*i+1] = x + outerPoints[i].x;
        }
        
        int[] yPoints = new int[sides*2];
        for(int i=0; i<sides; i++) {
            yPoints[2*i] = y - innerPoints[i].y;
            yPoints[2*i+1] = y - outerPoints[i].y;
        }
        
        g.fillPolygon(xPoints,yPoints,sides*2);
        
        ((Graphics2D) g).setStroke(new BasicStroke(s < 10 ? 0 : s < 20 ? 1 : 2));
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints,yPoints,sides*2);
    }

    private Point[] pointsOnCircle(int sides, int r, double offset) {
        Point[] result = new Point[sides];
        
        for(int i=0; i<sides; i++) {
            double x = Math.cos(2.0*(i+offset)/sides*Math.PI-Math.PI/2)*r;
            double y = Math.sin(2.0*(i+offset)/sides*Math.PI-Math.PI/2)*r;
            
            result[i] = new Point((int)x, (int)y);
        }
        
        return result;
    }
}
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

public class PrettyRendering extends FancyRendering {

	public PrettyRendering() {
		gameTitle = "Flower Wars";
		
		textColor = new Color(139,71,93);
		
		fancyTextColor = ColorTools.transparent(textColor, 0.8f);
	    fancyInActiveTextColor = ColorTools.transparent(textColor.darker(), 0.5f);

	    fancyActiveBorderColor = ColorTools.transparent(textColor, 0.5f);
	    fancyInActiveBorderColor = ColorTools.transparent(textColor.darker(), 0.3f);

	    fancyActiveBackColor = ColorTools.transparent(Color.pink, 0.5f);
	    fancyInActiveBackColor = ColorTools.transparent(Color.pink.darker(), 0.3f);
	}
	
	@Override
	public Font getFont(int style, int fontSize) {
		return new Font("Vivaldi", style, (int)getScaled(fontSize));
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

            final int x = (int)getX(g, fleet.getX());
            final int y = (int)getY(g, fleet.getY());
         
            final int s = 3 + (int) (4 * Math.log10(fleet.getForce()));

            drawHeart(g, x, y, s);
		}
    }

	private void drawHeart(Graphics g, int x, int y, int s) {
		g.fillArc(x-s, y-s/2, s, s, 0, 180);
		g.fillArc(x, y-s/2, s, s, 0, 180);
		g.fillPolygon(new int[]{x-s, x, x+s}, new int[]{y, y+(int)(1.5*s), y}, 3);
	}
}
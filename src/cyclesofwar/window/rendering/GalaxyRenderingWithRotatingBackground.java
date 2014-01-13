package cyclesofwar.window.rendering;

import cyclesofwar.window.rendering.textures.GalaxyTexture;
import cyclesofwar.window.rendering.textures.Texture;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GalaxyRenderingWithRotatingBackground extends GalaxyRendering {

    @Override
    public void drawBackground(Graphics g, long universeSeed) {
        int w = size.width; //this.getBackground(universeSeed).getWidth();
        int h = size.height; //this.getBackground(universeSeed).getHeight();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(w / 2, h / 2);
        g2.rotate(-angle / 2);
        g2.translate(-w / 2, -h / 2);

        g.drawImage(this.getBackground(universeSeed).getImage(), -(this.getBackground(universeSeed).getWidth() - size.width) / 2, -(this.getBackground(universeSeed).getHeight() - size.height) / 2, null);
        
        g2.translate(w / 2, h / 2);
        g2.rotate(angle / 2);
        g2.translate(-w / 2, -h / 2);
    }

    @Override
    protected Texture createBackground(long universeSeed) {
        return new GalaxyTexture((int) (Math.max(size.width, size.height) * 1.4), (int) (Math.max(size.width, size.height) * 1.4), universeSeed, 0.7);
    }
}

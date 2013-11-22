package cyclesofwar.window.rendering.noise;

import cyclesofwar.window.rendering.textures.ColorTools;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public abstract class Noise {

    public int seed = 0;
    public double zoom = 0.01;

    public Noise(int seed) {
        this.seed = seed;
    }

    public Noise(int seed, double zoom) {
        this.seed = seed;
        this.zoom = zoom;
    }

    public abstract double raw(double x, double y);

    public double at(double x, double y) {
        return raw((x + seed) * zoom, (y + seed) * zoom);
    }

    public Color at(double x, double y, Color c) {
        return at(x, y, Color.BLACK, c);
    }

    public Color at(double x, double y, Color c1, Color c2) {
        return ColorTools.interpolate(c1, c2, at(x, y));
    }

    public BufferedImage texture(int width, int height, Color c) {
        return texture(width, height, Color.BLACK, c);
    }

    public BufferedImage texture(int width, int height, Color c1, Color c2) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                pixels[y * width + x] = at(x, y, c1, c2).hashCode();
            }
        }

        return image;
    }
}

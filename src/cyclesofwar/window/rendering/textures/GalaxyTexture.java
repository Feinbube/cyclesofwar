package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.*;
import cyclesofwar.window.rendering.noise.simplex.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class GalaxyTexture extends Texture {

    double scale;
    
    protected static final int SEED_CUT = 16;
    protected static final Color[] NEBULA_COLORS = {
        new Color(64, 64, 255),
        Color.CYAN,
        Color.RED,
        Color.GREEN,
        Color.MAGENTA
    };

    public GalaxyTexture(final int width, final int height, final long seed, double scale) {
        super(width, height, seed);
        
        this.scale = scale;
    }

    @Override
    protected BufferedImage generate() {
        Color color = NEBULA_COLORS[random.nextInt(NEBULA_COLORS.length)];
        int seed = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.pitchBlack(image);
        new NebulaNoise(seed).texture(image, color);
        this.galaxy(image, seed);
        new StarNoise(seed).blendTexture(image, Color.WHITE);

        return image;
    }

    protected void galaxy(BufferedImage image, int seed) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int height = image.getHeight();
        final int width = image.getWidth();
        final int radius = (int)(Math.min(height, width) / 2 * scale);

        final int xMid = width / 2;
        final int yMid = height / 2;

        this.galaxyRing(pixels, xMid, yMid, radius, 0.6, (new Color(124, 145, 192)).darker(), seed);
        this.galaxyRing(pixels, xMid, yMid, (int) (0.6 * radius), 0.3, Color.ORANGE, seed);
        this.galaxyRing(pixels, xMid, yMid, (int) (0.2 * radius), 0.1, Color.WHITE, seed);
    }

    protected void galaxyRing(int[] pixels, int xMid, int yMid, int radius, double radiusFraction, Color color, int seed) {
        final int[] IN = pixels.clone();
        final int iRadius = (int) (radiusFraction * radius);
        final int outer = radius - iRadius;

        Noise fbm = new FractualBrownianMotionNoise(seed);

        for (int y = yMid - radius, yShift = -radius; y < yMid + radius; ++y, ++yShift) {
            for (int x = xMid - radius, xShift = -radius; x < xMid + radius; ++x, ++xShift) {
                final double DIST = Math.sqrt(xShift * xShift + yShift * yShift);
                final double SHARE = (radius - DIST) / radius;
                final double THETA = SHARE * SHARE * Math.PI / 4 * 8.0;
                final double SIN = Math.sin(THETA);
                final double COS = Math.cos(THETA);

                final int swirlX = (int) (xShift * COS - yShift * SIN) + xMid;
                final int swirlY = (int) (xShift * SIN + yShift * COS) + yMid;

                final int READ_INDEX = swirlY * width + swirlX;
                final int WRITE_INDEX = y * width + x;

                final double NOISE = Math.pow((1.0 + fbm.raw(x * 0.005, y * 0.005)) / 2, 1.4);

                double fraction;
                if (radius < DIST) // outside the circle
                {
                    continue;
                } else if (radius >= DIST && DIST >= iRadius) // outer nebula
                {
                    fraction = 1.0 - ((DIST - iRadius) / outer);
                } else // inner nebula
                {
                    fraction = 1.0;
                }
                fraction *= NOISE;

                Color old = new Color(IN[READ_INDEX]);
                Color newColor = ColorTools.interpolate(old, color, (float) fraction);

                pixels[WRITE_INDEX] = newColor.hashCode();
            }
        }
    }
}

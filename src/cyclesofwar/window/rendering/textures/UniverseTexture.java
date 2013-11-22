package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.perlin.FBM;
import cyclesofwar.window.rendering.noise.*;
import cyclesofwar.window.rendering.noise.perlin.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class UniverseTexture extends Texture {

    private static final int SEED_CUT = 16;
    private static final Color[] NEBULA_COLORS = {
        Color.BLUE.brighter(),
        Color.CYAN.darker(),
        Color.RED.darker(),
        Color.GREEN.darker(),
        Color.MAGENTA.darker()
    };
    private static final int MIN_STARS = 96;
    private static final int VAR_STARS = 64;
    private static final int STAR_SIZE = 2;

    private static final double MIN_BRIGHTNESS = 0.5;
    private static final double MAX_BRIGHTNESS = 5;
    private static final double BRIGHTNESS_RANGE = MAX_BRIGHTNESS - MIN_BRIGHTNESS;

    final private int stars;

    public UniverseTexture(final int width, final int height, final long seed) {
        super(width, height, seed);
        this.stars = MIN_STARS + random.nextInt(VAR_STARS);
    }

    @Override
    protected BufferedImage generate() {
        Color color = NEBULA_COLORS[random.nextInt(NEBULA_COLORS.length)];
        int seed = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);
        
        image = new FBM2(seed).texture(width, height, color);
        this.stars(image);

        return image;
    }

    private void stars(BufferedImage image) {
        Graphics2D canvas = image.createGraphics();

        for (int star = 0; star < this.stars; ++star) {
            final int x = this.random.nextInt(this.width);
            final int y = this.random.nextInt(this.height);

            final double brightness = MIN_BRIGHTNESS + BRIGHTNESS_RANGE * this.random.nextDouble();
            final int grey = (int) (255.0 / MAX_BRIGHTNESS * brightness);

            canvas.setColor(new Color(grey, grey, grey));
            canvas.fillOval(x, y, STAR_SIZE, STAR_SIZE);
        }
    }

    private void galaxy(BufferedImage image, int seed) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int height = image.getHeight();
        final int width = image.getWidth();
        final int radius = Math.min(height, width) / 2;

        final int xMid = width / 2;
        final int yMid = height / 2;

        this.galaxyRing(pixels, xMid, yMid, radius, 0.6, (new Color(124, 145, 192)).darker(), seed);
        this.galaxyRing(pixels, xMid, yMid, (int) (0.6 * radius), 0.3, Color.ORANGE, seed);
        this.galaxyRing(pixels, xMid, yMid, (int) (0.2 * radius), 0.1, Color.WHITE, seed);
    }

    private void galaxyRing(int[] pixels, int xMid, int yMid, int radius, double radiusFraction, Color color, int seed) {
        final int[] IN = pixels.clone();
        final int iRadius = (int) (radiusFraction * radius);
        final int outer = radius - iRadius;

        Noise fbm = new FBM(seed);

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

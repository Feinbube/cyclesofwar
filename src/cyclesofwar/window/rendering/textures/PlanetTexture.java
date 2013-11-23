package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.simplex.FractualBrownianMotionNoise;
import cyclesofwar.window.rendering.noise.simplex.SimplexNoise;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PlanetTexture extends Texture {

    private static final int SEED_CUT = 16;

    private final Color color;

    public PlanetTexture(final int width, final int height, final long seed, final Color color) {
        super(width, height, seed);

        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    protected BufferedImage generate() {
        BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

        this.pitchBlack(result);
        this.planetTexture(result, this.color);

        return result;
    }

    private float getDistance(float x, float y, float midX, float midY) {
        float x2 = x - midX;
        x2 *= x2;

        float y2 = y - midY;
        y2 *= y2;

        return (float) Math.sqrt(x2 + y2);
    }

    private void planetTexture(BufferedImage image, Color color) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int SEED = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        Color c1 = color;
        Color c2 = c1.darker();
        Color c3 = c2.darker();
        Color c4 = c3.darker();

        Color atmosphere = ColorTools.interpolate(Color.BLUE, Color.WHITE, 0.8f);

        float w2 = width / 2;
        float w7 = width / 7;

        Noise noise = new FractualBrownianMotionNoise(SEED);
        noise.zoom *= 5;

        float value = 0.5f;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double NOISE = noise.at(x, y);

                float d = getDistance(x, y, w2, height / 2.0f);

                if (d < w2 - w7) { // planet

                    // continents
                    Color c = NOISE < value
                            ? ColorTools.interpolate(c1, c2, (float) NOISE / value)
                            : ColorTools.interpolate(c3, c4, (float) (NOISE - value) / value);

                    // darker at the edges
                    float d2 = getDistance(x, y, width / 4.0f, height / 4.0f); // spot not in the middle of the planet :)
                    d2 = (float)Math.pow(d2, 1.11);
                    c = ColorTools.interpolate(c, Color.BLACK, d2 * 2.0f / width - 0.35f);

                    // atmosphere
                    c = ColorTools.interpolate(c, atmosphere, 0.1f);

                    pixels[y * width + x] = c.hashCode();

                } else if (d < w2) { // atmosphere
                    Color c = atmosphere;

                    // darker at the edges
                    float d2 = getDistance(x, y, width / 4.0f, height / 4.0f); // spot not in the middle of the planet :)
                    d2 = d2 * 2.0f / width - 0.8f;
                    c = ColorTools.interpolate(c, Color.BLACK, d2);

                    // less visible at the edges
                    float alpha = (1.0f - ((d + w7 - w2) / w7)) * 0.35f;
                    pixels[y * width + x] = ColorTools.transparent(c, alpha).hashCode();

                } else { // space
                    pixels[y * width + x] = 0;
                }

            }
        }
    }
}

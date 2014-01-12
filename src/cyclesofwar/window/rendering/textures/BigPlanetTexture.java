package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.cell.PointNoise;
import cyclesofwar.window.rendering.noise.simplex.*;
import static cyclesofwar.window.rendering.textures.GalaxyTexture.NEBULA_COLORS;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class BigPlanetTexture extends GalaxyTexture {

    public BigPlanetTexture(final int width, final int height, final long seed) {
        super(width, height, seed);
    }

    @Override
    protected BufferedImage generate() {
        Color color = NEBULA_COLORS[random.nextInt(NEBULA_COLORS.length)];
        int seed = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.pitchBlack(result);
        new NebulaNoise(seed).texture(result, color);
        new StarNoise(seed).blendTexture(result, Color.WHITE);
        this.planet(result, seed);

        return result;
    }

    protected float getDistance(float x, float y, float midX, float midY) {
        float x2 = x - midX;
        x2 *= x2;

        float y2 = y - midY;
        y2 *= y2;

        return (float) Math.sqrt(x2 + y2);
    }

    protected void planet(BufferedImage image, int seed) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final int SEED = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        Color c1 = new Color(0, 0, 0);
        Color c2 = new Color(0, 0, 160);

        Color c3 = new Color(0, 160, 0);
        Color c4 = new Color(0, 0, 0);

        Color atmosphere = ColorTools.interpolate(Color.BLUE, Color.WHITE, 0.8f);

        float w2 = width / 2.0f;
        float h2 = height / 2.0f;
        float planetwidth = w2 - width / 7;

        Noise noise = new FractualBrownianMotionNoise(SEED);
        noise.zoom /= 5;

        Noise nebula = new NebulaNoise(SEED);
        nebula.zoom /= 2;

        float value = 0.35f;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                float d = getDistance(x, y, w2, h2);

                if (d <= planetwidth) { // planet

                    float factor = (d / planetwidth * d / planetwidth + 4) / 4;
                    factor *= factor;
                    float sphereX = (x - w2) * factor;
                    float sphereY = (y - h2) * factor;

                    // continents
                    double NOISE = noise.at(sphereX, sphereY);
                    Color c = NOISE < 0.5f
                            ? ColorTools.interpolate(c1, c2, (float) NOISE / value)
                            : ColorTools.interpolate(c3, c4, (float) (NOISE - value) / value);

                    // ice
                    double NEBULA = nebula.at(sphereX, sphereY);
                    NEBULA = (float) Math.pow(NEBULA, 0.5);
                    NEBULA = NEBULA * 1.7f;
                    NEBULA = NEBULA * NEBULA;
                    NEBULA = NEBULA * 1.3f;
                    factor = (y - h2) / h2;
                    factor *= factor * 1.8f;
                    factor *= factor;
                    NEBULA *= factor;
                    c = ColorTools.interpolate(c, Color.WHITE, NEBULA);

                    // clouds
                    NEBULA = nebula.at(sphereX, sphereY);
                    NEBULA = (float) Math.pow(NEBULA, 0.5);
                    NEBULA = NEBULA * 1.7f;
                    NEBULA = NEBULA * NEBULA;
                    c = ColorTools.interpolate(c, Color.WHITE, NEBULA);

                    // atmosphere
                    c = ColorTools.interpolate(c, atmosphere, 0.1f);

                    float d3 = d / planetwidth;
                    d3 = (float) Math.pow(d3, 20);
                    c = ColorTools.interpolate(c, atmosphere, d3);

                    // darker at the edges
                    float d2 = getDistance(x, y, width / 3.0f, height / 3.0f); // spot not in the middle of the planet :)
                    d2 = (float) Math.pow(d2, 1.09);
                    factor = d2 * 2.0f / width - 0.35f;
                    c = ColorTools.interpolate(c, Color.BLACK, factor);

                    // cities at night
                    // ??

                    pixels[y * width + x] = c.hashCode();
                } else if (d <= planetwidth + w2 / 100) { // outer atmosphere
                    Color c = atmosphere;

                    // darker at the edges
                    float d2 = getDistance(x, y, width / 3.0f, height / 3.0f); // spot not in the middle of the planet :)
                    d2 = (float) Math.pow(d2, 1.09);
                    c = ColorTools.interpolate(c, Color.BLACK, d2 * 2.0f / width - 0.35f);

                    pixels[y * width + x] = c.hashCode();
                }
            }
        }
    }
}

package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.cell.PointNoise;
import cyclesofwar.window.rendering.noise.simplex.FractualBrownianMotionNoise;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class AsteroidTexture extends PlanetTexture {

    public AsteroidTexture(int width, int height, long seed, Color color) {
        super(width, height, seed, color);
    }

    @Override
    protected BufferedImage generate() {
        BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

        this.cellTexture(result, this.color);

        return result;
    }

    private void cellTexture(BufferedImage image, Color color) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Graphics2D canvas = image.createGraphics();
        final int SEED = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        drawBody(canvas, color);
        paintBody(pixels, color, SEED);
    }

    protected double r() {
        return random.nextDouble();
    }

    protected void drawBody(Graphics2D canvas, Color color) {

        int w2 = width / 2;
        float w4 = width / 4.0f;
        float w8 = width / 8.0f;
        int h = (int) (height * (random.nextDouble() * 0.4 + 0.6));
        int h2 = h / 2;

        canvas.translate(0, (height - h) / 2);

        GeneralPath shape = new GeneralPath();
        shape.moveTo(0, h2);
        shape.curveTo(
                w8 - w4 * r(), 0,
                w2, 0,
                w2, 0);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);

        shape = new GeneralPath();
        shape.moveTo(w2, 0);
        shape.curveTo(
                width - w8 + w4 * r(), 0,
                width, h2,
                width, h2);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);

        shape = new GeneralPath();
        shape.moveTo(width, h2);
        shape.curveTo(
                width - w8 + w4 * r(), h,
                w2, h,
                w2, h);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);

        shape = new GeneralPath();
        shape.moveTo(w2, h);
        shape.curveTo(
                w8 - w4 * r(), h,
                0, h2,
                0, h2);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);

        canvas.fillPolygon(new int[]{0, w2, width}, new int[]{h / 2, 0, h2}, 3);
        canvas.drawPolygon(new int[]{0, w2, width}, new int[]{h / 2, 0, h2}, 3);
        canvas.fillPolygon(new int[]{0, w2, width}, new int[]{h / 2, h, h2}, 3);
        canvas.drawPolygon(new int[]{0, w2, width}, new int[]{h / 2, h, h2}, 3);

    }

    protected void drawBody2(Graphics2D canvas, Color color) {

        GeneralPath shape = new GeneralPath();
        shape.moveTo(0, height / 2);
        shape.curveTo(width * (this.random.nextDouble() * 0.2), height, width * (this.random.nextDouble() * 0.3 + 0.35), height, width, height / 2);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);

        shape = new GeneralPath();
        shape.moveTo(0, height / 2);
        shape.curveTo(width * (this.random.nextDouble() * 0.2), 0, width, height * (this.random.nextDouble() * 0.4 + 0.1), width, height / 2);
        shape.closePath();

        canvas.setColor(color);
        canvas.fill(shape);
    }

    protected void paintBody(int[] pixels, Color color, int SEED) {
        Noise noise = new FractualBrownianMotionNoise(SEED);
        noise.zoom *= 8;

        Noise pointNoise = new PointNoise();
        pointNoise.zoom *= 8;

        float w2 = width / 2.0f;
        float h2 = height / 2.0f;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (pixels[y * width + x] == color.hashCode()) {
                    float d = getDistance(x, y, w2, h2);
                    float factor = (d / w2 * d / w2 + 4) / 4;
                    factor *= factor;
                    float sphereX = (x - w2) * factor;
                    float sphereY = (y - h2) * factor;

                    double NOISE = noise.at(sphereX + SEED, sphereY + SEED);
                    double PointNOISE = pointNoise.at(sphereX + SEED, sphereY + SEED);

                    Color c = ColorTools.interpolate(color, color.darker().darker().darker(), NOISE);
                    c = ColorTools.interpolate(c, Color.DARK_GRAY.darker(), PointNOISE);
                    
                    // darker at the edges
                    float d2 = getDistance(x, y, width / 3.0f, height / 3.0f); // spot not in the middle of the planet :)
                    d2 = (float) Math.pow(d2, 1.11);
                    c = ColorTools.interpolate(c, Color.BLACK, d2 * 2.0f / width - 0.5f);

                    pixels[y * width + x] = c.hashCode();
                }
            }
        }
    }
}

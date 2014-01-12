package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.cell.PointNoise;
import cyclesofwar.window.rendering.noise.simplex.FractualBrownianMotionNoise;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class CellTexture extends PlanetTexture {

    public CellTexture(int width, int height, long seed, Color color) {
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

        int h = (int) (height * (random.nextDouble() * 0.4 + 0.6));

        drawLegs(canvas, pixels, color, h);
        drawBody(canvas, color, h);
        paintBody(pixels, color, SEED);
    }

    protected void drawLegs(Graphics2D canvas, int[] pixels, Color color, int h) {

        Color c = color.darker();
        canvas.setColor(c);

        float w2 = width / 2.0f;
        float h2 = height / 2.0f;

        int times = random.nextInt(8) + 8;
        float steps = 360.0f / times;
        for (float i = 0; i < 360.0; i += steps) {
            canvas.drawLine(-width, (int) h2, 2 * width, (int) h2);
            canvas.translate(w2, h2);
            canvas.rotate(steps * Math.PI / 180);
            canvas.translate(-w2, -h2);
        }

        Color transparent = new Color(0, 0, 0, 0);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float d = getDistance(x, y, w2, h2);
                if (d > w2 * 1.2 || y < (height - h) / 3 || y > height - (height - h) / 3) {
                    pixels[y * width + x] = transparent.hashCode();
                }
            }
        }
    }

    protected double r() {
        return random.nextDouble();
    }

    protected void drawBody(Graphics2D canvas, Color color, int h) {

        int w2 = width / 2;
        float w4 = width / 4.0f;
        float w8 = width / 8.0f;
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
        pointNoise.zoom *= 3;

        float w2 = width / 2.0f;
        float h2 = height / 2.0f;

        Color c2 = color.darker().darker().darker();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (pixels[y * width + x] == color.hashCode()) {
                    float d = getDistance(x, y, w2, h2) * 1.5f;
                    float factor = (d / w2 * d / w2 + 4) / 4;
                    factor *= factor;
                    float sphereX = (x - w2) * factor;
                    float sphereY = (y - h2) * factor;

                    Color c = ColorTools.interpolate(color, c2, noise.at(sphereX + SEED, sphereY + SEED));
                    c = ColorTools.interpolate(c, Color.BLACK, pointNoise.at(sphereX + SEED, sphereY + SEED));

                    pixels[y * width + x] = c.hashCode();
                }
            }
        }
    }
}

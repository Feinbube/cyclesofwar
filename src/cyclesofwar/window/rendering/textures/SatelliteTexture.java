package cyclesofwar.window.rendering.textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SatelliteTexture extends PlanetTexture {

    int panelCount = 0;

    public SatelliteTexture(int width, int height, long seed, Color color) {
        this(width, height, seed, color, 6);
    }

    public SatelliteTexture(int width, int height, long seed, Color color, int panelCount) {
        super(width, height, seed, color);

        this.panelCount = panelCount;
    }

    @Override
    protected BufferedImage generate() {

        BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();

        Graphics2D canvas = result.createGraphics();
        drawSimpleSatellite(canvas);

        return result;
    }

    protected void drawSimpleSatellite(Graphics2D canvas) {
        float w2 = width / 2;
        float h2 = height / 2;

        float w15 = width / 15;
        float w30 = w15 / 2;
        float w60 = w30 / 2;
        float h10 = height / 10;
        float h20 = h10 / 2;
        float h40 = h20 / 2;

        // left wings
        drawMount(canvas, w2 - w30 + w60 - 20 * w60, h2 - h40, 20 * w60, h20);
        for (int i = 0; i < Math.min(panelCount - 3, 3); i++) {
            drawWing(canvas, w2 - 3 * w60 - 3 * w30 - i * 7 * w60, h2, 3 * w30, 2 * h20);
        }

        // body
        drawBody(canvas, w2, h2, w15, h20, 2 * h20);

        // right wings
        drawMount(canvas, w2 + w30 + w60, h2 - h40, 20 * w60, h20);
        for (int i = 0; i < Math.min(panelCount, 3); i++) {
            drawWing(canvas, w2 + 3 * w60 + i * 7 * w60, h2, 3 * w30, 2 * h20);
        }
    }

    private void drawMount(Graphics2D canvas, float x, float y, float w, float h) {
        canvas.setColor(Color.WHITE);
        drawLine(canvas, x, y, x + w, y);
        drawLine(canvas, x + h, y - h, x + w + h, y - h);
    }

    private void drawBody(Graphics2D canvas, float x, float y, float w, float h1, float h2) {
        canvas.setColor(color.darker().darker());
        fillRect(canvas, x - w / 2, y - h1 / 2, w, h1);
        canvas.setColor(color);
        fill3DRectX(canvas, x - w / 2, y - h1 / 2, w, h2);
        canvas.setColor(color.darker());
        fill3DRectY(canvas, x + w / 2, y + h1 / 2, h2, h1);

        // black border
        // canvas.setColor(Color.BLACK);
        // drawRect(canvas, x - w / 2, y - h1 / 2, w, h1);
        // draw3DRectX(canvas, x - w / 2, y - h1 / 2, w, h2);
        // draw3DRectY(canvas, x + w / 2, y + h1 / 2, h2, h1);
    }

    private void drawWing(Graphics2D canvas, float x, float y, float w, float h) {
        canvas.setColor(Color.BLUE.darker().darker());
        fill3DRectX(canvas, x, x + w, x + w + h, x + h, y, y - h);

        canvas.setColor(ColorTools.interpolate(Color.BLUE, Color.WHITE, 0.8f));
        draw3DRectX(canvas, x, x + w, x + w + h, x + h, y, y - h);
    }

    private void draw3DRectX(Graphics2D canvas, float x, float y, float w, float h) {
        draw3DRectX(canvas, x, x + w, x + w + h, x + h, y, y - h);
    }

    private void draw3DRectX(Graphics2D canvas, float x1, float x2, float x3, float x4, float y1, float y2) {
        canvas.drawPolygon(new int[]{(int) x1, (int) x2, (int) x3, (int) x4}, new int[]{(int) y1, (int) y1, (int) y2, (int) y2}, 4);
    }

    private void draw3DRectY(Graphics2D canvas, float x, float y, float w, float h) {
        draw3DRectY(canvas, x, x + w, y, y - w, y - w - h, y - h);
    }

    private void draw3DRectY(Graphics2D canvas, float x1, float x2, float y1, float y2, float y3, float y4) {
        canvas.drawPolygon(new int[]{(int) x1, (int) x2, (int) x2, (int) x1}, new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
    }

    private void fill3DRectX(Graphics2D canvas, float x, float y, float w, float h) {
        fill3DRectX(canvas, x, x + w, x + w + h, x + h, y, y - h);
    }

    private void fill3DRectX(Graphics2D canvas, float x1, float x2, float x3, float x4, float y1, float y2) {
        canvas.fillPolygon(new int[]{(int) x1, (int) x2, (int) x3, (int) x4}, new int[]{(int) y1, (int) y1, (int) y2, (int) y2}, 4);
    }

    private void fill3DRectY(Graphics2D canvas, float x, float y, float w, float h) {
        fill3DRectY(canvas, x, x + w, y, y - w, y - w - h, y - h);
    }

    private void fill3DRectY(Graphics2D canvas, float x1, float x2, float y1, float y2, float y3, float y4) {
        canvas.fillPolygon(new int[]{(int) x1, (int) x2, (int) x2, (int) x1}, new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
    }

    private void drawLine(Graphics2D canvas, float x1, float y1, float x2, float y2) {
        canvas.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    private void fillRect(Graphics2D canvas, float x, float y, float w, float h) {
        canvas.fillRect((int) x, (int) y, (int) w, (int) h);
    }

    private void drawRect(Graphics2D canvas, float x, float y, float w, float h) {
        canvas.drawRect((int) x, (int) y, (int) w, (int) h);
    }
}

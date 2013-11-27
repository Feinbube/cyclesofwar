package cyclesofwar.window.rendering.textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.simplex.SimplexNoise;

public class PrettyPlanetTexture extends PlanetTexture {

	public PrettyPlanetTexture(int width, int height, long seed, Color color) {
		super(width, height, seed, color);
	}

	@Override
	protected BufferedImage generate() {
		
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D canvas = image.createGraphics();
		canvas.setColor(Color.PINK);
		canvas.fillRect(0, 0, this.width, this.height);
                
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        final Color c1 = new Color(
        		(color.getRed() + 139)/2,
        		(color.getBlue() + 71)/2,
        		(color.getGreen() + 93)/2);
        
        final Color c2 = c1.darker();
        final Color c3 = c2.darker();
        final Color c4 = c3.darker();
        
        final float w2 = width / 2;
        final float w4 = width / 4;

        Noise noise = new SimplexNoise(random.nextInt());
        noise.zoom *= 5;

        final float[] petalValues = generatePetalValues(w4);
        float value = 0.5f;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double NOISE = noise.at(x, y);

                float d = getDistance(x, y, w2, height / 2.0f);

                if (d < w2 - w4) { // center

                    Color c = NOISE < value
                            ? ColorTools.interpolate(c1, c2, (float) NOISE / value)
                            : ColorTools.interpolate(c3, c4, (float) (NOISE - value) / value);

                    float d2 = getDistance(x, y, width / 4.0f, height / 4.0f);
                    d2 = (float)Math.pow(d2, 1.11);
                    c = ColorTools.interpolate(Color.YELLOW, c, d2 * 2.0f / width - 0.35f);

                    pixels[y * width + x] = c.hashCode();

                } else if (d < w2) { // petals
                    
                	Color c = this.color;
                	assert(petalValues.length == 360);

                    int angle = (int) (Math.atan2(y-height/2.0f, x-w2)*180.0 / Math.PI + 360.0);
                    if (d - w4 < petalValues[angle % 360]) {
                    	pixels[y * width + x] = c.hashCode();
                    } else {
                    	pixels[y * width + x] = 0;
                    }

                } else { // transparent
                    pixels[y * width + x] = 0;
                }
            }
        }
        return image;
	}

	private float[] generatePetalValues(float radius) {
		int petalCount = random.nextInt(6) + 5;
		float[] petalValues = new float[360];
		
		for (int i = 0; i < 360; ++i) {
			petalValues[i] = (float) Math.abs(Math.sin(i*petalCount/2.0 * Math.PI / 180.f) * radius);
		}
		return petalValues;
	}
}

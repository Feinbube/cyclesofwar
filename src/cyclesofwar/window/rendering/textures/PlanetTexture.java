package cyclesofwar.window.rendering.textures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PlanetTexture extends Texture {
	
	private static final double  ZOOM          = 0.1;
	private static final int     SEED_CUT      = 16;

        private final Color color;
	
	public PlanetTexture(final int width, final int height, final long seed, final Color color) {
		super(width, height, seed);
                
                this.color = color;
	}
        
        @Override
	protected BufferedImage generate() {
		BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		this.pitchBlack(result);
                this.nebulaEverywhere(result, this.color);

		return result;
	}
        
        private void nebulaEverywhere(BufferedImage image, Color color) {
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		
		final int SEED = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);
                
                Color c1 = color; 
                Color c2 = c1.darker();
                Color c3 = c2.darker();
                Color c4 = c3.darker();
                
                float value = 0.5f;
		for (int y = 0, yNoise = SEED; y < height ; ++y, ++yNoise) {
			for (int x = 0, xNoise = SEED; x < width; ++x, ++xNoise) {
				final double NOISE = PerlinNoise.noise(xNoise * ZOOM, yNoise * ZOOM);

                                float x2 = x - width/2.0f;
                                x2 *= x2;
                                
                                float y2 = y - height/2.0f;
                                y2 *= y2;
                                
                                float d = (float)Math.sqrt(x2 + y2);
                                
                                if(d < width/2) {
                                
                                    if(NOISE < value) {
                                        float t = (float)NOISE * 1.0f/value;
                                        pixels[y*width + x] = ColorTools.interpolate( ColorTools.interpolate(c2, c1, t), Color.BLACK, d * 2.0f/width - 0.4f  ).hashCode();
                                    } else {
                                        float t = (float)(NOISE-value) * 1.0f/value;
                                        pixels[y*width + x] = ColorTools.interpolate( ColorTools.interpolate(c3, c4, t), Color.BLACK, d * 2.0f/width - 0.4f ).hashCode();
                                    }
                                    
                                } else {
                                    pixels[y*width + x] = 0;
                                }
				
			}
		}
	}
}

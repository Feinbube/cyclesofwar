package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import cyclesofwar.noise.PerlinNoise;

public class Background {
	private enum NEBULA_MODE {
		NONE,
		FULL,
		PATCHES
	}
	
	private static final double  ZOOM          = 0.01;
	private static final int     SEED_CUT      = 16;
	private static final double  NEBULA_GRADE  = 2.0;
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

	private static final double MIN_BRIGHTNESS   = 0.5;
	private static final double MAX_BRIGHTNESS   = 5;
	private static final double BRIGHTNESS_RANGE = MAX_BRIGHTNESS - MIN_BRIGHTNESS;
	
	final private int           width;
	final private int           height;
	final private int           stars;
	final private Random        random;
	final private BufferedImage image;
	
	public Background(final int width, final int height) {
		this(width, height, 0);
	}
	
	public Background(final int width, final int height, final int seed) {
		this.width  = width;
		this.height = height;
		this.random = new Random(seed);
		this.stars  = MIN_STARS + random.nextInt(VAR_STARS);
		this.image  = this.generate();
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	private BufferedImage generate() {
		// a bit nasty to do the nebula and star generation in one loop
		// however due to performance reasons with image processing, we will do it anyway
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		// create outer space
		this.pitchBlack(image);
		
		// generate nebula (...maybe)
		NEBULA_MODE[] modes = NEBULA_MODE.values();
		NEBULA_MODE   mode  = modes[random.nextInt(modes.length)];
		
		switch (mode) {
		case NONE:
			break;
		case FULL:
			this.nebulaEverywhere(image, NEBULA_COLORS[random.nextInt(NEBULA_COLORS.length)]);
			break;
		case PATCHES:
			//TODO: this.nebula(pixels, );
			break;
		}
		
		// generate stars
		this.stars(image);
		
		return image;
	}
	
	private void pitchBlack(BufferedImage image) {
		Graphics2D canvas = image.createGraphics();
		canvas.setColor(Color.BLACK);
		canvas.fillRect(0, 0, this.width, this.height);
	}
	
	private void nebulaEverywhere(BufferedImage image, Color color) {
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		
		final int box  = Math.max(this.height, this.width) >> 1;
		final int xMid = this.width  >> 1;
		final int yMid = this.height >> 1;
		
		final int SEED = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);
		final int R = color.getRed();
		final int G = color.getGreen();
		final int B = color.getBlue();

		final int xStart = Math.max(xMid - box, 0);
		final int xEnd   = Math.min(xMid + box, this.width);
		final int yStart = Math.max(yMid - box, 0);
		final int yEnd   = Math.min(yMid + box, this.height);
		
		int i = yStart * this.width + xStart;
		
		for (int y = yStart, yNoise = SEED; y < yEnd ; ++y, ++yNoise) {
			for (int x = xStart, xNoise = SEED; x < xEnd; ++x, ++xNoise, ++i) {
				final double NOISE = PerlinNoise.noise(xNoise * ZOOM, yNoise * ZOOM);
				final double GRADE = Math.pow(NOISE, NEBULA_GRADE);
				
				pixels[i] = new Color((int)(R * GRADE), (int)(G * GRADE), (int)(B * GRADE)).hashCode();
			}
		}
	}
	
	private void stars(BufferedImage image) {
		Graphics2D canvas = image.createGraphics();
		
		for (int star = 0; star < this.stars; ++star) {
			final int x = this.random.nextInt(this.width);
			final int y = this.random.nextInt(this.height);
			
			final double brightness = MIN_BRIGHTNESS + BRIGHTNESS_RANGE * this.random.nextDouble();
			final int grey = (int)(255.0 / MAX_BRIGHTNESS * brightness);
			
			canvas.setColor(new Color(grey, grey, grey));
			canvas.fillOval(x, y, STAR_SIZE, STAR_SIZE);
		}
	}
}

package cyclesofwar.window.rendering.fractal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuliaFractal {

	static final int MAX_ITERATIONS = 15;

	Complex c;

	List<ColorAt> colors;

	final double x;
	final double y;
	final double z;

	public BufferedImage getImage(int w, int h) {

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		for (int yImage = 0; yImage < h; ++yImage) {
			for (int xImage = 0; xImage < w; ++xImage) {
				Color pixelColor = colorize(at(xImage * z + x, yImage * z + y));
				image.setRGB(xImage, yImage, pixelColor.hashCode());
			}
		}
		return image;
	}

	public JuliaFractal() {

		colors = new ArrayList<>();
		final int numColors = 30;

		final int mixR = 255 * 2;
		final int mixG = 190 * 2;
		final int mixB = 203 * 2;

		Random random = new Random(System.currentTimeMillis());
		colors = new ArrayList<>();

		for (int i = 0; i < numColors; ++i) {
			colors.add(new ColorAt(i / (double) numColors, new Color((random
					.nextInt(256) + mixR) / 3,
					(random.nextInt(256) + mixG) / 3,
					(random.nextInt(256) + mixB) / 3)));
		}

		x = -random.nextDouble();
		y = -random.nextDouble();
		z = 0.001; // (double)random.nextFloat() / 1000.f;

		c = new Complex(-random.nextDouble(), random.nextDouble());
	}

	public Color colorize(double raw) {
		return ColorAt.getColor(colors, raw);
	}

	public double at(double x, double y) {
		final int numIterations = juliaIterations(c, new Complex(x, y));
		return (double) numIterations / MAX_ITERATIONS;
	}

	static private int juliaIterations(Complex c, Complex z) {
		for (int t = 0; t < MAX_ITERATIONS; t++) {
			if (z.abs() > 2.0)
				return t;
			z = z.times(z).plus(c);
		}
		return MAX_ITERATIONS - 1;
	}
}

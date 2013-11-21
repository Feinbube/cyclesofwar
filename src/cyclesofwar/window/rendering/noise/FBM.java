package cyclesofwar.window.rendering.noise;

public final class FBM implements Noise {
	private double h;
	private double lacunarity;
	private int    octaves;
	private Noise  generator;
	
	public FBM() {
		this(1.0, 2.0, 4);
	}
	
	public FBM(double h, double lacunarity, int octaves) {
		this(h, lacunarity, octaves, new PerlinNoise());
	}
	
	public FBM(double h, double lacunarity, int octaves, Noise noise) {
		this.h          = h;
		this.lacunarity = lacunarity;
		this.octaves    = octaves;
		this.generator  = noise;
	}
	
	public double noise(double x, double y) {
		double value     = 0.0;
		double frequency = 1.0;

		// prevent cascades
		x += 371;
		y += 529;
		
		for (int i = 0; i < this.octaves; ++i) {
			final double exponent = Math.pow(frequency, -this.h);
			value += this.generator.noise(x, y) * exponent; 

			x         *= this.lacunarity;
			y 		  *= this.lacunarity;		
			frequency *= this.lacunarity;
		}
		
		return value;
	}
}

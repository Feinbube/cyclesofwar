package cyclesofwar.window.rendering.noise;

public final class VLNoise implements Noise {
	private double distortion;
	private PerlinNoise perlin;
	
	public VLNoise() {
		this(10.0, 0);
	}
	public VLNoise(double distortion) {
		this(distortion, 0);
	}
	public VLNoise(double distortion, int seed) {
		this.distortion = distortion;
		this.perlin = new PerlinNoise(seed);
	}
	
	public double noise(double x, double y) {
		final double dx = this.perlin.noise(x + 0.5, y) * this.distortion;
		final double dy = this.perlin.noise(x, y + 0.5) * this.distortion;
		
		return this.perlin.noise(x + dx, y + dy);
	}
}

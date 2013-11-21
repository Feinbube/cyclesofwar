package cyclesofwar.window.rendering.noise;

public final class RigidFBM implements Noise {
	private Noise generator;
	
	public RigidFBM() {
		this(new PerlinNoise());
	}
	public RigidFBM(Noise noise) {
		this.generator = noise;
	}
	
	public double noise(double x, double y) {
		return 1.0 - Math.abs(this.generator.noise(x, y));
	}
}

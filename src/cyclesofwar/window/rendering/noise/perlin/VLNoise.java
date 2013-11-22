package cyclesofwar.window.rendering.noise.perlin;

import cyclesofwar.window.rendering.noise.Noise;

public final class VLNoise extends Noise {

    private double distortion;
    private PerlinNoise perlin;

    public VLNoise(int seed) {
        this(seed, 10.0);
    }

    public VLNoise(int seed, double distortion) {
        super(seed);
        this.distortion = distortion;
        this.perlin = new PerlinNoise(seed);
    }

    @Override
    public double raw(double x, double y) {
        final double dx = this.perlin.raw(x + 0.5, y) * this.distortion;
        final double dy = this.perlin.raw(x, y + 0.5) * this.distortion;

        return this.perlin.raw(x + dx, y + dy);
    }
}

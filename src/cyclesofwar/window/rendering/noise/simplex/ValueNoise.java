package cyclesofwar.window.rendering.noise.simplex;

import cyclesofwar.window.rendering.noise.Noise;

public final class ValueNoise extends Noise {

    private double distortion;
    private Noise generator;

    public ValueNoise(int seed) {
        this(seed, 10.0);
    }

    public ValueNoise(int seed, double distortion) {
        super(seed);
        this.distortion = distortion;
        this.generator = new SimplexNoise(seed);
    }

    @Override
    public double raw(double x, double y) {
        final double dx = this.generator.raw(x + 0.5, y) * this.distortion;
        final double dy = this.generator.raw(x, y + 0.5) * this.distortion;

        return this.generator.raw(x + dx, y + dy);
    }
}

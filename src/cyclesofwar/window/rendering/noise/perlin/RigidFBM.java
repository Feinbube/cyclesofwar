package cyclesofwar.window.rendering.noise.perlin;

import cyclesofwar.window.rendering.noise.Noise;

public final class RigidFBM extends Noise {

    private Noise generator;

    public RigidFBM(int seed) {
        this(new PerlinNoise(seed), seed);
    }

    public RigidFBM(Noise noise, int seed) {
        super(seed);
        this.generator = noise;
    }

    @Override
    public double raw(double x, double y) {
        return 1.0 - Math.abs(this.generator.raw(x, y));
    }
}

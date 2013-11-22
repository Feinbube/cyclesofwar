package cyclesofwar.window.rendering.noise.simplex;

import cyclesofwar.window.rendering.noise.Noise;

public final class RigidFBMNoise extends Noise {

    private Noise generator;

    public RigidFBMNoise(int seed) {
        this(new SimplexNoise(seed), seed);
    }

    public RigidFBMNoise(Noise noise, int seed) {
        super(seed);
        this.generator = noise;
    }

    @Override
    public double raw(double x, double y) {
        return 1.0 - Math.abs(this.generator.raw(x, y));
    }
}

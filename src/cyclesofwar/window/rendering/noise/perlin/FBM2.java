package cyclesofwar.window.rendering.noise.perlin;

import cyclesofwar.window.rendering.noise.Noise;

public class FBM2 extends Noise {

    private static final int DEFAULT_OCTAVES = 8;
    private static final double DEFAULT_PERSISTANCE = 0.5;

    private Noise generator;
    private int octaves;
    private double persistance;

    public FBM2(int seed) {
        this(seed, DEFAULT_OCTAVES, DEFAULT_PERSISTANCE);
    }

    public FBM2(int seed, int octaves) {
        this(seed, octaves, DEFAULT_PERSISTANCE);
    }

    public FBM2(int seed, int octaves, double persistance) {
        this(seed, octaves, persistance, new PerlinNoise(seed));
    }

    public FBM2(int seed, int octaves, double persistance, Noise noise) {
        super(seed);
        this.generator = noise;
        this.octaves = octaves;
        this.persistance = persistance;
    }

    @Override
    public double raw(double x, double y) {
        double total = 0.0;

        double frequency = 1.0;
        double amplitude = this.persistance;

        for (int i = 0; i < this.octaves; ++i) {
            total += this.generator.raw(x * frequency, y * frequency) * amplitude;

            frequency *= 2.0;
            amplitude *= this.persistance;
        }

        return total;
    }
}

package cyclesofwar.window.rendering.noise.perlin;

import cyclesofwar.window.rendering.noise.Noise;

public final class FBM extends Noise {

    private double h;
    private double lacunarity;
    private int octaves;
    private Noise generator;

    public FBM(int seed) {
        this(1.0, 2.0, 4, seed);
    }

    public FBM(double h, double lacunarity, int octaves, int seed) {
        this(h, lacunarity, octaves, new PerlinNoise(), seed);
    }

    public FBM(double h, double lacunarity, int octaves, Noise noise, int seed) {
        super(seed);
        this.h = h;
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.generator = noise;
    }

    @Override
    public double raw(double x, double y) {
        double value = 0.0;
        double frequency = 1.0;

        // prevent cascades
        x += 371;
        y += 529;

        for (int i = 0; i < this.octaves; ++i) {
            final double exponent = Math.pow(frequency, -this.h);
            value += this.generator.raw(x, y) * exponent;

            x *= this.lacunarity;
            y *= this.lacunarity;
            frequency *= this.lacunarity;
        }

        return value;
    }
}

package cyclesofwar.window.rendering.noise.simplex;

import cyclesofwar.window.rendering.noise.Noise;

public class NebulaNoise extends Noise {

    Noise generator = null;
    
    public NebulaNoise(int seed) {
        super(seed);
        generator = new FractualBrownianMotionNoise(seed);
        this.zoom *= 0.3;
    }

    @Override
    public double raw(double x, double y) {
        return Math.pow(generator.raw(x, y), 2) * 0.6;
    }    
}

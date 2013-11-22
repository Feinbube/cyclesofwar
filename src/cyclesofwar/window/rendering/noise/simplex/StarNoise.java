package cyclesofwar.window.rendering.noise.simplex;

import cyclesofwar.window.rendering.noise.Noise;

public class StarNoise extends Noise {

    Noise generator = null;
    
    public StarNoise(int seed) {
        super(seed);
        generator = new FractualBrownianMotionNoise(seed);
        this.zoom *= 6;
    }

    @Override
    public double raw(double x, double y) {
        return (Math.pow(generator.raw(x, y), 4) * 2 - 0.8)*2;
    }    
}

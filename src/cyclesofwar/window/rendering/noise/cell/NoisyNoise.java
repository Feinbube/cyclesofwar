package cyclesofwar.window.rendering.noise.cell;

import cyclesofwar.window.rendering.textures.ColorTools;
import java.awt.Color;

public class NoisyNoise extends CellNoiseType {

    public NoisyNoise(int seed) {
        super(seed);
    }

    @Override
    public Color at(double x, double y, Color c1, Color c2) {
        noise(x, y);
        noise(zoom * this.cellDataStruct.F[0] * (x + seed), zoom * this.cellDataStruct.F[0] * (y + seed));

        return ColorTools.interpolate(c1, c2, this.cellDataStruct.F[1] * 0.5, (this.cellDataStruct.F[0] + this.cellDataStruct.F[1]) * 0.5, this.cellDataStruct.F[0] * 0.5, 1.0);
    }

    @Override
    protected double getNoise(double x, double y) {
        return cellDataStruct.F[0] + cellDataStruct.F[1];
    }
}

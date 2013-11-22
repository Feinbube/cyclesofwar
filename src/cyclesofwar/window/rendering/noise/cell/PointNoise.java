package cyclesofwar.window.rendering.noise.cell;

public class PointNoise extends CellNoiseType {

    public PointNoise(int seed) {
        super(seed);
        zoom = zoom / 2;
    }

    @Override
    protected double getNoise(double x, double y) {
        return 1.0 - this.cellDataStruct.F[0];
    }
}

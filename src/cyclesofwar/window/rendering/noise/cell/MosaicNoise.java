package cyclesofwar.window.rendering.noise.cell;

public class MosaicNoise extends CellNoiseType {

    @Override
    protected double getNoise(double x, double y) {
        return this.cellDataStruct.F[1] - this.cellDataStruct.F[0];
    }
}

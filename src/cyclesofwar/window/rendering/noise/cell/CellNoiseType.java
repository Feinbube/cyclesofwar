package cyclesofwar.window.rendering.noise.cell;

import cyclesofwar.window.rendering.noise.Noise;

public abstract class CellNoiseType extends Noise {

    CellNoiseImpl cellNoise = null;
    CellDataStruct cellDataStruct = null;

    public CellNoiseType(int seed) {
        super(seed);
        zoom = 5 * zoom;
    }

    @Override
    public double raw(double x, double y) {
        noise(x, y);
        return getNoise(x, y);
    }

    protected void noise(double x, double y) {
        if (cellNoise == null) {
            cellNoise = new CellNoiseImpl();
        }

        if (cellDataStruct == null) {
            cellDataStruct = new CellDataStruct(2, new double[]{0, 0}, CellNoiseImpl.EUCLIDEAN);
        }

        cellDataStruct.at = new double[]{x, y};
        cellNoise.noise(cellDataStruct);
    }

    protected abstract double getNoise(double x, double y);
}

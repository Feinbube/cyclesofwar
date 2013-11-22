package cyclesofwar.window.rendering.noise.cell;

public class CellNoise extends CellNoiseType {

    public CellNoise(int seed) {
        super(seed);
    }

    @Override
    protected double getNoise(double x, double y) {
        double sum = 1;

        for (int i = 0; i < 4; i++) {
            cellDataStruct.at[0] = 0.1 * (i * 2 + 1) * (x + 20);
            cellDataStruct.at[1] = 0.1 * (i * 2 + 1) * (y + 700);
            cellNoise.noise(cellDataStruct);
            sum *= cellDataStruct.F[0];
        }
        return sum / 5.0;
    }
}

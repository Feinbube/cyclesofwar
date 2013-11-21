package cyclesofwar.window.rendering.noise;

import java.util.Random;

public final class PerlinNoise implements Noise {
	private static final int BM = 0xFF;
	private static final int B  = 0x1000;
	private static final int N  = 0x1000;
	
	private final int[]      P;
	private final double[]   G1;
	private final double[][] G2;
	private final double[][] G3;
	
	public PerlinNoise() { 
		this(0);
	}

	public PerlinNoise(int seed) {
		P  = new int[B + B + 2];
        G1 = new double[B + B + 2];
        G2 = new double[B + B + 2][2];
        G3 = new double[B + B + 2][3];
        
		Random random = new Random(seed);
		int i, j, k;

		for (i = 0; i < B; i++) {
			P[i]  = i;
			G1[i] = (double)(random.nextInt(B + B) - B) / B;

			for (j = 0; j < 2; j++) {
				G2[i][j] = (double)(random.nextInt(B + B) - B) / B;
			}
			this.normalizeG2(G2[i]);

			for (j = 0; j < 3; j++) {
				G3[i][j] = (double)(random.nextInt(B + B) - B) / B;
			}
			normalizeG3(G3[i]);
		}
		
		while (--i > 0) {
			k    = P[i];
			j    = random.nextInt(B);
			P[i] = P[j];
			P[j] = k;
		}

		for (i = 0; i < B + 2; i++) {
			 P[B + i] = P[i];
			G1[B + i] = G1[i];
			
			for (j = 0; j < 2; j++) {
				G2[B + i][j] = G2[i][j];
			}
			for (j = 0; j < 3; j++) {
				G3[B + i][j] = G3[i][j];
			}
		}
    }
	
	public double noise(double x, double y) {
        final double tx  = x + N;
        final int    bx0 = ((int)tx) & BM;
        final int    bx1 = (bx0 + 1) & BM;
        final double rx0 = tx - (int)tx;
        final double rx1 = rx0 - 1;

        final double ty  = y + N;
        final int    by0 = ((int)ty) & BM;
        final int    by1 = (by0 + 1) & BM;
        final double ry0 = ty - (int)ty;
        final double ry1 = ry0 - 1;

        final int i = P[bx0];
        final int j = P[bx1];

        final int b00 = P[i + by0];
        final int b10 = P[j + by0];
        final int b01 = P[i + by1];
        final int b11 = P[j + by1];

        final double sx = rx0 * rx0 * (3.0 - 2.0 * rx0); //sCurve(rx0)
        final double sy = ry0 * ry0 * (3.0 - 2.0 * ry0); //sCurve(ry0)

        double[] q = G2[b00];
        double   u = rx0 * q[0] + ry0 * q[1];
        q = G2[b10];
        double v = rx1 * q[0] + ry0 * q[1];
        double a = u + sx * (v - u); //lerp(sx, u, v)

        q = G2[b01];
        u = rx0 * q[0] + ry1 * q[1];
        q = G2[b11];
        v = rx1 * q[0] + ry1 * q[1];
        double b = u + sx * (v - u); //lerp(sx, u, v)

        return a + sy * (b - a); //lerp(sy, a, b)
	}
	
	private void normalizeG2(double[] v) {
		final double v0 = v[0];
		final double v1 = v[1];
		final double s  = Math.sqrt(v0 * v0 + v1 * v1);
		
		v[0] = v0 / s;
		v[1] = v0 / s;
	}
	
	private void normalizeG3(double[] v) {
		final double v0 = v[0];
		final double v1 = v[1];
		final double v2 = v[2];
		final double s  = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		
		v[0] = v0 / s;
		v[1] = v1 / s;
		v[2] = v2 / s;
	}
}

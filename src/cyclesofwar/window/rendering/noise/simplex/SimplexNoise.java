package cyclesofwar.window.rendering.noise.simplex;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.Noise;

public final class SimplexNoise extends Noise {

    private static final double SQRT3 = Math.sqrt(3.0);

    private static final double SKEW = 0.5 * (SQRT3 - 1.0);
    private static final double UNSKEW = (3.0 - SQRT3) / 6.0;
    private static final double UNKSEW2 = UNSKEW * 2.0 - 1.0;

    private static final int[] PERMS = {
        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225,
        140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148,
        247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32,
        57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68,
        175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
        229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
        102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
        89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198,
        173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118,
        126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28,
        42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153,
        101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
        224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193,
        238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239,
        107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121,
        50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72,
        243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90,
        15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69,
        142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
        197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149,
        56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139,
        48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230,
        220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161,
        1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135,
        130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217,
        226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
        206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
        119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172,
        9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185,
        112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191,
        179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31,
        181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150,
        254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195,
        78, 66, 215, 61, 156, 180
    };

    private static final int[][] GRADIENTS = {
        {1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 0}, {-1, 0},
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {0, 1}, {0, -1}
    };
    private static final int WRAP = GRADIENTS.length;

    private static int floor(double n) {
        return n >= 0 ? (int) n : (int) n - 1;
    }

    private static double dot(int[] grid, double a, double b) {
        return grid[0] * a + grid[1] * b;
    }

    public SimplexNoise(int seed) {
        super(seed);
    }

    @Override
    public double raw(double x, double y) {
        final double HAIRY = (x + y) * SKEW;

        // indices into simplex cell space of skewed input 
        final int i = floor(x + HAIRY);
        final int j = floor(y + HAIRY);

        // first simplex corner
        final double ratio = (i + j) * UNSKEW;
        final double x_0 = x - (i - ratio);
        final double y_0 = y - (j - ratio);

        // second simplex corner
        int xOffset = 0;
        int yOffset = 0;
        if (x_0 > y_0) {
            xOffset = 1;
        } else {
            yOffset = 1;
        }

        final double x_1 = x_0 - xOffset + UNSKEW;
        final double y_1 = y_0 - yOffset + UNSKEW;

        // third simplex corner
        final double x_2 = x_0 + UNKSEW2;
        final double y_2 = y_0 + UNKSEW2;

        // compute the three hashed gradient of the simplex corners
        final int g_x = i & 0xFF;
        final int g_y = j & 0xFF;

		// compute the contributions of the corners to the noise
        //first corner
        double gradRatio_0 = 0.5 - x_0 * x_0 - y_0 * y_0;
        double n_0 = 0.0;

        if (gradRatio_0 > 0.0) {
            gradRatio_0 *= gradRatio_0;
            final int gI = PERMS[g_x + PERMS[g_y]] % WRAP;
            n_0 = gradRatio_0 * gradRatio_0 * dot(GRADIENTS[gI], x_0, y_0);
        }

        // second corner
        double gradRatio_1 = 0.5 - x_1 * x_1 - y_1 * y_1;
        double n_1 = 0.0;

        if (gradRatio_1 > 0.0) {
            gradRatio_1 *= gradRatio_1;
            final int gI = PERMS[g_x + xOffset + PERMS[g_y + yOffset]] % WRAP;
            n_1 = gradRatio_1 * gradRatio_1 * dot(GRADIENTS[gI], x_1, y_1);
        }

        // third corner
        double gradRatio_2 = 0.5 - x_2 * x_2 - y_2 * y_2;
        double n_2 = 0.0;

        if (gradRatio_2 > 0.0) {
            gradRatio_2 *= gradRatio_2;
            final int gI = PERMS[g_x + 1 + PERMS[g_y + 1]] % WRAP;
            n_2 = gradRatio_2 * gradRatio_2 * dot(GRADIENTS[gI], x_2, y_2);
        }

        return (70.0 * (n_0 + n_1 + n_2) + 1.0) / 2.0;
    }
}

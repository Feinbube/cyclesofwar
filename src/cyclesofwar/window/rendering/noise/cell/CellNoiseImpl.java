/** 
 Copyright 1994, 2002 by Steven Worley
 This software may be modified and redistributed without restriction
 provided this comment header remains intact in the source code.
 This code is provided with no warrantee, express or implied, for
 any purpose.
	
 A detailed description and application examples can be found in the
 1996 SIGGRAPH paper "A Cellular Texture Basis Function" and
 especially in the 2002 book "Texturing and Modeling, a Procedural
 Approach, 3rd edition." There is also extra information on the web
 site http://www.worley.com/cellular.html .
	
 If you do find interesting uses for this tool, and especially if
 you enhance it, please drop me an email at steve@worley.com.
	
 An implementation of the key cellular texturing basis
 function. This function is hardwired to return an average F_1 value
 of 1.0. It returns the <n> most closest feature point distances
 F_1, F_2, .. F_n the vector delta to those points, and a 32 bit
 seed for each of the feature points.  This function is not
 difficult to extend to compute alternative information such as
 higher order F values, to use the Manhattan distance metric, or
 other fun perversions.
	
 <at>    The input sample location. 
 <max_order>  Smaller values compute faster. < 5, read the book to extend it.
 <F>     The output values of F_1, F_2, ..F[n] in F[0], F[1], F[n-1]
 <delta> The output vector difference between the sample point and the n-th
 closest feature point. Thus, the feature point's location is the
 hit point minus this value. The DERIVATIVE of F is the unit
 normalized version of this vector.
 <ID>    The output 32 bit ID number which labels the feature point. This
 is useful for domain partitions, especially for coloring flagstone
 patterns.
			
 This implementation is tuned for speed in a way that any order > 5
 will likely have discontinuous artifacts in its computation of F5+.
 This can be fixed by increasing the internal points-per-cube
 density in the source code, at the expense of slower
 computation. The book lists the details of this tuning.
 */

/**
 * Edited by: Carl-Johan Rosén, Linköping University
 * Date: 2006-02-23
 * Contact: cj dot rosen at gmail dot com
 * 
 * This is the main class for generating cell noise. It uses the data 
 * structure CellDataStruct also in this package. The class implements 
 * one, two and three dimensional cell noise.
 */
// http://carljohanrosen.com/processing/images/
// http://carljohanrosen.com/processing/images/cellNoiseExample.pde
package cyclesofwar.window.rendering.noise.cell;

public class CellNoiseImpl {    

    /**
     * A hardwired lookup table to quickly determine how many feature points 
     * should be in each spatial cube. We use a table so we don't need to 
     * multiple slower tests. A random number indexed into this array will 
     * give an approximate Poisson distribution of mean density 2.5. Read 
     * the book for the long-winded explanation.
     */
    private final int[] Poisson_count = {
        4, 3, 1, 1, 1, 2, 4, 2, 2, 2, 5, 1, 0, 2, 1, 2, 2, 0, 4, 3, 2, 1, 2,
        1, 3, 2, 2, 4, 2, 2, 5, 1, 2, 3, 2, 2, 2, 2, 2, 3, 2, 4, 2, 5, 3, 2,
        2, 2, 5, 3, 3, 5, 2, 1, 3, 3, 4, 4, 2, 3, 0, 4, 2, 2, 2, 1, 3, 2, 2,
        2, 3, 3, 3, 1, 2, 0, 2, 1, 1, 2, 2, 2, 2, 5, 3, 2, 3, 2, 3, 2, 2, 1,
        0, 2, 1, 1, 2, 1, 2, 2, 1, 3, 4, 2, 2, 2, 5, 4, 2, 4, 2, 2, 5, 4, 3,
        2, 2, 5, 4, 3, 3, 3, 5, 2, 2, 2, 2, 2, 3, 1, 1, 5, 2, 1, 3, 3, 4, 3,
        2, 4, 3, 3, 3, 4, 5, 1, 4, 2, 4, 3, 1, 2, 3, 5, 3, 2, 1, 3, 1, 3, 3,
        3, 2, 3, 1, 5, 5, 4, 2, 2, 4, 1, 3, 4, 1, 5, 3, 3, 5, 3, 4, 3, 2, 2,
        1, 1, 1, 1, 1, 2, 4, 5, 4, 5, 4, 2, 1, 5, 1, 1, 2, 3, 3, 3, 2, 5, 2,
        3, 3, 2, 0, 2, 1, 1, 4, 2, 1, 3, 2, 1, 2, 2, 3, 2, 5, 5, 3, 4, 5, 5,
        2, 4, 4, 5, 3, 2, 2, 2, 1, 4, 2, 3, 3, 4, 2, 5, 4, 2, 4, 2, 2, 2, 4,
        5, 3, 2};

    /**
     * This constant is manipulated to make sure that the mean value of 
     * F[0] is 1.0. This makes an easy natural 'scale' size of the cellular 
     * features.
     *
     * Its inverse is also kept, to improve speed.
     */
    private double DENSITY_ADJUSTMENT, DENSITY_ADJUSTMENT_INV;

    /** This is the largest number possible to represent with a 32 bit 
     * unsigned integer. It's used in the overflow control function u32 
     * below.
     */
    private final long b32;

    /**
     * Distance measure type constants
     */
    public static final int EUCLIDEAN = 1;
    public static final int CITYBLOCK = 2;
    public static final int MANHATTAN = 3;
    public static final int QUADRATIC = 4;
    
    /**
     * Constructor.
     */
    public CellNoiseImpl() {
        DENSITY_ADJUSTMENT = 0.398150;
        DENSITY_ADJUSTMENT_INV = 1.0 / DENSITY_ADJUSTMENT;
        b32 = (long) Math.pow(2, 32);
    }

    public void noise(CellDataStruct cd) {
        if (cd.dim == 3) {
            /**
             * Adjustment variable to make F[0] average at 1.0 when using 
             * EUCLIDEAN distance in 3D.
             */
            DENSITY_ADJUSTMENT = 0.398150;
            DENSITY_ADJUSTMENT_INV = 1.0 / DENSITY_ADJUSTMENT;
            noise3D(cd);
        } else if (cd.dim == 2) {
            /**
             * Adjustment variable to make F[0] average at 1.0 when using 
             * EUCLIDEAN distance in 2D.
             */
            DENSITY_ADJUSTMENT = 0.294631;
            DENSITY_ADJUSTMENT_INV = 1.0 / DENSITY_ADJUSTMENT;
            noise2D(cd);
        }
    }

    /**
     * Noise function for three dimensions. Coordinating the search on the 
     * above cube level. Deciding in which cubes to search.
     */
    private void noise3D(CellDataStruct cd) {
        double x2, y2, z2, mx2, my2, mz2;
        double[] new_at = new double[3];
        int[] int_at = new int[3];
        int i;

        // initialize F
        for (i = 0; i < cd.max_order; i++) {
            cd.F[i] = 99999.9;
        }

        new_at[0] = DENSITY_ADJUSTMENT * cd.at[0];
        new_at[1] = DENSITY_ADJUSTMENT * cd.at[1];
        new_at[2] = DENSITY_ADJUSTMENT * cd.at[2];

        int_at[0] = (int) Math.floor(new_at[0]);
        int_at[1] = (int) Math.floor(new_at[1]);
        int_at[2] = (int) Math.floor(new_at[2]);

        /**
         * The center cube. It's very likely that the closest feature 
         * point will be found in this cube.
         */
        AddSamples(int_at[0], int_at[1], int_at[2], new_at, cd);

        /**
         * We test if the cubes are even possible contributors by examining 
         * the combinations of the sum of the squared distances from the 
         * cube's lower or upper corners.
         */
        x2 = new_at[0] - int_at[0];
        y2 = new_at[1] - int_at[1];
        z2 = new_at[2] - int_at[2];
        mx2 = (1.0 - x2) * (1.0 - x2);
        my2 = (1.0 - y2) * (1.0 - y2);
        mz2 = (1.0 - z2) * (1.0 - z2);
        x2 *= x2;
        y2 *= y2;
        z2 *= z2;

        /**
         * The 6 facing neighbours of center cube. These are the closest 
         * and most likely to have a close feature point.
         */
        if (x2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1], int_at[2], new_at, cd);
        }
        if (y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] - 1, int_at[2], new_at, cd);
        }
        if (z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1], int_at[2] - 1, new_at, cd);
        }
        if (mx2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1], int_at[2], new_at, cd);
        }
        if (my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] + 1, int_at[2], new_at, cd);
        }
        if (mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1], int_at[2] + 1, new_at, cd);
        }

        /**
         * The 12 edge cubes. These are next closest.
         */
        if (x2 + y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] - 1, int_at[2], new_at, cd);
        }
        if (x2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1], int_at[2] - 1, new_at, cd);
        }
        if (y2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] - 1, int_at[2] - 1, new_at, cd);
        }
        if (mx2 + my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] + 1, int_at[2], new_at, cd);
        }
        if (mx2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1], int_at[2] + 1, new_at, cd);
        }
        if (my2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] + 1, int_at[2] + 1, new_at, cd);
        }
        if (x2 + my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] + 1, int_at[2], new_at, cd);
        }
        if (x2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1], int_at[2] + 1, new_at, cd);
        }
        if (y2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] - 1, int_at[2] + 1, new_at, cd);
        }
        if (mx2 + y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] - 1, int_at[2], new_at, cd);
        }
        if (mx2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1], int_at[2] - 1, new_at, cd);
        }
        if (my2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] + 1, int_at[2] - 1, new_at, cd);
        }

        /**
         * The 8 corner cubes. 
         */
        if (x2 + y2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] - 1, int_at[2] - 1, new_at, cd);
        }
        if (x2 + y2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] - 1, int_at[2] + 1, new_at, cd);
        }
        if (x2 + my2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] + 1, int_at[2] - 1, new_at, cd);
        }
        if (x2 + my2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] + 1, int_at[2] + 1, new_at, cd);
        }
        if (mx2 + y2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] - 1, int_at[2] - 1, new_at, cd);
        }
        if (mx2 + y2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] - 1, int_at[2] + 1, new_at, cd);
        }
        if (mx2 + my2 + z2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] + 1, int_at[2] - 1, new_at, cd);
        }
        if (mx2 + my2 + mz2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] + 1, int_at[2] + 1, new_at, cd);
        }

        for (i = 0; i < cd.max_order; i++) {
            cd.F[i] = Math.sqrt(cd.F[i]) * DENSITY_ADJUSTMENT_INV;
            cd.delta[i][0] *= DENSITY_ADJUSTMENT_INV;
            cd.delta[i][1] *= DENSITY_ADJUSTMENT_INV;
            cd.delta[i][2] *= DENSITY_ADJUSTMENT_INV;
        }
    }

    /**
     * Noise function for two dimensions. Coordinating the search on the 
     * above square level. Deciding in which squares to search.
     */
    private void noise2D(CellDataStruct cd) {
        double x2, y2, mx2, my2;
        double[] new_at = new double[2];
        int[] int_at = new int[2];
        int i;

        // initialize F
        for (i = 0; i < cd.max_order; i++) {
            cd.F[i] = 99999.9;
        }

        new_at[0] = DENSITY_ADJUSTMENT * cd.at[0];
        new_at[1] = DENSITY_ADJUSTMENT * cd.at[1];

        int_at[0] = (int) Math.floor(new_at[0]);
        int_at[1] = (int) Math.floor(new_at[1]);

        /**
         * The center cube. It's very likely that the closest feature 
         * point will be found in this cube.
         */
        AddSamples(int_at[0], int_at[1], new_at, cd);

        /**
         * We test if the cubes are even possible contributors by examining 
         * the combinations of the sum of the squared distances from the 
         * cube's lower or upper corners.
         */
        x2 = new_at[0] - int_at[0];
        y2 = new_at[1] - int_at[1];
        mx2 = (1.0 - x2) * (1.0 - x2);
        my2 = (1.0 - y2) * (1.0 - y2);
        x2 *= x2;
        y2 *= y2;

        /**
         * The 4 facing neighbours of center square. These are the closest 
         * and most likely to have a close feature point.
         */
        if (x2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1], new_at, cd);
        }
        if (y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] - 1, new_at, cd);
        }
        if (mx2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1], new_at, cd);
        }
        if (my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0], int_at[1] + 1, new_at, cd);
        }

        /**
         * The 4 edge squares. These are next closest.
         */
        if (x2 + y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] - 1, new_at, cd);
        }
        if (mx2 + my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] + 1, new_at, cd);
        }
        if (x2 + my2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] - 1, int_at[1] + 1, new_at, cd);
        }
        if (mx2 + y2 < cd.F[cd.max_order - 1]) {
            AddSamples(int_at[0] + 1, int_at[1] - 1, new_at, cd);
        }

        for (i = 0; i < cd.max_order; i++) {
            cd.F[i] = Math.sqrt(cd.F[i]) * DENSITY_ADJUSTMENT_INV;
            cd.delta[i][0] *= DENSITY_ADJUSTMENT_INV;
            cd.delta[i][1] *= DENSITY_ADJUSTMENT_INV;
        }
    }

    /**
     * Generating the sample points in the grid
     * 3D
     */
    private void AddSamples(int xi, int yi, int zi, double[] at, CellDataStruct cd) {
        double dx, dy, dz, fx, fy, fz, d2;
        int count, i, j, index;
        long seed, this_id;

        /**
         * Generating a random seed, based on the cube's ID number. The seed might be 
         * better if it were a nonlinear hash like Perlin uses for noise, but we do very 
         * well with this faster simple one.
         * Our LCG uses Knuth-approved constants for maximal periods.
         */
        seed = u32(u32(702395077 * xi) + u32(915488749 * yi) + u32(2120969693 * zi));

        /** Number of feature points in this cube. */
        count = Poisson_count[(int) (0xFF & (seed >> 24))];

        /** Churn the seed with good Knuth LCG. */
        seed = u32(1402024253 * seed + 586950981);

        for (j = 0; j < count; j++) {
            this_id = seed;
            seed = u32(1402024253 * seed + 586950981);

            /** Compute the 0..1 feature point location's xyz. */
            fx = (seed + 0.5) / 4294967296.0;
            seed = u32(1402024253 * seed + 586950981);
            fy = (seed + 0.5) / 4294967296.0;
            seed = u32(1402024253 * seed + 586950981);
            fz = (seed + 0.5) / 4294967296.0;
            seed = u32(1402024253 * seed + 586950981);

            /** Delta from feature point to sample location. */
            dx = xi + fx - at[0];
            dy = yi + fy - at[1];
            dz = zi + fz - at[2];

            /**
             * Distance computation
             */
            if (cd.dist_type == CITYBLOCK) {
                d2 = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
                d2 *= d2;
            } else if (cd.dist_type == MANHATTAN) {
                d2 = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                d2 *= d2;
            } else if (cd.dist_type == QUADRATIC) {
                d2 = dx * dx + dy * dy + dz * dz + dx * dy + dx * dz + dy * dz;
                d2 *= d2;
            } else {
                // EUCLEDEAN
                d2 = dx * dx + dy * dy + dz * dz;
            }

            /** Store points that are close enough to remember. */
            if (d2 < cd.F[cd.max_order - 1]) {
                index = cd.max_order;
                while (index > 0 && d2 < cd.F[index - 1]) {
                    index--;
                }
                for (i = cd.max_order - 1; i-- > index;) {
                    cd.F[i + 1] = cd.F[i];
                    cd.ID[i + 1] = cd.ID[i];
                    cd.delta[i + 1][0] = cd.delta[i][0];
                    cd.delta[i + 1][1] = cd.delta[i][1];
                    cd.delta[i + 1][2] = cd.delta[i][2];
                }
                cd.F[index] = d2;
                cd.ID[index] = this_id;
                cd.delta[index][0] = dx;
                cd.delta[index][1] = dy;
                cd.delta[index][2] = dz;
            }
        }
    }

    /**
     * Generating the sample points in the grid
     * 2D
     */
    private void AddSamples(int xi, int yi, double[] at, CellDataStruct cd) {
        double dx, dy, fx, fy, d2;
        int count, i, j, index;
        long seed, this_id;

        /**
         * Generating a random seed, based on the cube's ID number. The seed might be 
         * better if it were a nonlinear hash like Perlin uses for noise, but we do very 
         * well with this faster simple one.
         * Our LCG uses Knuth-approved constants for maximal periods.
         */
        seed = u32(u32(702395077 * xi) + u32(915488749 * yi));

        /** Number of feature points in this cube. */
        count = Poisson_count[(int) (0xFF & (seed >> 24))];

        /** Churn the seed with good Knuth LCG. */
        seed = u32(1402024253 * seed + 586950981);

        /** Compute the 0..1 feature point location's xyz. */
        for (j = 0; j < count; j++) {
            this_id = seed;
            seed = u32(1402024253 * seed + 586950981);

            fx = (seed + 0.5) / 4294967296.0;
            seed = u32(1402024253 * seed + 586950981);
            fy = (seed + 0.5) / 4294967296.0;
            seed = u32(1402024253 * seed + 586950981);

            /** Delta from feature point to sample location. */
            dx = xi + fx - at[0];
            dy = yi + fy - at[1];

            /**
             * Calculate distance.
             */
            if (cd.dist_type == CITYBLOCK) {
                d2 = Math.max(Math.abs(dx), Math.abs(dy));
                d2 *= d2;
            } else if (cd.dist_type == MANHATTAN) {
                d2 = Math.abs(dx) + Math.abs(dy);
                d2 *= d2;
            } else if (cd.dist_type == QUADRATIC) {
                d2 = dx * dx + dy * dy + dx * dy;
                d2 *= d2;
            } else {
                // EUCLEDEAN
                d2 = dx * dx + dy * dy;
            }

            /** Store points that are close enough to remember. */
            if (d2 < cd.F[cd.max_order - 1]) {
                index = cd.max_order;
                while (index > 0 && d2 < cd.F[index - 1]) {
                    index--;
                }
                for (i = cd.max_order - 1; i-- > index;) {
                    cd.F[i + 1] = cd.F[i];
                    cd.ID[i + 1] = cd.ID[i];
                    cd.delta[i + 1][0] = cd.delta[i][0];
                    cd.delta[i + 1][1] = cd.delta[i][1];
                }
                cd.F[index] = d2;
                cd.ID[index] = this_id;
                cd.delta[index][0] = dx;
                cd.delta[index][1] = dy;
            }
        }
    }

    /**
     * This function implements the unsigned 32 bit integer overflow 
     * used in the original C++ code. The integer is represented by 
     * a 64 bit signed integer in Java since Java doesn't implement 
     * unsigned integers.
     *
     * This function slows the aplication down severely, but allowes 
     * us to keep the low repetative pattern generated by the constants
     * extracted by the original author, Steven Worley.
     */
    private long u32(long s) {
        s = s % b32;
        if (s < 0) {
            s += b32;
        }
        return s;
    }
}

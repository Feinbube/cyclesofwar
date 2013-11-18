package cyclesofwar.window.rendering.textures;

public final class PerlinNoise {
	/**
	 * Fast perlin noise generation
	 *
	 * Generate with a persistence of 0.5 and a maximum of 8 octaves
	 * 
	 */
	private static final int FREQUENCY      = 256;
	private static final int HALF_FREQUENCY = FREQUENCY / 2;
	private static final int RANDOM_1       = 1376312589;
	private static final int RANDOM_2       = 15731;
	private static final int RANDOM_3       = 789221;
	private static final int RANDOM_4       = 16384;
	private static final int MASK           = 0xFF;
	private static final int BICUBIC_FACTOR = 8;
	private static final int BICUBIC_1      = 3;
	private static final int BICUBIC_2      = 2;
	
	public static final double DEFAULT_BLUR    = 256.0;
	public static final double DEFAULT_DEGRADE = 1.0;
	public static final int    DEFAULT_OCTAVES = 7;
	
	public static double noise(final double x, final double y) {
		return noise(x, y, DEFAULT_OCTAVES);
	}
	
	public static double noise(final double x, final double y, final int octaves) {
		int result = 0;
		 
	    int sx = (int)((x) * FREQUENCY); 
	    int sy = (int)((y) * FREQUENCY);
	    
	    for (int octave = octaves; octave >= 0; --octave) {
	    	final int bX = sx & MASK;
	        final int bY = sy & MASK;

	        final int sxp = sx >> BICUBIC_FACTOR;
	        final int syp = sy >> BICUBIC_FACTOR;
	         
	        // compute noise for each corner of current cell
	        final int Y_00 = syp  * RANDOM_1;
	        final int Y_01 = Y_00 + RANDOM_1;

	        final int XY_00 = sxp + Y_00;
	        final int XY_10 = XY_00 + 1;
	        final int XY_01 = sxp + Y_01;
	        final int XY_11 = XY_01 + 1;

	        final int XYBASE_00 = (XY_00 << 13) ^ XY_00;
	        final int XYBASE_10 = (XY_10 << 13) ^ XY_10;
	        final int XYBASE_01 = (XY_01 << 13) ^ XY_01;
	        final int XYBASE_11 = (XY_11 << 13) ^ XY_11;

	        int alt1 = (XYBASE_00 * (XYBASE_00 * XYBASE_00 * RANDOM_2 + RANDOM_3) + RANDOM_1) ;
	        int alt2 = (XYBASE_10 * (XYBASE_10 * XYBASE_10 * RANDOM_2 + RANDOM_3) + RANDOM_1) ;
	        int alt3 = (XYBASE_01 * (XYBASE_01 * XYBASE_01 * RANDOM_2 + RANDOM_3) + RANDOM_1) ;
	        int alt4 = (XYBASE_11 * (XYBASE_11 * XYBASE_11 * RANDOM_2 + RANDOM_3) + RANDOM_1) ;
	         
	        // scalar product of the alt vectors to form the gradient
	        final int grad1X = (alt1 & MASK) - HALF_FREQUENCY;
	        final int grad1Y = ((alt1 >> BICUBIC_FACTOR) & MASK) - HALF_FREQUENCY;
	        final int grad2X = (alt2 & MASK) - HALF_FREQUENCY;
	        final int grad2Y = ((alt2 >> BICUBIC_FACTOR) & MASK) - HALF_FREQUENCY;
	        final int grad3X = (alt3 & MASK) - HALF_FREQUENCY;
	        final int grad3Y = ((alt3 >> BICUBIC_FACTOR) & MASK) - HALF_FREQUENCY;
	        final int grad4X = (alt4 & MASK) - HALF_FREQUENCY;
	        final int grad4Y = ((alt4 >> BICUBIC_FACTOR) & MASK) - HALF_FREQUENCY;
	          
	        final int sX1 = bX >> 1;
	        final int sY1 = bY >> 1;
	        final int sX2 = HALF_FREQUENCY - sX1;
	        final int sY2 = sY1;
	        final int sX3 = sX1;
	        final int sY3 = HALF_FREQUENCY - sY1;
	        final int sX4 = HALF_FREQUENCY - sX1;
	        final int sY4 = HALF_FREQUENCY - sY1;
	        
	        alt1 = (grad1X * sX1 + grad1Y * sY1) + RANDOM_4 + ((alt1 & 0xFF0000) >> 9);
        	alt2 = (grad2X * sX2 + grad2Y * sY2) + RANDOM_4 + ((alt2 & 0xFF0000) >> 9);
        	alt3 = (grad3X * sX3 + grad3Y * sY3) + RANDOM_4 + ((alt3 & 0xFF0000) >> 9);
        	alt4 = (grad4X * sX4 + grad4Y * sY4) + RANDOM_4 + ((alt4 & 0xFF0000) >> 9);
	         
	        // BiCubic interpolation
	        final int bX2   = (bX  * bX) >> BICUBIC_FACTOR;
	        final int bX3   = (bX2 * bX) >> BICUBIC_FACTOR;
	        final int _3bX2 = BICUBIC_1 * bX2;
	        final int _2bX3 = BICUBIC_2 * bX3;
	         
	        final int bY2   = (bY  * bY) >> BICUBIC_FACTOR;
	        final int bY3   = (bY2 * bY) >> BICUBIC_FACTOR;
	        final int _3bY2 = BICUBIC_1 * bY2;
	        final int _2bY3 = BICUBIC_2 * bY3;

	        final int alt12 = alt1 - (((_3bX2 - _2bX3) * (alt1 - alt2)) >> BICUBIC_FACTOR);
	        final int alt34 = alt3 - (((_3bX2 - _2bX3) * (alt3 - alt4)) >> BICUBIC_FACTOR);
	        int val = alt12 - (((_3bY2 - _2bY3) * (alt12 - alt34)) >> BICUBIC_FACTOR);
	         
	        val *= FREQUENCY;
	         
	        // Accumulate in result
	        result += (val << octave);
	        sx <<= 1;
	        sy <<= 1;
        }
	    
	    // normalize to [0, 1]
	    return (result >>> (16 + octaves + 1)) / 255.0;   
	}
}

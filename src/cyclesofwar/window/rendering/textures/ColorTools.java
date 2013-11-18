package cyclesofwar.window.rendering.textures;

import java.awt.Color;

public class ColorTools {
    
    // http://harmoniccode.blogspot.de/2011/04/bilinear-color-interpolation.html
    public static Color interpolate(Color c1, Color c2, float fraction) {
        final float INT_TO_FLOAT_CONST = 1f / 255f;
        fraction = Math.min(fraction, 1f);
        fraction = Math.max(fraction, 0f);

        final float r1 = c1.getRed() * INT_TO_FLOAT_CONST;
        final float g1 = c1.getGreen() * INT_TO_FLOAT_CONST;
        final float b1 = c1.getBlue() * INT_TO_FLOAT_CONST;
        final float a1 = c1.getAlpha() * INT_TO_FLOAT_CONST;

        final float r2 = c2.getRed() * INT_TO_FLOAT_CONST;
        final float g2 = c2.getGreen() * INT_TO_FLOAT_CONST;
        final float b2 = c2.getBlue() * INT_TO_FLOAT_CONST;
        final float a2 = c2.getAlpha() * INT_TO_FLOAT_CONST;

        final float d_r = r2 - r1;
        final float d_g = g2 - g1;
        final float d_b = b2 - b1;
        final float d_a = a2 - a1;

        float red = r1 + (d_r * fraction);
        float green = g1 + (d_g * fraction);
        float blue = b1 + (d_b * fraction);
        float alpha = a1 + (d_a * fraction);

        red = Math.min(red, 1f);
        red = Math.max(red, 0f);
        green = Math.min(green, 1f);
        green = Math.max(green, 0f);
        blue = Math.min(blue, 1f);
        blue = Math.max(blue, 0f);
        alpha = Math.min(alpha, 1f);
        alpha = Math.max(alpha, 0f);

        return new Color(red, green, blue, alpha);      
    }
}

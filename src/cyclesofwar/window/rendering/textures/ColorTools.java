package cyclesofwar.window.rendering.textures;

import java.awt.Color;

public class ColorTools {

    private static final double INT_TO_FLOAT_CONST = 1f / 255f;

    public static Color interpolate(Color c1, Color c2, double fraction) {
        return interpolate(c1, c2, fraction, fraction, fraction, fraction);
    }
    
    // http://harmoniccode.blogspot.de/2011/04/bilinear-color-interpolation.html
    public static Color interpolate(Color c1, Color c2, double fractionR, double fractionG, double fractionB, double fractionA) {

        fractionR = Math.min(fractionR, 1f);
        fractionR = Math.max(fractionR, 0f);
        fractionG = Math.min(fractionG, 1f);
        fractionG = Math.max(fractionG, 0f);
        fractionB = Math.min(fractionB, 1f);
        fractionB = Math.max(fractionB, 0f);
        fractionA = Math.min(fractionA, 1f);
        fractionA = Math.max(fractionA, 0f);

        final double r1 = c1.getRed() * INT_TO_FLOAT_CONST;
        final double g1 = c1.getGreen() * INT_TO_FLOAT_CONST;
        final double b1 = c1.getBlue() * INT_TO_FLOAT_CONST;
        final double a1 = c1.getAlpha() * INT_TO_FLOAT_CONST;

        final double r2 = c2.getRed() * INT_TO_FLOAT_CONST;
        final double g2 = c2.getGreen() * INT_TO_FLOAT_CONST;
        final double b2 = c2.getBlue() * INT_TO_FLOAT_CONST;
        final double a2 = c2.getAlpha() * INT_TO_FLOAT_CONST;

        final double d_r = r2 - r1;
        final double d_g = g2 - g1;
        final double d_b = b2 - b1;
        final double d_a = a2 - a1;

        double red = r1 + (d_r * fractionR);
        double green = g1 + (d_g * fractionG);
        double blue = b1 + (d_b * fractionB);
        double alpha = a1 + (d_a * fractionA);

        return newColor(red, green, blue, alpha);
    }

    public static Color transparent(Color c, double fraction) {
        final double r = c.getRed() * INT_TO_FLOAT_CONST;
        final double g = c.getGreen() * INT_TO_FLOAT_CONST;
        final double b = c.getBlue() * INT_TO_FLOAT_CONST;
        final double a = fraction;

        return newColor(r, g, b, a);
    }

    public static Color newColor(double r, double g, double b) {
        return newColor(r, g, b, 1.0);
    }

    public static Color newColor(double r, double g, double b, double a) {
        r = Math.min(r, 1f);
        r = Math.max(r, 0f);
        g = Math.min(g, 1f);
        g = Math.max(g, 0f);
        b = Math.min(b, 1f);
        b = Math.max(b, 0f);
        a = Math.min(a, 1f);
        a = Math.max(a, 0f);

        return new Color((float) r, (float) g, (float) b, (float) a);
    }
}

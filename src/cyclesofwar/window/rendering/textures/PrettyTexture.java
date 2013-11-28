package cyclesofwar.window.rendering.textures;

import java.awt.image.BufferedImage;

import cyclesofwar.window.rendering.fractal.JuliaFractal;
import java.awt.Color;

public class PrettyTexture extends Texture {

    public PrettyTexture(int width, int height, long seed) {
        super(width, height, seed);
    }

    @Override
    protected BufferedImage generate() {
        JuliaFractal julia = new JuliaFractal();
        return julia.getImage(width, height);
        // return this.fillColor(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), Color.PINK);
    }
}

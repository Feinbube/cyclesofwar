package cyclesofwar.window.rendering.textures;

import java.awt.image.BufferedImage;

import cyclesofwar.window.rendering.fractal.JuliaFractal;

public class PrettyTexture extends Texture {

	public PrettyTexture(int width, int height, long seed) {
		super(width, height, seed);
	}

	@Override
	protected BufferedImage generate() {
		JuliaFractal julia = new JuliaFractal();
		return julia.getImage(width, height);
	}
}
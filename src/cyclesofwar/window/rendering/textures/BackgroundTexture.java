package cyclesofwar.window.rendering.textures;

import cyclesofwar.window.rendering.noise.Noise;
import cyclesofwar.window.rendering.noise.simplex.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class BackgroundTexture extends Texture {

    boolean addStars;
    Noise generator;
            
    private static final int SEED_CUT = 16;
    private static final Color[] NEBULA_COLORS = {
        new Color(64, 64, 255),
        Color.CYAN,
        Color.RED,
        Color.GREEN,
        Color.MAGENTA
    };

    public BackgroundTexture(final Noise generator, final int width, final int height, final long seed, final boolean addStars) {
        super(width, height, seed);
        this.generator = generator;
        this.addStars = addStars;
    }
    
    @Override
    protected BufferedImage generate() {
        Color color = NEBULA_COLORS[random.nextInt(NEBULA_COLORS.length)];
        int seed = this.random.nextInt(Integer.MAX_VALUE >> SEED_CUT);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.pitchBlack(image);
        generator.setSeed(seed).texture(image, color);
        if(addStars) {
            new StarNoise(seed).blendTexture(image, Color.WHITE);
        }

        return image;
    }
}

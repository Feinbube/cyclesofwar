package cyclesofwar.window.rendering;

import cyclesofwar.window.rendering.noise.cell.*;
import cyclesofwar.window.rendering.noise.simplex.*;
import cyclesofwar.window.rendering.textures.BackgroundTexture;
import cyclesofwar.window.rendering.textures.Texture;

public class CloudRendering extends FancyRendering {
    
    @Override
    protected Texture createBackground(long universeSeed) {
        return new BackgroundTexture(new FractualBrownianMotionNoise(0, 8, 0.5, new PointNoise()), size.width, size.height, universeSeed, false);
    }
}

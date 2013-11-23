package cyclesofwar.window.rendering;

import cyclesofwar.window.rendering.textures.GalaxyTexture;
import cyclesofwar.window.rendering.textures.Texture;

public class GalaxyRendering extends FancyRendering {
    
    @Override
    protected Texture createBackground(long universeSeed) {
        return new GalaxyTexture(size.width, size.height, universeSeed);
    }
}

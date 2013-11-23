package cyclesofwar.window.rendering;

import cyclesofwar.window.rendering.noise.cell.CellNoise;
import cyclesofwar.window.rendering.textures.BackgroundTexture;
import cyclesofwar.window.rendering.textures.Texture;

public class OrganicRendering extends FancyRendering {
    
    @Override
    protected Texture createBackground(long universeSeed) {
        return new BackgroundTexture(new CellNoise(), size.width, size.height, universeSeed, false);
    }
}

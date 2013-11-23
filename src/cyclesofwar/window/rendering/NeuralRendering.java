package cyclesofwar.window.rendering;

import cyclesofwar.window.rendering.noise.cell.MosaicNoise;
import cyclesofwar.window.rendering.textures.BackgroundTexture;
import cyclesofwar.window.rendering.textures.Texture;

public class NeuralRendering extends FancyRendering {
    
    @Override
    protected Texture createBackground(long universeSeed) {
        return new BackgroundTexture(new MosaicNoise(), size.width, size.height, universeSeed, false);
    }
}

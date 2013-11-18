package cyclesofwar.window.rendering.textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class Texture {
	
	final protected int           width;
	final protected int           height;
        
	final protected Random        random;
        
	protected BufferedImage image = null;
	
	public Texture(final int width, final int height, final long seed) {
		this.width  = width;
		this.height = height;
		this.random = new Random(seed);
	}
	
	public BufferedImage getImage() {
            if( this.image == null) {
                this.image  = this.generate();
            }
                
            return this.image;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
        
	protected abstract BufferedImage generate();
        
        protected void pitchBlack(BufferedImage image) {
		Graphics2D canvas = image.createGraphics();
		canvas.setColor(Color.BLACK);
		canvas.fillRect(0, 0, this.width, this.height);
	}
}

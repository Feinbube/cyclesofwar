package cyclesofwar;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;



class Star extends Drawable{
	
	Star(Random random, Dimension size) {
		super();
		
		x = random.nextInt(size.width);
		y = random.nextInt(size.height);
		c = Color.getHSBColor(0, 0, (float)random.nextDouble());
		d = random.nextInt(3) + 1;
	}
}
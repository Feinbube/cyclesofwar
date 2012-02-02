package cyclesofwar;
import java.awt.Color;
import java.util.Random;

class Star {
	
	double size;
	double x;
	double y;
	Color c;
	double d;
	
	Star(Random random, double size) {
		super();
		
		this.size = size;
		x = random.nextDouble() * size;
		y = random.nextDouble() * size; 
		c = Color.getHSBColor(0, 0, (float)random.nextDouble());
		d = random.nextDouble();
	}
}
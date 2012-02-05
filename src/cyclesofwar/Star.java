package cyclesofwar;
import java.awt.Color;
import java.util.Random;

class Star {
	
	double x;
	double y;
	Color c;
	double d;
	
	Star(Random random) {
		super();
		
		x = random.nextDouble();
		y = random.nextDouble(); 
		c = Color.getHSBColor(0, 0, (float)random.nextDouble());
		d = random.nextDouble();
	}
}
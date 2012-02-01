import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;


class Planet extends Drawable implements Updatable {
	Player player;
	double forces;
	
	double productionRatePerSecond;
	
	public Planet(Random random, Dimension size, int d) {
		this.player = Player.IdlePlayer;
		
		if(d <= 0) {
			d = random.nextInt(3) + 1;
		}
		
		productionRatePerSecond = d;
		forces = d * 10;
		
		d = d * 15;
		
		x = random.nextInt(size.width-d) + d/2;
		y = random.nextInt(size.height-d) + d/2;
		
		this.d = d;
	}
	
	public Planet(Random random, Dimension size) {
		this(random, size, -1);
	}
	
	public void draw(Graphics g) {

		super.c = player.color;
		
		super.draw(g);
		
		drawText(g, ((int)forces) + "");
	}

	public boolean collidesWith(Planet other) {
		double xDiff = this.x-other.x;
		double yDiff = this.y-other.y;
		double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		if(distance <= this.d/2 + other.d/2)
			return true;
		else
			return false;
	}

	@Override
	public void update(double elapsedSeconds) {
		if(!player.equals(Player.IdlePlayer))
			forces += productionRatePerSecond * elapsedSeconds;
	}
}
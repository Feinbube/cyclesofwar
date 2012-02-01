import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class Fleet implements Updatable{

	final double speed = 100;
	
	Player player;
	int force;
	
	Planet target;
	
	double x;
	double y;
	
	public Fleet(Player player, int force, Planet start, Planet target){
		this.player = player;
		this.force = force;
		
		this.target = target;
		
		x = start.x;
		y = start.y;
	}
	
	@Override
	public void update(double elapsedSeconds) {
		double xDiff = x - target.x;
		double yDiff = y - target.y;
		
		double sqrt = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		if(sqrt < speed * elapsedSeconds)
			hit();
		
		xDiff = xDiff / sqrt;
		yDiff = yDiff / sqrt;
		
		x -= speed * elapsedSeconds * xDiff;
		y -= speed * elapsedSeconds * yDiff;
	}
	
	private void hit() {
		Universe.INSTANCE.FleetArrived(this);
	}

	public void draw(Graphics g)
	{
		g.setColor(player.color);
	
    	g.fillRect((int)(x - force /2.0),(int)(y - force /2.0),force,force);
    	drawText(g, force + "");
	}
	
	protected void drawText(Graphics g, String s) {
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		
		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();
		
		g.drawString(s, (int)(x-w/2), (int)(y+h/2));
	}

	public void land() {
		if(target.player.equals(player))
			target.forces += force;
		else
			target.forces -= force;
		
		if(target.forces < 0)
		{
			target.forces = -target.forces;
			target.player = player;
		}
	}
}

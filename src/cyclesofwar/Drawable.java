package cyclesofwar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


class Drawable {
	int x;
	int y;
	Color c;
	int d;

	protected void draw(Graphics g)
	{
		g.setColor(c);
    	g.fillOval(x-d/2, y-d/2, d, d);
	}
	
	protected void drawText(Graphics g, String s, Color color) {
		g.setColor(color);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		
		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();
		
		g.drawString(s, x-w/2, y+h/2);
	}
}
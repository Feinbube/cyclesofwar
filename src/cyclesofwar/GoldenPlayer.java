package cyclesofwar;
import java.awt.Color;

/*
 * this player is used for a real human who wants to play the game's interactive mode
 */
public class GoldenPlayer extends Player {

	@Override
	public void think() {
		// do nothing
	}

	@Override
	public Color getPlayerBackColor() {
		return new Color(230, 190, 138);
	}

	@Override
	public Color getPlayerForeColor() {
		return new Color(131, 137, 150);
	}

	@Override
	public String getCreatorsName() {
		return null;
	}
	
	@Override
	public String getName(){
		return "Player";
	}
}

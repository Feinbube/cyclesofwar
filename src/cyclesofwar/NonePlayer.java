package cyclesofwar;
import java.awt.Color;

/*
 * this player means "no player". it is the Null-Object that is assigned to empty planets 
 */
public class NonePlayer extends Player {

	@Override
	public void think() {
		// do nothing
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.white;
	}

	@Override
	public String getCreatorsName() {
		return "Frank";
	}
	
	@Override
	public String getName(){
		return "Nobody";
	}
}

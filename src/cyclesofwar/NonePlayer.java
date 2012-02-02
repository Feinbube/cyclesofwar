package cyclesofwar;
import java.awt.Color;



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

}

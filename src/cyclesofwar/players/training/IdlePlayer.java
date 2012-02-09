package cyclesofwar.players.training;
import java.awt.Color;

import cyclesofwar.Player;


public class IdlePlayer extends Player {

	@Override
	public void think() {
		// do nothing
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.yellow;
	}

	@Override
	public String getCreatorsName() {
		return "Training";
	}

}

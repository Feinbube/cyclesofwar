package cyclesofwar.players.training;
import java.awt.Color;

import cyclesofwar.Player;

/**
 * This bot does nothing at all. 
 * Easy prey ;)
 */
public class B00_Idle extends Player {

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
		return null;
	}
}

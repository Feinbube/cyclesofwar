package cyclesofwar.players.robert;

import java.awt.Color;

public abstract class Player extends cyclesofwar.Player {

	public Player() {
		super();
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.red.darker().darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.orange.brighter();
	}

	@Override
	public String getCreatorsName() {
		return "Robert";
	}

}
package cyclesofwar.players.frank;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Player;

public class Ender extends Player {

	List<Fleet> fleetsHandled = new ArrayList<Fleet>();
	
	@Override
	public void think() {
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.blue;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.black;
	}

	@Override
	public String getCreatorsName() {
		return "Frank";
	}

}

package cyclesofwar.console;

import cyclesofwar.Player;
import cyclesofwar.tournament.Tournament;

public class Console {
	protected void printPlayer(int place, Tournament tournament, Player player) {
		System.out.println(place + "," + player.getName() + "," + tournament.wonBy(player).size() + ","
				+ tournament.participatedIn(player).size());
	}
}
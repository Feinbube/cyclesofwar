package cyclesofwar.console;

import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.tournament.Tournament;

public class Console {
	protected void printPlayer(int place, Tournament tournament, Player player) {
		System.out.println(place + "," + player.getName() + "," + tournament.wonBy(player).size() + ","
				+ tournament.participatedIn(player).size());
	}

	protected void printDisqualifiedPlayers(List<Player> disqualifiedPlayers) {
		for (Player player : disqualifiedPlayers) {
			System.out.println("0," + player.getName() + " [DISQUALIFIED],0,0");
		}
	}
}
package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.*;

public class Arena {
	public static final int PlanetCount = 20;
	
	public static List<Player> Combatants(){
		ArrayList<Player> result = new ArrayList<Player>();
		
//		result.add(new Defender());
		result.add(new Petra());
		
		// result.add(new IdlePlayer());
		// result.add(new RandomPlayer());
		result.add(new FastExpand());
		
		return result;
	}

}

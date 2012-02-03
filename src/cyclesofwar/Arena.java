package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.*;

public class Arena {
	public static final int PlanetCountPerPlayer = 10;
	
	public static List<Player> Combatants(){
		ArrayList<Player> result = new ArrayList<Player>();
		
		//result.add(new IdlePlayer());
		//result.add(new RandomPlayer());
		result.add(new Defender());
		//result.add(new Petra());
		result.add(new HanTzu());
		//result.add(new FastExpand());
//		result.add(new AttackLargestPlayer());
		
		return result;
	}
}

package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.training.*;
import de.loewis.*;

public class Arena {
	public static final int speedUp = 5;
	public static final int PlanetCountPerPlayer = 10;
	
	public static List<Player> Combatants(){
		ArrayList<Player> result = new ArrayList<Player>();
		
		// Basic
		result.add(new IdlePlayer());
		result.add(new RandomPlayer());
		result.add(new SharpShooter());
		
		// Martin
		result.add(new Cratters());
		
		// Robert
		result.add(new Defender());		
		result.add(new FastExpand());
		result.add(new AttackLargestPlayer());
		// result.add(new Borg());
		
		// Frank
		//result.add(new Petra());
		//result.add(new HanTzu());
		//result.add(new Alai());
		result.add(new Bean());
		
		return result;
	}
}

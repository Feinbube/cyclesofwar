package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.training.*;
import de.loewis.*;

public class Arena {
	static double speedUp = 2;
	static final int PlanetCountPerPlayer = 10;
	
	static final int matchesPerPairing = 25;
	
	public static List<Player> playersForGameMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new Bean());
		result.add(new Cratters());
		result.add(new AttackLargestPlayer());
		
		return result;
	}
	
	public static List<Player> playersForArenaMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new Bean());
		result.add(new Cratters());
		result.add(new AttackLargestPlayer());
		
		return result;
	}
	
	public static List<Player> registeredPlayers(){
		ArrayList<Player> result = new ArrayList<Player>();
		
		// Training
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
		result.add(new Petra());
		result.add(new HanTzu());
		result.add(new Alai());
		result.add(new Bean());
		
		return result;
	}
}

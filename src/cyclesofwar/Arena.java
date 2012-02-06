package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.theo.*;
import cyclesofwar.players.training.*;
import de.loewis.*;

public class Arena {
	
	public static final int matchesInArena = 50;
	public static final int matchesInTournament = 3;
	
	public static List<Player> playersForGameMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld());
		result.add(new Cratters());
		result.add(new CrazyTom());
		
		return result;
	}
	
	public static List<Player> playersForArenaMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld());
		result.add(new Cratters());
		result.add(new CrazyTom());
		
		return result;
	}
	
	public static List<Player> champions(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld()); // Theo
		result.add(new CrazyTom()); // Frank
		result.add(new Cratters()); // Martin
		result.add(new AttackLargestPlayer()); // Robert
		
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
		
		// Theo
		result.add(new HelloWorld());
		
		// Frank
		result.add(new Petra());
		result.add(new HanTzu());
		result.add(new CrazyTom());
		result.add(new Alai());
		result.add(new Bean());
		
		return result;
	}
}

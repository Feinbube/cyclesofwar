package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.theo.HelloWorld;
import cyclesofwar.players.training.*;
import de.loewis.*;

public class Arena {
	static double speedUp = 2;
	static final int PlanetCountPerPlayer = 10;
	
	static final int matchesPerPairing = 50; // should be a multiple of 2
	
	public static List<Player> playersForGameMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld());
		result.add(new CrazyTom());
		result.add(new Cratters());
		
		return result;
	}
	
	public static List<Player> playersForArenaMode(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld());
		result.add(new CrazyTom());
		result.add(new Cratters());
		
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

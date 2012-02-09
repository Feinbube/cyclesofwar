package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.peter.*;
import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.theo.*;
import cyclesofwar.players.training.*;
import de.loewis.*;

/*
 * here new players are registered and tournaments are configured
 */
public class Arena {
	
	/*
	 * the number of matches per round for the daily tournament
	 */
	public static final int matchesInTournament = 100;
	
	/*
	 * please register your champion here for the daily tournament
	 */
	public static List<Player> champions(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new HelloWorld()); // Theo
		result.add(new Bean()); // Frank
		result.add(new Cratters()); // Martin
		result.add(new AttackLargestPlayer()); // Robert
		result.add(new ViralForces()); // Peter		
		
		return result;
	}
	
	/*
	 * this list contains all known players. please register yours as well 
	 */
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
		result.add(new Borg());
		
		// Theo
		result.add(new HelloWorld());
		
		// Frank
		result.add(new Petra());
		result.add(new HanTzu());
		result.add(new CrazyTom());
		result.add(new Alai());
		result.add(new Bean());

		// Peter
		result.add(new DumbVirus());
		result.add(new ViralForces());

		
		return result;
	}
}

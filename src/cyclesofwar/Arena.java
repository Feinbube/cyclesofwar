package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.peter.*;
import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.jan.FriendlyPirates;
import cyclesofwar.players.theo.*;
import cyclesofwar.players.training.*;
import de.loewis.*;

/*
 * here new players are registered and tournaments are configured
 */
public class Arena {
	
	/*
	 * seed for the random generators
	 */
	public static final long tournamentSeed = 3141592;
	
	/*
	 * the number of matches per round for the daily tournament
	 */
	public static final int matchesInLastManStandingTournamentPerRound = 100;
	
	/*
	 * the number of matches per player for the daily tournament
	 */
	public static final int matchesInOneOnOneTournamentPerPlayer = 100;
	
	/*
	 * please register your champion here for the daily tournament
	 */
	public static List<Player> champions(){
		ArrayList<Player> result = new ArrayList<Player>();

		result.add(new SpaceMenace()); // Theo
		result.add(new HanTzu()); // Frank
		result.add(new Cratters()); // Martin
		result.add(new AttackLargestPlayer()); // Robert
		result.add(new DumbVirus()); // Peter	
		result.add(new FriendlyPirates()); // Jan
		
		return result;
	}
	
	/*
	 * this list contains all known players. please register yours as well 
	 */
	public static List<Player> registeredPlayers(){
		ArrayList<Player> result = new ArrayList<Player>();
		
		// Training
		result.add(new Idle());
		result.add(new Sniper());
		result.add(new Fair());
		result.add(new Fugitive());
		result.add(new Random());
		result.add(new Gunner());
		result.add(new Clone());
		result.add(new Traveller());
		result.add(new Collective());
		result.add(new Rabbit());
		result.add(new Borderline());
		
		// Martin
		result.add(new Cratters());
		
		// Robert
		result.add(new AttackLargestPlayer());
		result.add(new Defender());		
		result.add(new FastExpand());
		// result.add(new Borg());
		
		// Theo
		result.add(new HelloWorld());
		result.add(new SpaceMenace());
		
		// Frank
		result.add(new Alai());
		result.add(new Bean());
		result.add(new CrazyTom());
		result.add(new Petra());
		result.add(new HanTzu());

		// Peter
		result.add(new DumbVirus());
		result.add(new ViralForces());
		
		// Jan
		result.add(new FriendlyPirates());

		
		return result;
	}
}

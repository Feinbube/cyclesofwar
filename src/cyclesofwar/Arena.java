package cyclesofwar;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.players.peter.*;
import cyclesofwar.players.robert.*;
import cyclesofwar.players.frank.*;
import cyclesofwar.players.frank.gene.training.*;
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
	 * the number of matches per round for the daily last-man-standing tournament
	 */
	public static final int matchesInLastManStandingTournamentPerRound = 100;
	
	/*
	 * the number of matches per player for the daily one-on-one tournament
	 */
	public static final int matchesInOneOnOneTournamentPerPlayer = 100;
        
        /*
	 * the number of planets per player for the daily tournaments
	 */
        public static final int planetsPerPlayer = 10;
        
        /*
	 * the universe size factor for the daily tournaments
	 */
        public static final double universeSizeFactor = 1.0;
	
	/*
	 * please register your champion here for the daily tournament
	 */
	public static List<Player> champions(){
		ArrayList<Player> result = new ArrayList<>();

		result.add(new AttackLargestPlayer()); // Robert
                result.add(new DumbVirus()); // Peter	
                result.add(new Jane()); // Frank
                result.add(new FriendlyPirates()); // Jan
		result.add(new Cratters()); // Martin
		result.add(new SpaceMenace()); // Theo

		return result;
	}
	
	/*
	 * this list contains all known players. please register yours as well 
	 */
	public static List<Player> registeredPlayers(){
		ArrayList<Player> result = new ArrayList<>();
		
		// Training
		result.add(new B00_Idle());
		result.add(new B01_ChaseMe());
		result.add(new B02_Wave());
		result.add(new B03_BigGun());
		result.add(new B04_Random());
                result.add(new B05_Worm());
		result.add(new B06_PowersOf2());
		result.add(new B07_Sniper());
		result.add(new B08_NewLeader());
                result.add(new B09_CloseBond());
		result.add(new B10_Front());
		result.add(new B11_Cells());       
                result.add(new B12_Closest());                
		result.add(new B13_Rabbit());
                result.add(new B14_BraveRabbit());

		
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
		result.add(new Jane());
                
                // Gene
                result.add(new G02_Wave());
                result.add(new G04_Random());

		// Peter
		result.add(new DumbVirus());
		result.add(new ViralForces());
		
		// Jan
		result.add(new FriendlyPirates());

		return result;
	}
}

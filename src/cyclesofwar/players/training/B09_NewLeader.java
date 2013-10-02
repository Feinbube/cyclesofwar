package cyclesofwar.players.training;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;

/*
 * This bot is very similar to the worm bot. It's winding through the galaxy as well,
 * but the tail planets also attack the target of the head instead of following the rest of the body.
 */
public class B09_NewLeader extends Player {
	
	// List of the planets the bot owns. 0 being the head.
	// It is important to keep our own list, the universe doesn't order planets the way we acquire them. ;)
	List<Planet> myPlanets = null;
	
	@Override
	public void think() {	
		
		// if this is the first round, I just need to ask the universe for my planets
		if(myPlanets == null){
			myPlanets = this.getPlanets();
		} else {
			// for every other round, I have to update my local list update the planets I lost or won
			checkPlanets();
		}
		
		// for each of my planets
		for(Planet planet : myPlanets) {
			
			// if a planet has enough forces to attack
			if(planet.getForces() > 20) {
				
				// attack the hostile planet that is closest to the front planet
				sendFleetUpTo(planet, 10, firstOrNull(hostileOnly(myPlanets.get(0).getOthersByDistance())));
			}
		}
	}

	// update the local list of planets: remove planets I lost, add planets I won
	// the order of the planets remains the same
	private void checkPlanets() {

		// we work with a local result list, because it is not 
		// nice to remove items from a list we are iterating over
		List<Planet> newMyPlanets = new ArrayList<Planet>();
		
		// only add planets to the result that still belong to me
		for(Planet planet : myPlanets) {
			if(this.getPlanets().contains(planet))
				newMyPlanets.add(planet);
		}
		
		// add planets I won (due to the nature of this algorithm, it can only be one planet at a time anyway)
		for(Planet planet : this.getPlanets()) {
			if(!newMyPlanets.contains(planet))
				// insert the new planets at the front -> they become the new head 
				newMyPlanets.add(0, planet);
		}
		
		// update myPlanets
		myPlanets = newMyPlanets;
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.gray.darker();
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.yellow;
	}

	@Override
	public String getCreatorsName() {
		return "B";
	}
}

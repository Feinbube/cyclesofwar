package cyclesofwar.players.jan;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class FriendlyPirates extends Player {
	private static final int MAXIMUM_SELF_DEFENSE = 1000; //effectively disabled
	private static final int MINIMUM_FLEET_SIZE = 10;
	
	private static final int DEFENSE_FORCE_MARGIN = 1;
	private static final int DEFENSE_FORCE_HELP_MARGIN = 5;

	@Override
	protected void think() {
		for(Planet p : getPlanetsOf(this))
			planetaryThink(p);
	}

	private void planetaryThink(Planet currentPlanet) {
		int minimumDefensiveForces = calculateDefense(currentPlanet);
		defendAllies(currentPlanet, minimumDefensiveForces);
		performAttack(currentPlanet, minimumDefensiveForces);
	}

	private int calculateDefense(Planet currentPlanet) {
		int attackingForces = getAttackingForces(currentPlanet);
		return Math.min(MAXIMUM_SELF_DEFENSE, attackingForces + DEFENSE_FORCE_MARGIN);
	}
	
	private int getAttackingForces(Planet p) {
		int incomingForces = 0;
		for(Fleet f : getFleetsWithTarget(p))
			if(!f.getPlayer().equals(p.getPlayer()))
				incomingForces += f.getForce();
		return incomingForces;
	}
	
	private int getDefendingForces(Planet p) {
		int incomingForces = 0;
		for(Fleet f : getFleetsWithTarget(p))
			if(f.getPlayer().equals(p.getPlayer()))
				incomingForces += f.getForce();
		return incomingForces;
	}
	
	private void defendAllies(Planet currentPlanet, int minimumSelfDefense) {
		int availableForces = (int)currentPlanet.getForces() - minimumSelfDefense;
		if(availableForces <= MINIMUM_FLEET_SIZE)
			return;
		
		for(Planet ally : currentPlanet.getOthersByDistance()) {
			if(!ally.getPlayer().equals(this))
				continue;
			
			int neededDefense = getAttackingForces(ally) - ((int)ally.getForces() + getDefendingForces(ally));
			if(neededDefense > 0) {
				neededDefense += DEFENSE_FORCE_HELP_MARGIN;
				int forcesSent = Math.min(neededDefense, availableForces);
				sendFleetUpTo(currentPlanet, forcesSent, ally);
				return; //defend only one ally
			}
		}
	}

	private void performAttack(Planet currentPlanet, int minimumSelfDefense) {
		int availableForces = (int)currentPlanet.getForces() - minimumSelfDefense;
		if(availableForces <= MINIMUM_FLEET_SIZE)
			return;
		
		for(Planet target : currentPlanet.getOthersByDistance()) {
			if(target.getPlayer().equals(this))
				continue;
						
			sendFleetUpTo(currentPlanet, availableForces, target);
			return;
		}
	}

	@Override
	public Color getPlayerBackColor() {
		return Color.orange;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.black;
	}

	@Override
	public String getCreatorsName() {
		return "Jan";
	}

}

package cyclesofwar.players.jan;

import java.awt.Color;

import cyclesofwar.Fleet;
import cyclesofwar.Fleet.Formation;
import cyclesofwar.Planet;
import cyclesofwar.Player;

public class FriendlyPirates extends Player {
	private static final int MAXIMUM_SELF_DEFENSE = 1000; //effectively disabled
	private static final int MINIMUM_FLEET_SIZE = 5;
	
	private static final int DEFENSE_FORCE_MARGIN = 1;
	private static final int DEFENSE_FORCE_HELP_MARGIN = 5;
	
	private static final int NEUTRAL_CAPTURE_MARGIN = 1;

	@Override
	protected void think() {
		for(Planet p : getPlanetsOf(this))
			planetaryThink(p);
	}

	private void planetaryThink(Planet currentPlanet) {
		int minimumSelfDefense = calculateDefense(currentPlanet);
		defendAllies(currentPlanet, minimumSelfDefense);
		performAttack(currentPlanet, minimumSelfDefense);
	}

	private int calculateDefense(Planet currentPlanet) {
		int attackingForces = getEnemyForces(currentPlanet);
		return Math.min(MAXIMUM_SELF_DEFENSE, attackingForces + DEFENSE_FORCE_MARGIN);
	}
	
	private int getEnemyForces(Planet p) {
		int incomingForces = 0;
		for(Fleet f : getFleetsWithTarget(p))
			if(!f.getPlayer().equals(this))
				incomingForces += f.getForce();
		return incomingForces;
	}
	
	private int getAlliedForces(Planet p) {
		int incomingForces = 0;
		for(Fleet f : getFleetsWithTarget(p))
			if(f.getPlayer().equals(this))
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
			
			int neededDefense = getEnemyForces(ally) - ((int)ally.getForces() + getAlliedForces(ally));
			if(neededDefense > 0) {
				neededDefense += DEFENSE_FORCE_HELP_MARGIN;
				int forcesSent = Math.min(neededDefense, availableForces);
				Fleet fleet = sendFleetUpTo(currentPlanet, forcesSent, ally);
				fleet.setFormation(Formation.EYE);
				return; //defend only one ally
			}
		}
	}

	private void performAttack(Planet currentPlanet, int minimumSelfDefense) {		
		for(Planet target : currentPlanet.getOthersByDistance()) {
			if(target.getPlayer().equals(this))
				continue;
			
			int availableForces = (int)currentPlanet.getForces() - minimumSelfDefense;
			if(availableForces <= MINIMUM_FLEET_SIZE)
				return;
						
			if(target.getPlayer().equals(NonePlayer))
				captureNeutralPlanet(currentPlanet, target, availableForces);
			else
				attackOtherPlayer(currentPlanet, target, availableForces);
		}
	}

	private void captureNeutralPlanet(Planet currentPlanet, Planet target, int availableForces) {
		int required = (int)(target.getForces() + NEUTRAL_CAPTURE_MARGIN) - getAlliedForces(target);
		if(required > 0) {
			int sent = Math.min(required, availableForces);
			Fleet fleet = sendFleetUpTo(currentPlanet, sent, target);
			fleet.setFormation(Formation.SWARM);
		}
	}

	private void attackOtherPlayer(Planet currentPlanet, Planet target, int availableForces) {
		int required = (int)-getPrediction(target, currentPlanet.getTimeTo(target)).getBalance();
		if(required > 0) {
			int sent = Math.min(required, availableForces);
			Fleet fleet = sendFleetUpTo(currentPlanet, sent, target);
			fleet.setFormation(Formation.V);
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

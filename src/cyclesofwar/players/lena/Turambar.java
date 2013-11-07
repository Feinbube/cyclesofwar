package cyclesofwar.players.lena;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.players.frank.common.Prediction;

public class Turambar extends Player {

	@Override
	public void think() {
		
		List<Planet> myPlanets 		= this.getPlanets();
		List<Planet> foreignPlanets = this.getAllPlanetsButMine();
		List<Planet> freePlanets 	= this.getPlanetsOf(Player.NonePlayer);
		
		int range = foreignPlanets.size();
		
		Planet.sortBy(Planet.ForceCountComparator, myPlanets);
		for (Planet p : myPlanets) {
			
			double mine = p.getForces();
			
			mine = tryToAttackPlanetsInRange(p, mine, freePlanets, 5, false);
			mine = tryToAttackPlanetsInRange(p, mine, foreignPlanets, range, false);
			if (freePlanets.size() > 5)
				mine = tryToAttackPlanetsInRange(p, mine, freePlanets, 3, false);
			
			if (range >= 8) range /= 1.5;
			mine = tryToAttackPlanetsInRange(p, mine, foreignPlanets, range, true);
			
			if (mine <= 0) continue;
			
			for (Fleet f : Fleet.sortedBy(Fleet.ArrivalTimeComparator, this.getAllEnemyFleets())) {
				if (f.getTarget() == p) break;
				if (f.getTarget().getPlayer() == this) {
					
					int required = (int)f.getForce()+1;
					if ((int)mine - this.getAdvise(p, p.getTimeTo(f.getTarget())).getForcesToKeep() > required) {
						this.sendFleetUpTo(p, required, f.getTarget());
						mine -= required;
					}
				}
			}
		}
	}


	private double tryToAttackPlanetsInRange(Planet p, double mine,
			List<Planet> closest, int range, Boolean sort) {
		
		if (closest.size() == 0) return mine;
		
		Planet.sortByDistanceTo(closest, p);
		List<Planet> sublist = closest.subList(0,range < closest.size() ? range : closest.size()-1);
		for (Planet foreign : sort ? Planet.sortedBy(Planet.ProductivityComparator, sublist) : sublist) {
			mine = tryToAttack(p, mine, foreign);
		}
		return mine;
	}


	private double tryToAttack(Planet p, double mine, Planet foreign) {
		
		if (mine <= 1) return mine;
		
		int required 	= (int)-getPrediction(foreign, p.getTimeTo(foreign)).getBalance();
		int available 	= (int)(mine - this.getAdvise(p, p.getTimeTo(foreign)).getForcesToKeep());               
		if (required > 0 && available > required) {
			this.sendFleetUpTo(p, available > required ? required+1 : required, foreign);
			mine -= required;
		}
		return mine;
	}

	
	@Override
	public Color getPlayerBackColor() {
		return Color.pink;
	}

	@Override
	public Color getPlayerForeColor() {
		return Color.darkGray;
	}

	@Override
	public String getCreatorsName() {
		return "Lena";
	}
	
	@Override
	public String getName(){
		return "Turambar";
	}
}

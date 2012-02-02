package cyclesofwar;
import java.awt.Color;
import java.util.List;

import cyclesofwar.players.IdlePlayer;

public abstract class Player {
	
	static Player IdlePlayer = new IdlePlayer();

	protected abstract void think();
	
	protected abstract Color getPlayerBackColor();
	protected abstract Color getPlayerForeColor();
	protected abstract String getCreatorsName();
	
	// Players
	protected List<Player> getOtherPlayers() {
		return Universe.INSTANCE.OtherPlayers(this);
	}
	
	// Planets
	protected List<Planet> getAllPlanets() {
		return Universe.INSTANCE.AllPlanets();
	}
	
	public List<Planet> getPlanets() {
		return Universe.INSTANCE.PlanetsOfPlayer(this);
	}
	
	protected List<Planet> getFreePlanets() {
		return Universe.INSTANCE.PlanetsOfPlayer(Player.IdlePlayer);
	}
	
	protected List<Planet> getPlanetsOf(Player player) {
		return Universe.INSTANCE.PlanetsOfPlayer(player);
	}
	
	// Fleets
	protected List<Fleet> getAllFleets() {
		return Universe.INSTANCE.AllFleets();
	}
	
	public List<Fleet> getFleets() {
		return Universe.INSTANCE.FleetsOfPlayer(this);
	}
	
	protected List<Fleet> getFleetsOf(Player player) {
		return Universe.INSTANCE.FleetsOfPlayer(player);
	}
	
	protected void sendFleet(Planet planet, int force, Planet target) {
		Universe.INSTANCE.SendFleet(this, planet, force, target);
	}
}
package cyclesofwar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Universe {
	static Universe INSTANCE = new Universe();

	final int StarCount = 1000;

	List<Star> stars = new ArrayList<Star>();
	List<Planet> planets = new ArrayList<Planet>();
	List<Fleet> fleets = new ArrayList<Fleet>();

	List<Player> players = new ArrayList<Player>();

	Random random = new Random();

	Dimension size;
	
	ArrayList<Fleet> fleetsAtDestination = new ArrayList<Fleet>();

	private Universe() {
	}

	void reInitialize(Dimension size) {
		this.size = size;

		stars.clear();
		for (int i = 0; i < StarCount; i++) {
			stars.add(new Star(random, size));
		}

		planets.clear();
		for (int i = 0; i < Arena.PlanetCount; i++) {
			planets.add(suitablePlanet(-1));
		}

		fleets.clear();
		fleetsAtDestination.clear();
		
		players.clear();
		for(Player player : Arena.Combatants()) {
			players.add(player);
			createStarterPlanet(player);	
		}
	}

	private Planet createStarterPlanet(Player player) {
		Planet planet = suitablePlanet(5);
		planet.player = player;
		planets.add(planet);
		return planet;
	}

	private Planet suitablePlanet(int d) {
		while (true) {
			Planet planet = new Planet(random, size, d);

			if (noCollision(planets, planet))
				return planet;
		}
	}

	private boolean noCollision(List<Planet> planets, Planet planet) {
		for (Planet other : planets)
			if (other.collidesWith(planet))
				return false;

		return true;
	}

	void draw(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, size.width, size.height);

		for (Star star : stars) {
			star.draw(g);
		}

		for (Planet planet : planets) {
			planet.draw(g);
		}

		for (Fleet fleet : fleets) {
			fleet.draw(g);
		}
		
		for(int i=0; i<players.size(); i++){
			drawPlayer(g, players.get(i), 5, i*20);
		}
		
	}
	
	private String statistics(Player player) {
		String result = "";
	
		result += player.getCreatorsName() + "'s " + player.getClass().getSimpleName();
		
		double groundForces = 0;
		for(Planet planet : PlanetsOfPlayer(player))
			groundForces += planet.forces;
				
		result += " P[" + PlanetsOfPlayer(player).size() + "/" + ((int)groundForces) + "]";
				
		int spaceForces = 0;
		for(Fleet fleet : FleetsOfPlayer(player))
			spaceForces += fleet.force;
				
		result += " F[" + FleetsOfPlayer(player).size() + "/" + spaceForces + "]";
		
		return result;
	}

	protected void drawPlayer(Graphics g, Player p, int x, int y) {
		String s = statistics(p);
		
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		
		int w = g.getFontMetrics().stringWidth(s);
		int h = g.getFontMetrics().getHeight();
		
		g.setColor(p.getPlayerBackColor());
		g.fillRect(x-2, y+3, w+3, h);
		
		g.setColor(p.getPlayerForeColor());
		g.drawString(s, x, y+h);
	}
	
	void update(double elapsedSeconds) {
		for (Planet planet : planets) {
			planet.update(elapsedSeconds);
		}

		for (Fleet fleet : fleets) {
			fleet.update(elapsedSeconds);
		}

		for (Fleet fleet : fleetsAtDestination) {
			fleet.land();
			fleets.remove(fleet);
		}
		fleetsAtDestination.clear();
		
		for (Player player : players) {
			player.think();
		}
	}

	List<Player> OtherPlayers(Player player) {
		ArrayList<Player> result = new ArrayList<Player>();
		for (Player other : players) {
			if (!other.equals(player)) {
				result.add(other);
			}
		}

		return result;
	}
	
	List<Planet> AllPlanets() {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
				result.add(planet);
		}

		return result;
	}
	
	List<Planet> PlanetsOfPlayer(Player player) {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			if (planet.player.equals(player)) {
				result.add(planet);
			}
		}

		return result;
	}

	List<Fleet> AllFleets() {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
				result.add(fleet);
		}

		return result;
	}
	
	List<Fleet> FleetsOfPlayer(Player player) {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			if (fleet.player.equals(player)) {
				result.add(fleet);
			}
		}

		return result;
	}
	
	void SendFleet(Player player, Planet planet, int force, Planet target) {
		if(force <= 0 || force > planet.forces)
			return;
		
		planet.forces -= force;
		fleets.add(new Fleet(player, force, planet, target));
	}

	void fleetArrived(Fleet fleet) {
		fleetsAtDestination.add(fleet);
	}
}

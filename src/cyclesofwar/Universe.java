package cyclesofwar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Universe implements Updatable {
	public static Universe INSTANCE = new Universe();

	final int StarCount = 1000;
	final int PlanetCount = 20;

	List<Star> stars = new ArrayList<Star>();
	List<Planet> planets = new ArrayList<Planet>();
	List<Fleet> fleets = new ArrayList<Fleet>();

	List<Player> players = new ArrayList<Player>();

	Random random = new Random();

	Dimension size;
	
	ArrayList<Fleet> fleetsAtDestination = new ArrayList<Fleet>();

	private Universe() {
	}

	public void reInitialize(Dimension size) {
		this.size = size;

		stars.clear();
		for (int i = 0; i < StarCount; i++) {
			stars.add(new Star(random, size));
		}

		planets.clear();
		for (int i = 0; i < PlanetCount; i++) {
			planets.add(suitablePlanet(-1));
		}

		fleets.clear();
		fleetsAtDestination.clear();
		
		players.clear();
		Player player1 = new RandomPlayer(createPlayerColor(), createStarterPlanet());
		players.add(player1);
		
		Player player2 = new AttackLargestPlayer(createPlayerColor(), createStarterPlanet());
		players.add(player2);
	}

	private Color createPlayerColor() {
		Color c = Color.getHSBColor((float) random.nextDouble(), 0.5f, 0.5f);
		return c;
	}

	private Planet createStarterPlanet() {
		Planet planet = suitablePlanet(5);
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

	public void draw(Graphics g) {
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
			drawText(g, "P" + i + ":" + statistics(players.get(i)), 5, (i+1)*15);
		}
		
	}
	
	private String statistics(Player player) {
		String result = "";
		
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

	protected void drawText(Graphics g, String s, int x, int y) {
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		
		g.drawString(s, x, y);
	}

	@Override
	public void update(double elapsedSeconds) {
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

	public List<Planet> PlanetsOfPlayer(Player player) {
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (Planet planet : planets) {
			if (planet.player.equals(player)) {
				result.add(planet);
			}
		}

		return result;
	}

	public List<Fleet> FleetsOfPlayer(Player player) {
		ArrayList<Fleet> result = new ArrayList<Fleet>();
		for (Fleet fleet : fleets) {
			if (fleet.player.equals(player)) {
				result.add(fleet);
			}
		}

		return result;
	}
	
	public void SendFleet(Player player, Planet planet, int force, Planet target) {
		planet.forces -= force;
		fleets.add(new Fleet(player, force, planet, target));
	}

	public List<Planet> AllPlanets() {
		return planets;
	}

	public void FleetArrived(Fleet fleet) {
		fleetsAtDestination.add(fleet);
	}
}

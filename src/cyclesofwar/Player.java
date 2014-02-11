package cyclesofwar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * the player is the shaper of the universe. he has a number of planets and can send fleets to conquer even more of them
 * 
 * a player starts with a single planet
 * a player can only interact with the universe by sending fleets from planets he owns to other planets
 * 
 * get your opponents by using getOtherPlayers()
 * get your planets by using getPlanets()
 * get your fleets by using getFleets()
 * send forces using sendFleet()
 * 
 * logic of the game per round:
 * 		forces on all planets are increased
 * 		fleets are moved
 * 		fleets that have reached their destination land on it: (in the correct order)
 * 			planet and fleet belong to the same player: fleet forces join planet forces
 * 			planet and fleet belong to different players: planet forces are reduced by fleet forces. planet may change owner.
 * 		if there is only one player left: he is the winner, game is over
 * 		all players think. local changes to the universe (by sending fleets) can not be seen by player until next round
 * 		if nobody has sent a fleet for 1.000 rounds: game is over, nobody wins
 * 		if the game exceeds 100.000 rounds: game is over, nobody wins
 * 		start next round
 */
public abstract class Player extends Sortable implements Comparable<Player> {

	/**
	 * this player means "no player". it is the Null-Object that is assigned to
	 * empty planets
	 */
	public static Player NonePlayer = new NonePlayer();
        
        /**
        * this player is used for a real human who wants to play the game's interactive mode
        */
        public static Player GoldenPlayer = new GoldenPlayer();

	private Universe universe;

	void setUniverse(Universe universe) {
		this.universe = universe;
	}

	/**
	 * think and act. check your planets. check the forces of other players.
	 * send forces to win the game :)
	 */
	protected abstract void think();

	/**
	 * the unique back color used by this player class
	 */
	public abstract Color getPlayerBackColor();

	/**
	 * the unique fore color used by this player class
	 */
	public abstract Color getPlayerForeColor();

	/**
	 * the name of the creator / programmer of this player class
	 */
	public abstract String getCreatorsName();

        private String name = null;
	/**
	 * the unique name of this player class
	 */
	public String getName() {
            if(name == null)
                if(getCreatorsName() != null) {
                    name = getCreatorsName() + "'s " + this.getClass().getSimpleName();
                } else {
                    name = this.getClass().getSimpleName();    
                }
            return name;
	}

	/**
	 * get a new instance of this player class
	 */
	public Player freshOne() {
		for (Player other : Arena.registeredPlayers()) {
			if (other.equals(this)) {
				return other;
			}
		}

		return null;
	}

	@Override
	public boolean equals(Object other) {
            if(!(other instanceof Player))
                return false;
            return this.getName().equals(((Player) other).getName());
	}

        @Override
        public int hashCode() {
            return this.getName().hashCode();
        }

	// Universe

	/**
	 * returns the virtual elapsed time in seconds
	 */
	public double now() {
		return universe.getNow();
	}

	/**
	 * Deprecated. use getRoundDuration() instead.
	 */
	@Deprecated
	public double getStepInterval() {
		return getRoundDuration();
	}

	/**
	 * returns the duration of every round in seconds
	 */
	public double getRoundDuration() {
		return Universe.getRoundDuration();
	}

	// Players

	/**
	 * returns a list of all other players in the universe (including extinct
	 * ones)
	 */
	public List<Player> getOtherPlayers() {
		List<Player> result = universe.getPlayers();
		result.remove(this);
		return result;
	}

	/**
	 * returns a list of all other players in the universe (not including
	 * extinct ones)
	 */
	public List<Player> getOtherAlivePlayers() {
		List<Player> result = new ArrayList<>();
		for (Player player : universe.getPlayers()) {
			if (player.isAlive()) {
				result.add(player);
			}
		}
		result.remove(this);
		return result;
	}

	// Planets

	/**
	 * returns a list of all planets in the universe
	 */
	public List<Planet> getAllPlanets() {
		return universe.getAllPlanets();
	}

	/**
	 * returns all planets of this player
	 */
	public List<Planet> getPlanets() {
		return mineOnly(getAllPlanets());
	}

	/**
	 * returns all planets owned by player
	 */
	public List<Planet> getPlanetsOf(Player player) {
		return filter(getAllPlanets(), player);
	}

	/**
	 * return true if this is the owner of planet
	 */
	public boolean isMyPlanet(Planet planet) {
		return planet.getPlayer().equals(this);
	}

	/**
	 * returns all planets not owned by this player
	 */
	public List<Planet> getAllPlanetsButMine() {
		return hostileOnly(getAllPlanets(), this);
	}

	// Fleets

	/**
	 * returns a list of all fleets in the universe
	 */
	public List<Fleet> getAllFleets() {
		return universe.getAllFleets();
	}

	/**
	 * return all fleets of this player
	 */
	public List<Fleet> getFleets() {
		return mineOnly(getAllFleets());
	}

	/**
	 * returns all fleets owned by player
	 */
	public List<Fleet> getFleetsOf(Player player) {
		return filter(getAllFleets(), player);
	}

	/**
	 * return true if this is the owner of fleet
	 */
	public boolean isMyFleet(Fleet fleet) {
		return fleet.getPlayer().equals(this);
	}

	/**
	 * returns all fleets not owned by this player
	 */
	public List<Fleet> getAllEnemyFleets() {
		return hostileOnly(getAllFleets(), this);
	}

	/**
	 * returns all fleets targeting the specified planet
	 */
	public List<Fleet> getFleetsWithTarget(Planet target) {
		List<Fleet> result = new ArrayList<>();
		for (Fleet fleet : this.getAllFleets()) {
			if (fleet.getTarget().equals(target)) {
				result.add(fleet);
			}
		}
		return result;
	}

	/**
	 * starts a fleet with size 'force' and the specified target planet from
	 * planet
	 * 
	 * throws an exception if sender does not own the planet throws an exception
	 * if force is less than one or larger than forces on planet
	 */
	public Fleet sendFleet(Planet planet, int force, Planet target) {
		return universe.sendFleet(planet, force, target);
	}

	/**
	 * starts a fleet with size 'force' and the specified target planet from
	 * planet if force is less than one or larger than forces on planet no fleet
	 * will be sent
	 * 
	 * throws an exception if sender does not own the planet
	 */
	public Fleet sendFleetUpTo(Planet planet, double force, Planet target) {
            return universe.sendFleetUpTo(planet, force, target);		
	}

	// random

	/**
	 * returns a random double 0.0 to 1.0 (the normal random function) use this
	 * to make sure that games can be replayed
	 */
	public double getRandomDouble() {
		return universe.getRandomDouble();
	}

	/**
	 * returns a random int 0 to max-1 (the normal random function) use this to
	 * make sure that games can be replayed
	 */
	public int getRandomInt(int max) {
		return universe.getRandomInt(max);
	}
        
        /**
         * shuffles a list
         */
        public <T> void shuffle(List<T> list){
            universe.shuffle(list);
        }

        // checking functions
        
        /**
	 * returns true if a planet/fleet in list that shallBelongTo: belong to player
	 * !shallBelongTo: do not belong to player
	 */
        static <T extends GameObject> boolean containsItemOf(List<T> list, Player player, boolean shallBelongTo) {
            for (T gameObject : list) {
                    if ((gameObject.getPlayer().equals(player)) == shallBelongTo) {
                            return true;
                    }
            }
            return false;
	}
        
        /**
	 * return true if at least one planet/fleet in list that does not belong to this player
	 */
	public <T extends GameObject> boolean containsHostileItem(List<T> list) {
		return containsItemOf(list, this, false);
	}
        
	// filter functions
        
	/**
	 * returns all planets/fleets in list that shallBelongTo: belong to player
	 * !shallBelongTo: do not belong to player
	 */
	static <T extends GameObject> List<T> filter(List<T> list, Player player, boolean shallBelongTo) {
		ArrayList<T> result = new ArrayList<>();
		for (T gameObject : list) {
			if ((gameObject.getPlayer().equals(player)) == shallBelongTo) {
				result.add(gameObject);
			}
		}
		return result;
	}

	/**
	 * returns all planets/fleets in list that belong to player
	 */
	public static <T extends GameObject> List<T> filter(List<T> list, Player player) {
		return filter(list, player, true);
	}

	/**
	 * returns all planets/fleets in list that belong to this player
	 */
	public <T extends GameObject> List<T> mineOnly(List<T> list) {
		return filter(list, this, true);
	}

	/**
	 * returns all planets/fleets in list that do not belong to player
	 */
	public static <T extends GameObject> List<T> hostileOnly(List<T> list, Player player) {
		return filter(list, player, false);
	}

	/**
	 * returns all planets/fleets in list that do not belong to this player
	 */
	public <T extends GameObject> List<T> hostileOnly(List<T> list) {
		return filter(list, this, false);
	}

	/**
	 * returns all planets/fleets in list that do not belong to any player
	 */
	public <T extends GameObject> List<T> freeOnly(List<T> list) {
		return filter(list, Player.NonePlayer, true);
	}
	
	/**
	 * returns the first element of list or null if list is empty
	 */
	public static <T> T firstOrNull(List<T> list) {
		return atIndexOrNull(list, 0);
	}
        
        /**
	 * returns the last element of list or null if list is empty
	 */
	public static <T> T lastOrNull(List<T> list) {
		return atIndexOrNull(list, list.size()-1);
	}
	
	/**
	 * returns a random element of list or null if list is empty
	 */
	public <T> T randomOrNull(List<T> list) {
		if(list.isEmpty())
			return null;
		return list.get(universe.getRandomInt(list.size()));
	}

	/**
	 * returns the first element of list or null if list does not have so many
	 * elements
	 */
	public static <T> T atIndexOrNull(List<T> list, int index) {
		if (list.size() > index && index >= 0) {
			return list.get(index);
		} else {
			return null;
		}
	}

	/**
	 * returns a new list that contains all elements that are in both lists
	 */
	public static <T> List<T> inBothLists(List<T> list1, List<T> list2) {
		List<T> result = new ArrayList<>();
		for (T one : list1) {
			if (list2.contains(one)) {
				result.add(one);
			}
		}
		return result;
	}
	
	/**
	 * returns a list containing up to the specified number of elements of list, starting from the first one
	 */
	public static <T> List<T> firstElements(List<T> list, int numberOfElements) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i<Math.min(numberOfElements, list.size()); i++) {
			result.add(list.get(i));
		}
		return result;
	}

	/**
	 * get the total amount of ground forces of this player (troops on all
	 * planets)
	 */
	public double getGroundForce() {
		double result = 0.0;
		for (Planet planet : this.getPlanets()) {
			result += planet.getForces();
		}
		return result;
	}

	/**
	 * get the total amount of ground forces of this player (troops on all
	 * fleets)
	 */
	public int getSpaceForce() {
		int result = 0;
		for (Fleet fleet : this.getFleets()) {
			result += fleet.getForce();
		}
		return result;
	}

	/**
	 * get the total amount of ground forces of this player (troops on all
	 * planets + troops on all fleets)
	 */
	public double getFullForce() {
		return getGroundForce() + getSpaceForce();
	}

	/**
	 * Deprecated. use Planet.sortBy(Planet.ForceCountComparator, planets) instead
	 */
	@Deprecated
	public static void sortByForceCount(List<Planet> planets) {
		Planet.sortBy(Planet.ForceCountComparator, planets);
	}

	/**
	 * returns true if the player has fleets or planets left
	 */
	public boolean isAlive() {
		return !this.getFleets().isEmpty() || !this.getPlanets().isEmpty();
	}
        
	/**
	 * get a prediction of the state of the planet at the given time
	 * given no new fleet is created 
	 */
	public Prediction getPrediction(Planet planet, double time){
            return universe.getPrediction(planet, time);
	}
        
        /**
	 * get an advise how to keep a planet till the point in time
	 */
	public Advise getAdvise(Planet planet, double time){
		return this.getAdvise(planet, 0.0, time);
	}
        
        /**
	 * get an advise how to keep a planet till the point in time (endTime)
         * given it has been acquired before (at startTime)
	 */
	public Advise getAdvise(Planet planet, double startTime, double endTime){
            return universe.getAdvise(planet, startTime, endTime);
	}

	/**
	 * returns the time of the when the last existing fleet (by anyone) lands on its target
	 * given no new fleet is created  
	 */
	public double getlastFleetArrivalTime() {
            return universe.getlastFleetArrivalTime();
	}
        
        /**
         * used to sort players by planet count (descending)
         * 
         * use Player.sortBy(PlanetCountComparator, players, )
         * or Player.sortedBy(PlanetCountComparator, players, )
         */
        public static Comparator<Player> PlanetCountComparator = new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return Double.compare(player2.getPlanets().size(), player1.getPlanets().size());
            }
        };
        
        
        /**
         * used to sort players by force count (descending)
         * 
         * use Player.sortBy(ForceCountComparator, players, )
         * or Player.sortedBy(ForceCountComparator, players, )
         */
        public static Comparator<Player> ForceCountComparator = new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return Double.compare(player2.getFullForce(), player1.getFullForce());
            }
        };

    @Override
    public int compareTo(Player other) {
        return this.getName().compareTo(other.getName());
    }
}
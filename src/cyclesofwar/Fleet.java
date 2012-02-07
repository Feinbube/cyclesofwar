package cyclesofwar;


public class Fleet {

	public enum Formation {
		SWARM, ARROW 
	}
	
	private Universe universe;
	
	private Player player;
	private int force;
	
	private Planet target;
	
	private double x;
	private double y;
	
	private Formation formation = Formation.SWARM;
	
	Fleet(Universe universe, Player player, int force, Planet start, Planet target){
		if (force < 1) {
			throw new IllegalArgumentException("force must be greater than 0 but was " + force);
		}
		
		if (target == null) {
			throw new IllegalArgumentException("fleet must have a target");
		}
		
		if (target.equals(start)) {
			throw new IllegalArgumentException("start and target are not allowed to be identical");
		}
		
		this.universe = universe;
		
		this.player = player;
		this.force = force;
		
		this.target = target;
		
		x = start.getX();
		y = start.getY();
	}
	
	public Player getPlayer() {
		return player;
	}

	public int getForce() {
		return force;
	}

	public Planet getTarget() {
		return target;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	void update(double elapsedSeconds) {
		double xDiff = x - target.getX();
		double yDiff = y - target.getY();
		
		double sqrt = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		if(sqrt < Universe.speedOfLight * elapsedSeconds)
			hit(sqrt);
		
		xDiff = xDiff / sqrt;
		yDiff = yDiff / sqrt;
		
		x -= Universe.speedOfLight * elapsedSeconds * xDiff;
		y -= Universe.speedOfLight * elapsedSeconds * yDiff;
	}
	
	private void hit(double distance) {
		universe.fleetArrived(this, distance);
	}
	void land() {
		this.target.land(this);
	}
	
	// for players
	
	public double distanceToTarget() {
		double xDiff = target.getX() - this.x;
		double yDiff = target.getY() - this.y;
		
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	public double timeToTarget() {
		return distanceToTarget() / Universe.speedOfLight;
	}
	
	public double getFlightSpeed() {
		return Universe.speedOfLight;
	}
	
	public Formation getFormation() {
		return this.formation;
	}
	
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
}

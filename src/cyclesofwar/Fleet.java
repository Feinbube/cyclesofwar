package cyclesofwar;

public class Fleet {

	Player player;
	int force;
	
	Planet target;
	
	double x;
	double y;
	
	public Fleet(Player player, int force, Planet start, Planet target){
		if (force < 1) {
			throw new IllegalArgumentException("force must be greate 0 but was " + force);
		}
		
		if (target == null) {
			throw new IllegalArgumentException("fleet must have a target");
		}
		
		this.player = player;
		this.force = force;
		
		this.target = target;
		
		x = start.x;
		y = start.y;
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
		double xDiff = x - target.x;
		double yDiff = y - target.y;
		
		double sqrt = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		if(sqrt < Universe.speedOfLight * elapsedSeconds)
			hit(sqrt);
		
		xDiff = xDiff / sqrt;
		yDiff = yDiff / sqrt;
		
		x -= Universe.speedOfLight * elapsedSeconds * xDiff;
		y -= Universe.speedOfLight * elapsedSeconds * yDiff;
	}
	
	private void hit(double distance) {
		Universe.INSTANCE.fleetArrived(this, distance);
	}
	
	void land() {
		if(target.player.equals(player))
			target.forces += force;
		else
			target.forces -= force;
		
		if(target.forces < 0)
		{
			target.forces = -target.forces;
			target.player = player;
		}
	}
	
	public double distanceToTarget() {
		double xDiff = target.x - this.x;
		double yDiff = target.y - this.y;
		
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	public double timeToTarget() {
		return distanceToTarget() * Universe.speedOfLight;
	}
}

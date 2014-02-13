package cyclesofwar.players.andreasg.helper;

import java.util.List;
import java.util.Comparator;
import java.lang.Comparable;

import cyclesofwar.Player;
import cyclesofwar.Planet;
import cyclesofwar.Fleet;

import cyclesofwar.players.andreasg.helper.*;

public class MyFleet implements Comparable<MyFleet>
{
  
  public double forces;
  public Planet target;
  public double timeToTarget;
  public Player owner;

  public MyFleet(double forces, Planet target, double timeToTarget, Player owner)
  {
    this.forces = Math.floor(forces);
    this.target = target;
    this.timeToTarget = timeToTarget;
    this.owner = owner;
  }

	public static Comparator<MyFleet> ArrivalTimeComparator = new Comparator<MyFleet>() 
  {
		@Override
		public int compare(MyFleet one, MyFleet other) 
    {
			return Double.compare(one.timeToTarget, other.timeToTarget);
		}
	};
        
  @Override
  public int compareTo(MyFleet other) 
  {
    return ArrivalTimeComparator.compare(this, other);
  }


}

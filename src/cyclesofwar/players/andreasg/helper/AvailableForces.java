package cyclesofwar.players.andreasg.helper;

import java.util.Comparator;

import cyclesofwar.players.andreasg.AndreasG;
import cyclesofwar.Planet;

public class AvailableForces implements Comparable<AvailableForces>
{
    
  public Planet from;
  public double forces;

  public AvailableForces(Planet from, double forces)
  {
    this.from = from;
    this.forces = forces;
  }

  public static Comparator<AvailableForces> ArrivalTimeComparator = new Comparator<AvailableForces>() 
  {
    @Override
    public int compare(AvailableForces a, AvailableForces b) 
    {
      return Double.compare(
        a.from.getDistanceTo(((AndreasG)a.from.getPlayer()).helper1()), 
        b.from.getDistanceTo(((AndreasG)b.from.getPlayer()).helper1()));
    }
  };
        
  @Override
  public int compareTo(AvailableForces other) 
  {
    return ArrivalTimeComparator.compare(this, other);
  }

}


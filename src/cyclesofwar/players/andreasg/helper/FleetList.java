package cyclesofwar.players.andreasg.helper;

import java.util.List;
import java.util.ArrayList;
import java.lang.Iterable;
import java.util.Iterator;
import java.util.Collections;

import cyclesofwar.Player;
import cyclesofwar.Planet;
import cyclesofwar.Fleet;

import cyclesofwar.players.andreasg.helper.*;

public class FleetList implements Iterable
{

  List<MyFleet> fleets;

  public FleetList(List<Fleet> fleets)
  {
    this.fleets = new ArrayList<MyFleet>();
    for (Fleet f : fleets)
      this.fleets.add(new MyFleet(f.getForce(), f.getTarget(), f.getTimeToTarget(), f.getPlayer()));

    Collections.sort(this.fleets, MyFleet.ArrivalTimeComparator);
  }

  public void add(AvailableForces a, Planet p)
  {
		double xDiff = a.from.getX() - p.getX();
		double yDiff = a.from.getY() - p.getY();

    double flightSpeed = Fleet.getFlightSpeed();
                
		double distanceToTarget = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    double time = distanceToTarget / flightSpeed;

    this.fleets.add(new MyFleet(a.forces, a.from, time, a.from.getPlayer()));

    Collections.sort(this.fleets, MyFleet.ArrivalTimeComparator);
  }

  @Override
  public Iterator<MyFleet> iterator()
  {
    return fleets.iterator();
  }

}

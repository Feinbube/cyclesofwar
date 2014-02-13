package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Comparator;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

import cyclesofwar.players.andreasg.helper.*;

/* This is my current top player, based on the things I learned
 * while developing Zerglings and Hydralisks.
 */

public class Mutalisks extends AndreasG 
{
 
  private Actions actions;
  private DistressBeacons beacons;

  private List<AvailableForces> troops;
 
  @Override
  protected void think() 
  {
    debug("Round" + ++round + ": " + getPlanetsOf(this).size() + "p" + getFleetsOf(this).size() + "f");

    actions = new Actions();
    beacons = new DistressBeacons();

    troops =  new ArrayList<AvailableForces>();

    for (Planet p : getPlanetsOf(this))
      contactPlanet(p);

    for (DistressBeacon b : beacons.get())
      tryToSendHelp(b);

    spread();

    actions.execute();
    beacons.evacuate();
  }

  private void contactPlanet(Planet p)
  {
    PlanetSimulationResult r = new PlanetSimulationResult(p, this);

    if (r.beacon != null)
      {
        debug("  distress: " + p + " needs " + r.beacon.forcesRequired + " by " + r.beacon.secondsRemaining);
        beacons.add(r.beacon);
      }
    else if (r.available >= 1)
      {
        debug("  troops:   " + p + " has   " + r.available);
        troops.add(new AvailableForces(p, r.available));
      }
  }

  private void tryToSendHelp(DistressBeacon b)
  {
    int troops_in_range = 0;

    for (AvailableForces a : troops)
      if (a.from.getTimeTo(b.from) < b.secondsRemaining)
        troops_in_range += (int)(a.forces + (b.secondsRemaining - a.from.getTimeTo(b.from)) * a.from.getProductionRatePerSecond());

    // in 1on1, sending help regardless of how much troops we have seems to be a good idea 
    if (getOtherAlivePlayers().size() > 1 && troops_in_range < b.forcesRequired)
      {
        b.evacuate();
        return;
      }

    thePlanetThatIAmCurrentlySortingFor = b.from;
    Collections.sort(troops, AvailableForces.ArrivalTimeComparator);
    for (AvailableForces a : troops)
      {
        if (a.from == b.from)
          continue;

        debug("  help from " + b.from + " " + a.from.getRoundsTo(b.from));
        double tosend = Math.min(Math.ceil(b.forcesRequired), Math.floor(a.forces));
        actions.add(a.from, b.from, (int)tosend);
        a.forces -= tosend;
        if (a.forces < 1)
          a.forces = 0;
        b.forcesRequired -= tosend;
        if (b.forcesRequired < 0)
          b.forcesRequired = 0;
      }
  }

  private void spread()
  {
    for (AvailableForces a : troops)
      {
        List<Planet> cluster = new ArrayList<Planet>();
        cluster.add(a.from);
        for (Planet p : a.from.getOthersByDistance())
          {
            if (cluster.size() >= 1)
              break;
            if (p.getPlayer().equals(this))
              cluster.add(p);
          }

        Map<Double, Planet> cluster_targets = new TreeMap<Double, Planet>();
        for (Planet p : a.from.getOthersByDistance())
          {
            double dist = 0;
            for (Planet c : cluster)
              dist += c.getDistanceTo(p) * c.getDistanceTo(p);
            dist /= cluster.size();
            cluster_targets.put(dist, p);
          }

        int dist = 0;
        for (Entry<Double, Planet> e : cluster_targets.entrySet())
          {
            if (e.getValue().getPlayer().equals(this))
              continue;

            PlanetSimulationResult r = new PlanetSimulationResult(e.getValue(), this);
            if (r.owner.equals(this))
              continue;

            r = new PlanetSimulationResult(e.getValue(), this, a);
            if (!r.owner.equals(this) && a.forces < 100)
              break;

            debug("  spreading from " + e.getValue() + " with " + Math.floor(a.forces));
            actions.add(a.from, e.getValue(), (int)Math.floor(a.forces));
            a.forces = 0;
          }
      }
  }

}


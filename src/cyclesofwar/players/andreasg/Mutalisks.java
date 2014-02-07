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
    //PlanetSimulationResult r = new PlanetSimulationResult(p);

    //if (r.beacon != null)
    //  beacons.add(r.beacon);

    double forces = p.getForces();
    double free_forces = forces;
    double production = p.getProductionRatePerRound();

    int lastevent = 0;

    List<Fleet> fleets = Fleet.sortedBy(Fleet.ArrivalTimeComparator, getFleetsWithTarget(p));
    for (Fleet f : fleets)
      {
        debug("  fleet " + f + " will arrive at " + p + " in " + f.getRoundsToTarget());
        forces += production * (f.getRoundsToTarget() - lastevent);
        lastevent = f.getRoundsToTarget();
        if (f.getPlayer() == this)
          forces += f.getForce();
        else
          {
            forces -= f.getForce();
            if (forces < 0)
              {
                debug("  distress: " + p + " needs " + -forces + " by " + f.getRoundsToTarget());
                beacons.add(p, -forces, f.getRoundsToTarget());
                return;
              }
            if (forces < free_forces) 
              free_forces = forces;
          }
      }

    if (free_forces >= 1)
      {
        debug("  available forces from " + p + ": " + free_forces);
        troops.add(new AvailableForces(p, free_forces));
      }
  }

  private void tryToSendHelp(DistressBeacon b)
  {
    if (b.roundsRemaining < 2)
      troops.add(new AvailableForces(b.from, b.from.getForces()));

    int troops_in_range = 0;

    for (AvailableForces a : troops)
      if (a.from.getRoundsTo(b.from) < b.roundsRemaining)
        troops_in_range += (int)a.forces;

    if (troops_in_range < b.forcesRequired)
      return;
    
    thePlanetThatIAmCurrentlySortingFor = b.from;
    Collections.sort(troops);
    for (AvailableForces a : troops)
      {
        if (a.from == b.from)
          continue;

        debug("  help from " + b.from + " " + a.from.getRoundsTo(b.from));
        if (a.forces > Math.ceil(b.forcesRequired))
          {
            actions.add(a.from, b.from, (int)Math.ceil(b.forcesRequired));
            a.forces -= Math.ceil(b.forcesRequired);
            b.forcesRequired = 0;
            break;
          }
        actions.add(a.from, b.from, (int)Math.floor(a.forces));
        a.forces = 0;
      }
  }

  private void spread()
  {
    for (AvailableForces a : troops)
      {
        if (a.forces < 1)
          continue;
       
        List<Planet> cluster = new ArrayList<Planet>();
        cluster.add(a.from);
        for (Planet p : a.from.getOthersByDistance())
          {
            if (cluster.size() >= 1)
              break;
            if (p.getPlayer() == this)
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
            if (e.getValue().getPlayer() == this)
              continue;

            if (a.forces > 100 || possibleConquer(a, e.getValue()))
              {
                debug("  spreading from " + e.getValue() + " with " + Math.floor(a.forces));
                actions.add(a.from, e.getValue(), (int)Math.floor(a.forces));
                a.forces = 0;
              }

            if (++dist > 1)
              break;
          }
      }
  }

  private boolean possibleConquer(AvailableForces a, Planet p)
  {
    Player owner = p.getPlayer();
    double forces = p.getForces();

    int myArrival = a.from.getRoundsTo(p);
    int lastevent = 0;

    List<Fleet> fleets = Fleet.sortedBy(Fleet.ArrivalTimeComparator, getFleetsWithTarget(p));    

    for (Fleet f : fleets)
      {
        if (owner != Player.NonePlayer)
          forces += p.getProductionRatePerRound() * (f.getRoundsToTarget() - lastevent);

        lastevent = f.getRoundsToTarget();
        if (f.getPlayer() == owner)
          forces += f.getForce();
        else
          {
            forces -= f.getForce();
            if (forces < 0)
              {
                owner = f.getPlayer();
                forces = -forces;
              }
          }
      }

    if (owner == this)
      return false;

    owner = p.getPlayer();
    forces = p.getForces();

    for (Fleet f : fleets)
      {
        if (f.getRoundsToTarget() > myArrival && myArrival != 0)
          {
            myArrival = 0;
            if (owner != Player.NonePlayer)
              forces += p.getProductionRatePerRound() * (myArrival - lastevent);

            lastevent = myArrival;
            if (owner == this)
              forces += a.forces;
            else
              {
                forces -= a.forces;
                if (forces < 0)
                  {
                    owner = this;
                    forces = -forces;
                  }
              }
          }

        if (owner != Player.NonePlayer)
          forces += p.getProductionRatePerRound() * (f.getRoundsToTarget() - lastevent);

        lastevent = f.getRoundsToTarget();
        if (f.getPlayer() == owner)
          forces += f.getForce();
        else
          {
            forces -= f.getForce();
            if (forces < 0)
              {
                owner = f.getPlayer();
                forces = -forces;
              }
          }
      }

    if (myArrival != 0)
      {
        if (owner != Player.NonePlayer)
          forces += p.getProductionRatePerRound() * (myArrival - lastevent);

        lastevent = myArrival;
        if (owner == this)
          forces += a.forces;
        else
          {
            forces -= a.forces;
            if (forces < 0)
              {
                owner = this;
                forces = -forces;
              }
          }
      }

    return (owner == this);
  }

}


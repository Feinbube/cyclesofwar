package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Comparator;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

/* todo: do not send more troops than necessary
 *
 */

public class Hydralisks extends Player 
{
  
  private List<Action> actions;
  private List<DistressBeacon> beacons;

  private List<AvailableForces> troops;
 
  private Planet thePlanetThatIAmCurrentlySortingFor;

  private int round = 0;

  @Override
  protected void think() 
  {
    debug("Round" + ++round + ": " + getPlanetsOf(this).size() + "p" + getFleetsOf(this).size() + "f");
    actions = new ArrayList<Action>();
    beacons = new ArrayList<DistressBeacon>();
    troops =  new ArrayList<AvailableForces>();

    for (Planet p : getPlanetsOf(this))
      simulateMyPlanet(p);

    for (DistressBeacon b : beacons)
      tryToSendHelp(b);

    spread();
    evacuate();

    for (Action a : actions)
      a.execute();
  }

  private void simulateMyPlanet(Planet p)
  {
    double forces = p.getForces();
    double free_forces = forces;
    double production = p.getProductionRatePerRound();

    int lastevent = 0;

    List<Fleet> fleets = Fleet.sortedBy(Fleet.ArrivalTimeComparator, getFleetsWithTarget(p));
    for (Fleet f : fleets)
      {
        forces += production * (f.getRoundsToTarget() - lastevent);
        lastevent = f.getRoundsToTarget();
        if (f.getPlayer() == this)
          forces += f.getForce();
        else
          {
            forces -= f.getForce();
            if (forces <= 0)
              {
                debug("  distress: " + p + " needs " + -forces + " by " + f.getRoundsToTarget());
                beacons.add(new DistressBeacon(p, -forces, f.getRoundsToTarget()));
                return;
              }
            if (forces < free_forces) 
              free_forces = forces;
          }
      }

    if (free_forces >= 1)
      {
        debug("  available forces from " + p + ": " + free_forces * 0.95);
        troops.add(new AvailableForces(p, free_forces * 0.95, false));
      }
  }

  private void tryToSendHelp(DistressBeacon b)
  {
    if (b.roundsRemaining < 2)
      troops.add(new AvailableForces(b.from, b.from.getForces(), true));

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
            actions.add(new Action(a.from, b.from, (int)Math.ceil(b.forcesRequired)));
            a.forces -= Math.ceil(b.forcesRequired);
            b.forcesRequired = 0;
            break;
          }
        actions.add(new Action(a.from, b.from, (int)Math.floor(a.forces)));
        a.forces = 0;
      }
  }

  private void spread()
  {
    for (AvailableForces a : troops)
      {
        if (a.forces < 1)
          continue;
        
        int dist = 0;
        for (Planet p : a.from.getOthersByDistance())
          {
            if (p.getPlayer() == this)
              continue;

            if (a.forces > 100 || possibleConquer(a, p))
              {
                debug("  spreading from " + p + " with " + Math.floor(a.forces));
                actions.add(new Action(a.from, p, (int)Math.floor(a.forces)));
                a.forces = 0;
              }

            if (++dist > 2)
              break;
          }
      }
  }

  private void evacuate()
  {
    for (AvailableForces a : troops)
      {
        if (!a.evacuation)
          continue;

        for (Planet p : a.from.getOthersByDistance())
          if (p.getPlayer() == this && p != a.from)
            {
              actions.add(new Action(a.from, p, (int)Math.floor(a.forces)));
              a.forces = 0;
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
            if (forces <= 0)
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
                if (forces <= 0)
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
            if (forces <= 0)
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
            if (forces <= 0)
              {
                owner = this;
                forces = -forces;
              }
          }
      }

    return (owner == this);
  }

  @Override
  public Color getPlayerBackColor() 
  {
    return new Color(0xF5, 0x5A, 0x0C);
  }

  @Override
  public Color getPlayerForeColor() 
  {
    return Color.WHITE;
  }

  @Override
  public String getCreatorsName() 
  {
    return "Andi";
  }

  public Planet helper1()
  {
    return thePlanetThatIAmCurrentlySortingFor;
  }

  private void debug(String str)
  {
    //System.out.println(str);
  }

  private static class AvailableForces implements Comparable<AvailableForces>
  {
    
    private Planet from;
    private double forces;

    private boolean evacuation;

    public AvailableForces(Planet from, double forces, boolean evacuation)
    {
      this.from = from;
      this.forces = forces;
      this.evacuation = evacuation;
    }

	  public static Comparator<AvailableForces> ArrivalTimeComparator = new Comparator<AvailableForces>() 
    {
		  @Override
		  public int compare(AvailableForces a, AvailableForces b) 
      {
			  return Double.compare(
          a.from.getDistanceTo(((Hydralisks)a.from.getPlayer()).helper1()), 
          b.from.getDistanceTo(((Hydralisks)b.from.getPlayer()).helper1()));
		  }
	  };
        
    @Override
    public int compareTo(AvailableForces other) 
    {
      return ArrivalTimeComparator.compare(this, other);
    }

  }

  private class DistressBeacon
  {
    
    private Planet from;
    private double forcesRequired;
    private int roundsRemaining;

    public DistressBeacon(Planet from, double forcesRequired, int roundsRemaining)
    {
      this.from = from;
      this.forcesRequired = forcesRequired;
      this.roundsRemaining = roundsRemaining;
    }

  }

  private class Action
  {

    private Planet from;
    private Planet to;
    private int forces;

    public Action(Planet from, Planet to, int forces)
    {
      this.from = from;
      this.to = to;
      this.forces = forces;
    }

    public void execute()
    {
      if (forces > 0)
        from.getPlayer().sendFleet(from, forces, to);
    }

  }

}


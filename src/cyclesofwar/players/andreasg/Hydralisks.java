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

import cyclesofwar.players.andreasg.helper.*;

/* This class has probably reached its end of life and is soon to be
 * superceded by the Mutalisks. It is kept for historical reasons as 
 * the first Bot I could make that could *almost* beat Frank's Jane
 *
 * last training score: 
 *   1,Andi's Hydralisks,1979,2100
 *   2,Frank's Jane,52,100
 *   3,Jan's FriendlyPirates,22,100
 *   4,Theo's SpaceMenace,11,100
 *   5,B14_BraveRabbit,10,100
 *   6,B10_Front,6,100
 *   7,B12_Closest,6,100
 *   8,B13_Rabbit,4,100
 *   9,B08_NewLeader,3,100
 *   10,B11_Cells,3,100
 *   11,B05_Worm,2,100
 *   12,B09_CloseBond,1,100
 *   13,Martin's Cratters,1,100
 *   14,B00_Idle,0,100
 *   15,B01_ChaseMe,0,100
 *   16,B02_Wave,0,100
 *   17,B03_BigGun,0,100
 *   18,B04_Random,0,100
 *   19,B06_PowersOf2,0,100
 *   20,B07_Sniper,0,100
 *   21,Robert's AttackLargestPlayer,0,100
 *   22,Peter's DumbVirus,0,100
 *   23,Nobody,0,2100
 */ 

public class Hydralisks extends AndreasG 
{
 
  private Actions actions;

  private List<DistressBeacon> beacons;

  private List<AvailableForces> troops;
 
  private int round = 0;

  @Override
  protected void think() 
  {
    debug("Round" + ++round + ": " + getPlanetsOf(this).size() + "p" + getFleetsOf(this).size() + "f");

    actions = new Actions();

    beacons = new ArrayList<DistressBeacon>();
    troops =  new ArrayList<AvailableForces>();

    for (Planet p : getPlanetsOf(this))
      contactPlanet(p);

    for (DistressBeacon b : beacons)
      tryToSendHelp(b);

    spread();

    actions.execute();
    evacuate();
  }

  private void contactPlanet(Planet p)
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
            if (forces < 0)
              {
                debug("  distress: " + p + " needs " + -forces + " by " + f.getTimeToTarget());
                beacons.add(new DistressBeacon(p, -forces, f.getTimeToTarget()));
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
    if (b.secondsRemaining <= getRoundDuration())
      troops.add(new AvailableForces(b.from, b.from.getForces()));

    int troops_in_range = 0;

    for (AvailableForces a : troops)
      if (a.from.getTimeTo(b.from) < b.secondsRemaining)
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
        
        int dist = 0;
        for (Planet p : a.from.getOthersByDistance())
          {
            if (p.getPlayer() == this)
              continue;

            if (a.forces > 100 || possibleConquer(a, p))
              {
                debug("  spreading from " + p + " with " + Math.floor(a.forces));
                actions.add(a.from, p, (int)Math.floor(a.forces));
                a.forces = 0;
              }

            if (++dist > 2)
              break;
          }
      }
  }

  private void evacuate()
  {
    for (DistressBeacon b : beacons)
      b.evacuate();
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


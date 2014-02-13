package cyclesofwar.players.andreasg.helper;

import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Planet;
import cyclesofwar.Fleet;

import cyclesofwar.players.andreasg.helper.*;

public class PlanetSimulationResult
{

  public DistressBeacon beacon;
  public double available;
  public double overkill;
  public Player owner;

  private Player me;

  public PlanetSimulationResult(Planet p, Player me)
  {
    this.me = me;
    
    FleetList fleets = new FleetList(me.getFleetsWithTarget(p));
   
    simulate(p, fleets);
  }

  public PlanetSimulationResult(Planet p, Player me, AvailableForces a)
  {
    this.me = me;

    FleetList fleets = new FleetList(me.getFleetsWithTarget(p));
    fleets.add(a, p);

    simulate(p, fleets);
  }

  private void simulate(Planet p, FleetList fleets)
  {
    beacon = null;
    owner = p.getPlayer();
    Player original_owner = owner;
    double forces = p.getForces();
    double production = p.getProductionRatePerSecond();
    available = (p.getPlayer().equals(me) ? forces : 0);

    double last_event = 0;

    for (Object o : fleets)
      {
        MyFleet f = (MyFleet)o;

        if (!owner.equals(Player.NonePlayer))
          forces += production * (f.timeToTarget - last_event);
        last_event = f.timeToTarget;

        forces += (owner.equals(f.owner) ? 1 : -1) * f.forces;
        available = Math.min(available, forces);

        if (forces < 0)
          {
            forces = -forces;

            if (original_owner.equals(me) && beacon == null)
              {
                beacon = new DistressBeacon(p, forces, f.timeToTarget);
                available = 0;
              }

            owner = f.owner;
          }
      }
  }

}

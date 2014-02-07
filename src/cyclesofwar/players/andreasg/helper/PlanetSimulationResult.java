package cyclesofwar.players.andreasg.helper;

import cyclesofwar.Player;
import cyclesofwar.Planet;

import cyclesofwar.players.andreasg.helper.*;

public class PlanetSimulationResult
{

  public DistressBeacon beacon;

  public PlanetSimulationResult(Planet p, Player me)
  {
    beacon = null;

    if (p.getPlayer() == me)
      simulateFriendly(p, me);
    else
      simulateAggressive(p, me);
  }

  private void simulateFriendly(Planet p, Player me)
  {
  
  }

  private void simulateAggressive(Planet p, Player me)
  {
  
  }

}

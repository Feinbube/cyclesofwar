package cyclesofwar.players.andreasg.helper;

import cyclesofwar.players.andreasg.AndreasG;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class DistressBeacon
{

  public Planet from;
  public double forcesRequired;
  public int roundsRemaining;

  public DistressBeacon(Planet from, double forcesRequired, int roundsRemaining)
  {
    this.from = from;
    this.forcesRequired = forcesRequired;
    this.roundsRemaining = roundsRemaining;
  }

  public void evacuate()
  {
    if (roundsRemaining > 1)
      return;
    
    if (from.getForces() < 1)
      return;

    Planet nearest = ((AndreasG)from.getPlayer()).getNearestFriendlyPlanet(from);
    if (nearest == null)
      return;

    from.getPlayer().sendFleet(from, (int)Math.floor(from.getForces()), nearest);
  }

}



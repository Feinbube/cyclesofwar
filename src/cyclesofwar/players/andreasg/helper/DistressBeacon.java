package cyclesofwar.players.andreasg.helper;

import cyclesofwar.players.andreasg.AndreasG;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class DistressBeacon
{

  public Planet from;
  public double forcesRequired;
  public double secondsRemaining;

  public DistressBeacon(Planet from, double forcesRequired, double secondsRemaining)
  {
    this.from = from;
    this.forcesRequired = forcesRequired;
    this.secondsRemaining = secondsRemaining;
  }

  public void evacuate()
  {
    if (secondsRemaining > from.getPlayer().getRoundDuration())
      return;
    
    if (from.getForces() < 1)
      return;

    Planet nearest = ((AndreasG)from.getPlayer()).getNearestFriendlyPlanet(from);
    if (nearest == null)
      return;

    from.getPlayer().sendFleet(from, (int)Math.floor(from.getForces()), nearest);
  }

}



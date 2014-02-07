package cyclesofwar.players.andreasg;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public abstract class AndreasG extends Player 
{
  
  /* handle stuff irrelevant for the actual strategy implementation */
  
  protected long round = 0;

  @Override
  public Color getPlayerBackColor() 
  {
    return new Color(0x43, 0x8C, 0x19);
  }

  @Override
  public Color getPlayerForeColor() 
  {
    return Color.BLACK;
  }

  @Override
  public String getCreatorsName() 
  {
    return "Andi";
  }

  protected void debug(String str)
  {
    if(System.getenv("DEBUG") != null)
      System.out.println(str);
  }

  /* this is used by Mutalisks to sort fleets by arrival time on a given planet */
  protected Planet thePlanetThatIAmCurrentlySortingFor;
  public Planet helper1()
  {
    return thePlanetThatIAmCurrentlySortingFor;
  }

  public Planet getNearestFriendlyPlanet(Planet p)
  {
    for (Planet q : p.getOthersByDistance())
      if (q.getPlayer() == p.getPlayer())
        return q;

    return null;
  }

}


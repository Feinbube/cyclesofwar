package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

public class Zerg extends Player 
{

  @Override
  protected void think() 
  {
    int spread_count = 3;

    int c = 0;
    for (Planet p : getPlanets())
      {
        List<Planet> neighbors = p.getOthersByDistance();
        int i = 0;
        int max_i = (int)p.getForces() / 10 + 1;

        for (Planet t : neighbors)
          {
            int x = (int)p.getForces();
            int y = (int)t.getForces();

            if (x > y)
              sendFleet(p, (int)(p.getForces() * 0.2 + 1), t);
            
            if (++i > max_i)
              break;
          }
      }
  }
  
  @Override
  public Color getPlayerBackColor() 
  {
    return Color.BLACK;
  }

  @Override
  public Color getPlayerForeColor() 
  {
    return Color.BLACK;
  }

  @Override
  public String getCreatorsName() 
  {
    return "AndreasG";
  }

}

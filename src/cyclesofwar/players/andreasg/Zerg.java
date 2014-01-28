package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.List;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

public class Zerg extends Player 
{

  protected boolean isMine(Planet t)
  {
    for (Planet x : getPlanets())
      if (x == t)
        return true;
    return false;
  }

  @Override
  protected void think() 
  {
    int spread_count = 3;

    int c = 0;
    for (Planet p : getPlanets())
      {
        List<Planet> neighbors = p.getOthersByDistance();
        int i = 0;
        int max_i = (int)p.getForces() / 5 + 2;

        for (Planet t : neighbors)
          {
            int x = (int)p.getForces();
            int y = (int)t.getForces();

            if (t.isFree() && x > y * 1.1)
              sendFleet(p, y + 1, t);
            //else if (isMine(t) && x > y * 1.2)
            //  sendFleet(p, 1, t);
            else if (!isMine(t) && x > y * 1.2 || x > 50)
              sendFleet(p, (int)(x * 0.7 + 1), t);

            if (++i > max_i)
              break;
          }
      }
  }
  
  @Override
  public Color getPlayerBackColor() 
  {
    return Color.WHITE;
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

}

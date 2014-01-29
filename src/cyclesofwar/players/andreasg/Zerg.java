package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

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

    List<Planet> sent_to = new ArrayList<Planet>();

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

            if (sent_to.contains(t))
              continue;

            if (t.isFree() && x > y * 1.1)
              {
                sendFleet(p, y + 1, t);
                sent_to.add(t);
              }
            else if (isMine(t) && x > 33 && y < 15)
              {
                sent_to.add(t);
                sendFleet(p, 1, t);
              }
            else if (!isMine(t) && x > y * 2.0)
              {
                sent_to.add(t);
                sendFleet(p, (int)(y * 1.5 + 1), t);
              }
            //else if (!isMine(t) && x > 66)
            //  {
            //    sendFleet(p, 44, t);
            //  }

            if (++i > max_i)
              break;
          }
      }
  }
  
  @Override
  public Color getPlayerBackColor() 
  {
    return new Color(0xF5, 0x5A, 0x0C);
  }

  @Override
  public Color getPlayerForeColor() 
  {
    return new Color(0x91, 0x8D, 0x8C);
  }

  @Override
  public String getCreatorsName() 
  {
    return "Andi";
  }

}

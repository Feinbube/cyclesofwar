package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

/* This class has probably reached its end of life and has been
 * superceded by the Hydralisks, which do an overall better job.
 *
 * last training score: 
 *   1,Theo's SpaceMenace,97,100
 *   2,Jan's FriendlyPirates,95,100
 *   3,Frank's Jane,93,100
 *   4,Robert's AttackLargestPlayer,73,100
 *   5,Andi's Zerglings,1237,2100
 *   6,B13_Rabbit,48,100
 *   7,B14_BraveRabbit,48,100
 *   8,B12_Closest,44,100
 *   9,B11_Cells,36,100
 *   10,B09_CloseBond,29,100
 *   11,Martin's Cratters,24,100
 *   12,B06_PowersOf2,18,100
 *   13,B05_Worm,16,100
 *   14,B10_Front,15,100
 *   15,B08_NewLeader,10,100
 *   16,B04_Random,5,100
 *   17,B07_Sniper,2,100
 *   18,Peter's DumbVirus,1,100
 *   19,B00_Idle,0,100
 *   20,B01_ChaseMe,0,100
 *   21,B02_Wave,0,100
 *   22,B03_BigGun,0,100
 *   23,Nobody,209,2100
 */ 

public class Zerglings extends AndreasG 
{

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
            else if (t.getPlayer() == this && x > 33 && y < 15)
              {
                sent_to.add(t);
                sendFleet(p, 1, t);
              }
            else if (t.getPlayer() != this && x > y * 2.0)
              {
                sent_to.add(t);
                sendFleet(p, (int)(y * 1.5 + 1), t);
              }

            if (++i > max_i)
              break;
          }
      }
  }
  
}

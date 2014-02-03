package cyclesofwar.players.andreasg;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Fleet;

public class Hydralisks extends Player 
{

  public class PlanetEvent
  {
    public long timestamp;
    public long forces;
    public Player player;

    public PlanetEvent(long timestamp, long forces, Player player)
    {
      this.timestamp = timestamp;
      this.forces = forces;
      this.player = player;
    }
  }

  public class PlanetTimeline
  {
    public Planet p;
    public List<PlanetEvent> events;

    public PlanetTimeline(Planet p)
    {
      this.p = p;
      this.events = new ArrayList<PlanetEvent>();
    }
  }

  private Map<Planet, PlanetTimeline> timelines;

  private long groundForces;
  private long airForces;
  private long production;

  @Override
  protected void think() 
  {
    init();


  }
  
  @Override
  public Color getPlayerBackColor() 
  {
    return new Color(0xF5, 0x5A, 0x0C);
  }

  @Override
  public Color getPlayerForeColor() 
  {
    return Color.WHITE;
  }

  @Override
  public String getCreatorsName() 
  {
    return "Andi";
  }

  private void init()
  {
    timelines = new HashMap<Planet, PlanetTimeline>();
    groundForces = 0;
    airForces = 0;
    production = 0;

    for (Planet p : getAllPlanets())
      {
        timelines.put(p, new PlanetTimeline(p));  
        if (p.getPlayer().equals(this))
          {
            groundForces += p.getForces();
            production += p.getProductionRatePerRound();
          }
      }

    for (Fleet f : getAllFleets())
      {
        timelines.get(f.getTarget()).events.add(new PlanetEvent(f.getRoundsToTarget(), f.getForce(), f.getPlayer()));
        if (f.getPlayer().equals(this))
          airForces += f.getForce();
      }

    for (Entry<Planet, PlanetTimeline> e : timelines.entrySet())
      appraise(e.getKey(), e.getValue());
  }

  private void appraise(Planet p, PlanetTimeline t)
  {
    double production = p.getProductionRatePerRound();
    Player owner = p.getPlayer();

    List<Planet> neighbors = p.getOthersByDistance();

    for (PlanetEvent e : t.events)
      {
        
      }
  }

}

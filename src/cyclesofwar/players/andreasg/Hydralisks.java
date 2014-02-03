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

    public double score;

    public PlanetTimeline(Planet p)
    {
      this.p = p;
      this.events = new ArrayList<PlanetEvent>();
    }
  }

  private Map<Planet, PlanetTimeline> timelines;
  private List<Planet> mine;

  private long groundForces;
  private long airForces;
  private long production;

  @Override
  protected void think() 
  {
    init();

    double max_score = 0;
    Planet p = null;

    for (Entry<Planet, PlanetTimeline> e : timelines.entrySet())
      {
        PlanetTimeline t = e.getValue();
        if (t.score > max_score)
          {
            max_score = t.score;
            p = t.p;
          }
      }

    if (max_score > 0)
      attack(p);
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
    mine = new ArrayList<Planet>();
    groundForces = 0;
    airForces = 0;
    production = 0;

    for (Planet p : getAllPlanets())
      {
        timelines.put(p, new PlanetTimeline(p));  
        if (p.getPlayer().equals(this))
          {
            mine.add(p);
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
      e.getValue().score = appraise(e.getKey(), e.getValue());
  }

  private double appraise(Planet p, PlanetTimeline t)
  {
    double production = p.getProductionRatePerRound();

    List<Planet> neighbors = p.getOthersByDistance();

    double value = production;
    double effort = p.getForces() / groundForces;

    Player owner = p.getPlayer();
    double forces = p.getForces();
    long lastevent = 0;
    for (PlanetEvent e : t.events)
      {
        forces += production * (e.timestamp - lastevent);
        if (e.player == owner)
          forces += e.forces;
        else
          {
            forces -= e.forces;
            if (forces < 0)
              {
                owner = e.player;
                forces = -forces;
              }
          }

        lastevent = e.timestamp;
      }

    if (owner == this)
      return 0;

    double distance = 0;
    for (Planet m : mine)
      distance += m.getRoundsTo(p);
    distance /= mine.size();

    return value / (effort * distance);
  }

  public void attack(Planet p)
  { 
    for (Planet m : mine)
      if (m.getForces() > 10 && p != m)
        sendFleet(m, (int)(m.getForces() / 2), p);
  }

}

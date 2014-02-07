package cyclesofwar.players.andreasg.helper;

import java.util.List;
import java.util.ArrayList;

import cyclesofwar.Planet;

import cyclesofwar.players.andreasg.helper.*;

public class DistressBeacons
{

  private List<DistressBeacon> _items;

  public DistressBeacons()
  {
    _items = new ArrayList<DistressBeacon>();
  }

  public void add(Planet from, double forcesRequired, int roundsRemaining)
  {
    _items.add(new DistressBeacon(from, forcesRequired, roundsRemaining));
  }

  public void add(DistressBeacon b)
  {
    _items.add(b);
  }

  public List<DistressBeacon> get()
  {
    return _items;
  }

  public void evacuate()
  {
    for (DistressBeacon b : _items)
      b.evacuate();
  }
  
}



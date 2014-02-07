package cyclesofwar.players.andreasg.helper;

import java.util.List;
import java.util.ArrayList;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Actions
{

  private List<Action> _items;

  public Actions()
  {
    _items = new ArrayList<Action>();
  }

  public void add(Planet from, Planet to, int forces)
  {
    _items.add(new Action(from, to, forces));
  }

  public void execute()
  {
    for (Action a : _items)
      a.execute();
  }

  private class Action
  {

    private Planet from;
    private Planet to;
    private int forces;

    private Action(Planet from, Planet to, int forces)
    {
      this.from = from;
      this.to = to;
      this.forces = forces;
    }

    private void execute()
    {
      if (forces > 0)
        from.getPlayer().sendFleet(from, forces, to);
    }

  }

}


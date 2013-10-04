package cyclesofwar.players.frank.gene;

import cyclesofwar.Planet;
import cyclesofwar.Player;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Gene extends Player {

    @Override
    protected void think() {
        SortedMap<Double, List<ValueEntry>> valueEntryMap = new TreeMap<>();

        for (Planet local : this.getPlanets()) {
            Value localValue = this.getValue(local);

            for (Planet other : local.getOthers()) {
                addValueEntry(valueEntryMap, local, localValue, other);
            }
        }

        for (double key : valueEntryMap.keySet()) {
            List<ValueEntry> valueEntries = valueEntryMap.get(key);
            this.shuffle(valueEntries);

            for (ValueEntry valueEntry : valueEntries) {
                if (valueEntry.isValueable()) {
                    this.sendFleet(valueEntry.getPlanet(), valueEntry.getForces(), valueEntry.getTarget());
                }
            }
        }
    }

    private void addValueEntry(SortedMap<Double, List<ValueEntry>> valueEntryMap, Planet local, Value localValue, Planet other) {
        Value otherValue = this.getValue(local, other);

        if (otherValue != null) {
            double priority = -otherValue.getPriority();
            if (!valueEntryMap.containsKey(priority)) {
                valueEntryMap.put(priority, new ArrayList<ValueEntry>());
            }

            valueEntryMap.get(priority).add(new ValueEntry(local, localValue, other, otherValue));
        }
    }

    protected abstract Value getValue(Planet local);
    protected abstract Value getValue(Planet local, Planet other);

    @Override
    public Color getPlayerBackColor() {
        return Color.magenta.darker();
    }

    @Override
    public Color getPlayerForeColor() {
        return Color.pink;
    }

    @Override
    public String getCreatorsName() {
        return "G";
    }
}

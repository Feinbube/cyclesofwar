package cyclesofwar.players.frank.gene;

public class Value {
    
    double priority;
    int forces;
    
    public Value(double priority, double forces) {
        this.priority = priority;
        this.forces = (int)forces;
    }

    public double getPriority() {
        return priority;
    }
    
    public int getForces() {
        return forces;
    }
}

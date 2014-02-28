package cyclesofwar.players.frank;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Valentine extends Player {

    @Override
    protected void think() {

        if (iAmTheStrongest()) {
            beABraveBunny();
            return;
        }

        for (Planet planet : this.getPlanets()) {

            if (planet.getForces() < 1) {
                continue;
            }

            double overproduction = planet.getForces() - new Advise(this, planet, this.getlastFleetArrivalTime()).getForcesToKeep();

            if (overproduction <= 1) {
                continue;
            }

            for (Planet other : planet.getOthersByDistance()) {

                double attackBalance = new Prediction(this, other, planet.getTimeTo(other)).getBalance();

                if (attackBalance > 0) {
                    continue;
                }

                Planet target = getTarget(planet, other);
                if (target.equals(planet)) {
                    if (-attackBalance < (int) overproduction) {
                        int force = (int) Math.max(1, Math.min(-attackBalance, overproduction));
                        this.sendFleet(planet, force, other);
                        overproduction -= force;
                    }
                } else {
                    int force = (int) overproduction;
                    this.sendFleet(planet, force, target);
                    overproduction -= force;
                }
                
                break;
            }
        }
    }
    
    private Planet getTarget(Planet planet, Planet other) {
        double c = planet.getDistanceTo(other);
        for (Planet closest : mineOnly(other.getOthersByDistance())) {
            double a = planet.getDistanceTo(closest);
            double b = other.getDistanceTo(closest);

            if (c < a || c * 1.5 < a + b) {
                continue;
            }
            
            return closest;
        }

        return planet;
    }

    private boolean iAmTheStrongest() {
        if (this.getPlanets().size() > this.getAllPlanets().size() / 1.5) {
            return true;
        }
        return false;
    }

    private void beABraveBunny() {
        if (getAllPlanetsButMine().isEmpty()) {
            return;
        }

        for (Planet planet : this.getPlanets()) {
            Planet target = this.hostileOnly(planet.getOthersByDistance()).get(0);
            if (planet.getForces() >= 31) {
                sendFleet(planet, 31, target);
            }
        }
    }

    @Override
    public Color getPlayerBackColor() {
        return Color.blue;
    }

    @Override
    public Color getPlayerForeColor() {
        return Color.orange;
    }

    @Override
    public String getCreatorsName() {
        return "Frank";
    }
}

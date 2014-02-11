package cyclesofwar.players.frank;

import java.awt.Color;

import cyclesofwar.Planet;
import cyclesofwar.Player;

public class Jane extends Player {

    @Override
    protected void think() {
        if (iAmTheStrongest()) {
            beABraveBunny();
            return;
        }

        for (Planet planet : this.getPlanets()) {

            if(planet.getForces() < 1) {
                continue;
            }
            
            double overproduction = planet.getForces() - new Advise(this, planet, this.getlastFleetArrivalTime()).getForcesToKeep();

            for (Planet other : planet.getOthersByDistance()) {

                if (planet.getForces() < 1 || overproduction < 1) {
                    break;
                }

                double attackBalance = new Prediction(this, other, planet.getTimeTo(other)).getBalance();

                if (-attackBalance > overproduction) {
                    break;
                }

                if (attackBalance < 0) {
                    int force = (int) Math.min(-attackBalance, overproduction);
                    this.sendFleet(planet, force, other);
                    overproduction -= force;
                }
            }
        }
    }

    private boolean iAmTheStrongest() {
        if(this.getPlanets().size() > this.getAllPlanets().size() / 1.5)
            return true;
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

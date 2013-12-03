package cyclesofwar.window.rendering;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.tournament.TournamentBook;
import cyclesofwar.tournament.TournamentRecord;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

public class ShuffleRendering extends Rendering {

    int switchDelay = 60;
    
    final List<Rendering> renderings;
    Rendering selected;
    long last;

    public ShuffleRendering(List<Rendering> renderings) {
        this.renderings = renderings; 
        select();
    }
    
    private void select() {
        if(System.currentTimeMillis() - last > switchDelay * 1000) {
            this.selected = renderings.get(random.nextInt(renderings.size()));
            last = System.currentTimeMillis();
        }
    }
    
    @Override
    public Font getFont(int style, int fontSize) {
        return selected.getFont(style, fontSize);
    }

    @Override
    public void drawBackground(Graphics g, long universeSeed) {
        selected.drawBackground(g, universeSeed);
    }

    @Override
    public void drawPlanet(Graphics g, Planet planet, int id) {
        selected.drawPlanet(g, planet, id);
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {
        selected.drawFleets(g, fleets, time);
    }

    @Override
    public void drawTitleScreen(Graphics g) {
        select();    
        selected.drawTitleScreen(g);
    }

    @Override
    public void drawMainMenu(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers, List<String> possibleRenderings, String selectedRendering, List<Integer> possibleNumbersOfRounds, Integer selectedNumberOfRounds, List<Integer> possiblePlanetsPerPlayer, Integer selectedNumberOfPlanetsPerPlayer, List<Double> possibleUniverseSizes, double selectedUniverseSize) {
        selected.drawMainMenu(g, selectedPlayers, allPlayers, possibleRenderings, selectedRendering, possibleNumbersOfRounds, selectedNumberOfRounds, possiblePlanetsPerPlayer, selectedNumberOfPlanetsPerPlayer, possibleUniverseSizes, selectedUniverseSize);
    }

    @Override
    public void drawStatistics(Graphics g, TournamentBook tournament, String s, boolean showDetails) {
        selected.drawStatistics(g, tournament, s, showDetails);
    }

    @Override
    public void drawSeed(Graphics g, long seed) {
        selected.drawSeed(g, seed);
    }

    @Override
    public void drawControlInfo(Graphics g, String s, int fontsize) {
        selected.drawControlInfo(g, s, fontsize);
    }

    @Override
    public void drawInfo(Graphics g, String s) {
        selected.drawInfo(g, s);
    }

    @Override
    public void drawPlayers(Graphics g, List<Player> players) {
        selected.drawPlayers(g, players);
    }

    @Override
    protected void drawUpdatedFps(Graphics g) {
        selected.drawUpdatedFps(g);
    }

    @Override
    protected void drawGameOverScreen(Graphics g, String winnerName) {
        selected.drawGameOverScreen(g, winnerName);
    }

    @Override
    public void setSize(Dimension size) {
        super.setSize(size);
        for(Rendering rendering : renderings) {
            rendering.setSize(size);
        }
    }
    
    @Override
    protected void applyNewSize() {
        for(Rendering rendering : renderings) {
            rendering.applyNewSize();
        }
    }
    
    @Override
    public void drawUniverse(Graphics g, Universe universe) {
        selected.drawUniverse(g, universe);
    }
    
    @Override
    public List<TournamentRecord> getFightRecords(int x, int y) {
        return selected.getFightRecords(x, y);
    }
    
    @Override
    public String getButtonTag(int x, int y) {
        return selected.getButtonTag(x, y);
    }
    
    @Override
    public Player getPlayer(int x, int y) {
        return selected.getPlayer(x, y);
    }

    @Override
    public void drawSelectedPlanet(Graphics g, Planet planet) {
        selected.drawSelectedPlanet(g, planet);
    }

    @Override
    public void drawSelectedForce(Graphics g, double selectedForceRatio) {
        selected.drawSelectedForce(g, selectedForceRatio);
    }

    @Override
    public double getUpdatedSelectedForce(int x, int y) {
        return selected.getUpdatedSelectedForce(x, y);
    }
}

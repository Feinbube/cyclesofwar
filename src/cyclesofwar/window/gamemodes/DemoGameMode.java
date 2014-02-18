package cyclesofwar.window.gamemodes;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.PlayerDisqualifiedException;
import cyclesofwar.Universe;
import cyclesofwar.tournament.TournamentBook;
import cyclesofwar.window.GameModes;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import cyclesofwar.window.GamePanel;
import java.util.Arrays;
import java.util.List;

public class DemoGameMode extends GameMode {

    private final int speed = 3;
    
    private final int titleBlendingTime = 50;
    private final int gameOverBlendingTime = 150;
    private final int statisticsBlendingTime = 100;
    
    private DemoStates state = DemoStates.TITLETOARENA;    
    
    private final TournamentBook oneOnOneTournament;
    private final TournamentBook lastManStandingTournament;
   
    private Universe universe;
    
    private int timecounter = 0;
    

    public enum DemoStates {

        TITLETOARENA, ARENA, ARENASTATS, TITLETOTOURNAMENT, TOURNAMENT, TOURNAMENTSTATS
    }

    public DemoGameMode(GamePanel gamePanel) {
        super(gamePanel);
        
        oneOnOneTournament = new TournamentBook(Arena.champions(), 200);
        lastManStandingTournament = new TournamentBook(Arena.champions(), 200);        
    }

    @Override
    protected void updateGame() {
        if (state == DemoStates.TITLETOARENA) {
            switchToTournamentOnTime(DemoStates.ARENA, getChampions());
        } else if (state == DemoStates.ARENA) {
            updateUniverse(DemoStates.ARENASTATS, oneOnOneTournament);
        } else if (state == DemoStates.ARENASTATS) {
            switchToDemoState(DemoStates.TITLETOTOURNAMENT);
        } else if (state == DemoStates.TITLETOTOURNAMENT) {
            switchToTournamentOnTime(DemoStates.TOURNAMENT, Arena.champions());
        } else if (state == DemoStates.TOURNAMENT) {
            updateUniverse(DemoStates.TOURNAMENTSTATS, lastManStandingTournament);
        } else if (state == DemoStates.TOURNAMENTSTATS) {
            switchToDemoState(DemoStates.TITLETOARENA);
        }
    }

    private List<Player> getChampions() {
        int i = random.nextInt(Arena.champions().size());
        int j = random.nextInt(Arena.champions().size() - 1);
        if(i >= j) {
            j++;
        }

        return Arrays.asList(Arena.champions().get(i), Arena.champions().get(j));
    }
    
    private void switchToTournamentOnTime(DemoStates state, List<Player> champions) {
        if (timecounter++ > titleBlendingTime) {
            timecounter = 0;
            this.state = state;
            universe = new Universe(random.nextLong(), champions, getSelectedNumberOfPlanetsPerPlayer(), getSelectedUniverseSizeFactor());
        }
    }
    
    private void switchToDemoState(DemoStates state) {
        if (timecounter++ > statisticsBlendingTime) {
            timecounter = 0;
            this.state = state;
        }
    }
    
    private void updateUniverse(DemoStates state, TournamentBook tournamentBook) {
        try {
            for(int i = 0; i<speed; i++){
                universe.update(Universe.getRoundDuration());
            }
        } catch (PlayerDisqualifiedException e) {
            throw new RuntimeException(e);
        }
        
        if(universe.isGameOver() && timecounter++ > gameOverBlendingTime) {
            tournamentBook.addFinishedGame(universe);
            timecounter = 0;
            this.state = state;
        }
    }

    @Override
    protected void paintGame(Graphics g) {
        
        if (state == DemoStates.TITLETOARENA) {
            getRendering().drawTitleScreen(g);
        } else if (state == DemoStates.ARENA) {
            getRendering().drawUniverse(g, universe);
            getRendering().drawPlayers(g, universe.getPlayers());
            // getRendering().drawSeed(g, universe.getSeed());
            getRendering().drawFps(g);
        } else if (state == DemoStates.ARENASTATS) {
            getRendering().drawStatistics(g, oneOnOneTournament, "1-On-1 Tournament", true);
        } else if (state == DemoStates.TITLETOTOURNAMENT) {
            getRendering().drawTitleScreen(g);
        } else if (state == DemoStates.TOURNAMENT) {
            getRendering().drawUniverse(g, universe);
            getRendering().drawPlayers(g, universe.getPlayers());
            // getRendering().drawSeed(g, universe.getSeed());
            getRendering().drawFps(g);
        } else if (state == DemoStates.TOURNAMENTSTATS) {
            getRendering().drawStatistics(g, lastManStandingTournament, "Last Man Standing Tournament", false);
        }
        
        if (state != DemoStates.TITLETOARENA && state != DemoStates.TITLETOTOURNAMENT) {
            getRendering().drawControlInfo(g, "Join the action: svn://code.hpi.uni-potsdam.de/cyclesofwar", 16);
        }
    }

    @Override
    protected void drawControls(Graphics g) {
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.switchTo(GameModes.PLAYERSELECTION);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
    }

    @Override
    public void mouseReleased(int x, int y) {
    }

    @Override
    public void mouseMoved(int x, int y) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void reset() {
    }
}

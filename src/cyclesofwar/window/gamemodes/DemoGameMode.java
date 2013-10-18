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
    private final int titleBlendingTime = 80;
    private final int statisticsBlendingTime = 300;
    
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
        
        oneOnOneTournament = new TournamentBook(Arena.champions(), Arena.planetsPerPlayer, Arena.universeSizeFactor);
        lastManStandingTournament = new TournamentBook(Arena.champions(), Arena.planetsPerPlayer, Arena.universeSizeFactor);
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
        if(i == j) {
            j++;
        }

        return Arrays.asList(Arena.champions().get(i), Arena.champions().get(j));
    }
    
    private void switchToTournamentOnTime(DemoStates state, List<Player> champions) {
        if (timecounter++ > titleBlendingTime) {
            timecounter = 0;
            this.state = state;
            universe = new Universe(random.nextLong(), champions, Arena.planetsPerPlayer, Arena.universeSizeFactor);
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
        
        if(universe.isGameOver() && timecounter++ > titleBlendingTime) {
            tournamentBook.addFinishedGame(universe);
            timecounter = 0;
            this.state = state;
        }
    }

    @Override
    protected void paintGame(Graphics g) {
        if (state == DemoStates.TITLETOARENA) {
            rendering.drawTitleScreen(g);
        } else if (state == DemoStates.ARENA) {
            rendering.drawUniverse(g, universe);
        } else if (state == DemoStates.ARENASTATS) {
            rendering.drawStatistics(g, oneOnOneTournament, "1-On-1 Tournament", true);
        } else if (state == DemoStates.TITLETOTOURNAMENT) {
            rendering.drawTitleScreen(g);
        } else if (state == DemoStates.TOURNAMENT) {
            rendering.drawUniverse(g, universe);
        } else if (state == DemoStates.TOURNAMENTSTATS) {
            rendering.drawStatistics(g, lastManStandingTournament, "Last Man Standing Tournament", false);
        }
    }

    @Override
    protected void drawControls(Graphics g) {
    }

    @Override
    protected void keyPressedGame(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.switchTo(GameModes.PLAYERSELECTION);
        }
    }

    @Override
    protected void mousePressedGame(int x, int y) {
    }

    @Override
    protected void mouseReleasedGame(int x, int y) {
    }

    @Override
    protected void mouseMovedGame(int x, int y) {
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

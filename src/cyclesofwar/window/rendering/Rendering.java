package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.tournament.TournamentBook;
import cyclesofwar.tournament.TournamentRecord;

public abstract class Rendering {

    protected abstract class DrawContentProvider {
        abstract String getString(TournamentBook tournament, int i);
        Object getTag(TournamentBook tournament, int i) { return null; }
    }
    
    protected class ValueAndColor {
        public double value;
        public Color color;

        public ValueAndColor(double value, Color color) {
            this.value = value;
            this.color = color;
        }               
    }

    protected enum HAlign { LEFT, CENTER, RIGHT }
    protected enum VAlign { TOP, CENTER, BOTTOM }
    
    protected Dimension size = new Dimension(800, 480);
    protected double universeSize = 0.0;   
    protected int borderSize = 20;
    
    protected final Random random = new Random();
    
    protected final int MaxRenderedFleet = 20;
    
    protected final List<Tag> tags = new ArrayList<>();
    protected final static Fps fps = new Fps();
    
    public Rendering() {
    }

    public abstract Font getFont(int style, int fontSize);
    
    public abstract void drawBackground(Graphics g, long universeSeed);
    public abstract void drawPlanets(Graphics g, List<Planet> planets);
    public abstract void drawFleets(Graphics g, List<Fleet> fleets, double time);
    
    public abstract void drawTitleScreen(Graphics g);
    public abstract void drawMainMenu(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers,
            List<Integer> possibleNumbersOfRounds, Integer selectedNumberOfRounds, List<Integer> possiblePlanetsPerPlayer,
            Integer selectedNumberOfPlanetsPerPlayer, List<Double> possibleUniverseSizes, double selectedUniverseSize);
    public abstract void drawStatistics(Graphics g, TournamentBook tournament, String s, boolean showDetails);
    
    public abstract void drawSeed(Graphics g, long seed);
    public abstract void drawControlInfo(Graphics g, String s, int fontsize);
    public abstract void drawInfo(Graphics g, String s);
    public abstract void drawPlayers(Graphics g, List<Player> players);
    
    protected abstract void drawUpdatedFps(Graphics g);
    protected abstract void drawGameOverScreen(Graphics g, String winnerName);
    
    protected abstract void applyNewSize();
    
    public void setSize(Dimension size) {
        if (!this.size.equals(size)) {
            this.size = size;
            this.borderSize = planetSize(size.width, 5.0) / 2 + 30;
            applyNewSize();
        }     
    }
    
    public double getScaled(double value) {
        return value * 0.5 + value * 0.5 * size.width / 1000.0;
    }
    
    protected int planetSize(int size, double productionRatePerSecond) {
        return (int) ((productionRatePerSecond + 1.0) * 7.0 * size / 1000.0);
    }

    public void drawUniverse(Graphics g, Universe universe) {
        if (this.universeSize != universe.getSize()) {
            this.universeSize = universe.getSize();
        }

        drawBackground(g, universe.getSeed());

        if (!universe.isGameOver()) {
            drawPlanets(g, universe.getAllPlanets());
            drawFleets(g, universe.getAllFleets(), universe.getNow());
        } else {
            drawGameOverScreen(g, universe.getWinner().getName());
        }
    }

    public void drawFps(Graphics g) {    
        fps.update();
        drawUpdatedFps(g);
    }

    public void drawControlInfo(Graphics g, String s) {
        drawControlInfo(g, s, 12);
    }

    protected double getX(Graphics g, double x) {
        return ((size.width - borderSize * 2) * x / universeSize) + borderSize;
    }

    protected double getY(Graphics g, double y) {
        return ((size.height - borderSize * 2) * y / universeSize) + borderSize;
    }

    protected List<ValueAndColor> planetData(List<Player> players) {
        List<ValueAndColor> result = new ArrayList<>();
        for(Player player : players) {
            result.add(new ValueAndColor(player.getPlanets().size(), player.getPlayerBackColor()));
        }
        return result;
    }
    
    protected List<ValueAndColor> fleetData(List<Player> players) {
        List<ValueAndColor> result = new ArrayList<>();
        for(Player player : players) {
            result.add(new ValueAndColor(player.getFleets().size(), player.getPlayerBackColor()));
        }
        return result;
    }
    
    protected List<ValueAndColor> forceData(List<Player> players) {
        List<ValueAndColor> result = new ArrayList<>();
        for(Player player : players) {
            result.add(new ValueAndColor(player.getFullForce(), player.getPlayerBackColor()));
        }
        return result;
    }
    
    protected String shortInfo(Player player) {
        String result = player.getName();

        int planets = player.getPlanets().size();
        int planetForces = (int) player.getGroundForce();
        if (planets > 0) {
            result += " P[" + planets + "/" + planetForces + "]";
        }

        int fleets = player.getFleets().size();
        int fleetForces = player.getSpaceForce();
        if (fleets > 0) {
            result += " F[" + fleets + "/" + fleetForces + "]";
        }

        return result;
    }

    protected String percentage(double value) {
        return (int) (value * 100) + "%";
    }

    protected int drawRank(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return (i + 1) + ".";
            }
        });
    }

    protected int drawWins(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return tournament.getRankings().get(i).getWins() + "/" + tournament.getRankings().get(i).getGames();
            }

            @Override
            public Object getTag(TournamentBook tournament, int i) {
                return tournament.wonBy(tournament.getRankings().get(i).getPlayer());
            }
        });
    }

    protected int drawPerformance(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return percentage(tournament.getRankings().get(i).getRatio());
            }

            @Override
            public Object getTag(TournamentBook tournament, int i) {
                return tournament.wonBy(tournament.getRankings().get(i).getPlayer());
            }
        });
    }

    protected int drawNames(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return tournament.getRankings().get(i).getPlayer().getName();
            }
        });
    }

    protected int drawPerformanceAgainst(Graphics g, TournamentBook tournament, int rank, Font f, int marginLeft, int marginTop) {
        final Player competitor = tournament.getRankings().get(rank).getPlayer();
        int maxPos = drawText(g, marginLeft, marginTop, (rank + 1) + ".", competitor.getPlayerForeColor(), competitor.getPlayerBackColor(), f);

        return drawContent(g, tournament, f, marginLeft, marginTop, maxPos, new DrawContentProvider() {

            @Override
            public String getString(TournamentBook tournament, int i) {
                Player player = tournament.getRankings().get(i).getPlayer();

                if (player != competitor) {
                    return percentage(tournament.winsOver(player, competitor).size() / (double) tournament.fightsAgainst(player, competitor).size());
                } else {
                    return "--";
                }
            }

            @Override
            public Object getTag(TournamentBook tournament, int i) {
                Player player = tournament.getRankings().get(i).getPlayer();

                if (player != competitor) {
                    return tournament.winsOver(player, competitor);
                } else {
                    return null;
                }
            }
        });
    }

    protected void remember(int x, int y, int w, int h, Object tag) {
        tags.add(0, new Tag(x, y, w, h, tag));
    }

    protected int drawContent(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop, DrawContentProvider drawContentProvider) {
        return drawContent(g, tournament, f, marginLeft, marginTop, 0, drawContentProvider);
    }

    protected int drawContent(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop, int maxpos, DrawContentProvider drawContentProvider) {
        int maxPos = maxpos;
        int h = g.getFontMetrics(f).getHeight();
        for (int i = 0; i < tournament.getRankings().size(); i++) {
            Player player = tournament.getRankings().get(i).getPlayer();
            int pos = drawText(g, marginLeft, marginTop + (h + 2) * (i + 1), drawContentProvider.getString(tournament, i),
                    player.getPlayerForeColor(), null, f);
            Object tag = drawContentProvider.getTag(tournament, i);
            if (tag != null) {
                remember(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, pos - marginLeft + 3, g.getFontMetrics(f).getHeight(), tag);
            }
            if (pos > maxPos) {
                maxPos = pos;
            }
        }
        return maxPos;
    }

    protected int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, Font font) {
        return drawText(g, x, y, s, fc, bc, HAlign.LEFT, VAlign.BOTTOM, font);
    }

    protected int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, int fontSize) {
        return drawText(g, x, y, s, fc, bc, hAlgin, vAlign, getFont(Font.PLAIN, fontSize));
    }

    protected int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, Font font) {
        g.setFont(font);

        int w = g.getFontMetrics(font).stringWidth(s);
        int h = g.getFontMetrics(font).getHeight();
        
        x = alignHorizontally(hAlgin, x, w);
        y = alignVertically(vAlign, y, h);

        if (bc != null) {
            g.setColor(bc);
            g.fillRect(x - 2, y + 4, w + 3, h);
        }

        g.setColor(fc);
        g.drawString(s, x, y + h);

        return x + w;
    }

    protected int alignHorizontally(HAlign hAlgin, int x, int w) {
        if (hAlgin == HAlign.CENTER) {
            x -= w / 2;
        } else if (hAlgin == HAlign.RIGHT) {
            x -= w;
        }
        return x;
    }
    
    protected int alignVertically(VAlign vAlign, int y, int h) {
        if (vAlign == VAlign.CENTER) {
            y -= h / 2;
        } else if (vAlign == VAlign.TOP) {
            y -= h;
        }
        return y;
    }

    public <T> T getTagAtPosition(Class c, int x, int y) {
        for (Tag tag : tags) {
            if (tag.intersects(x, y)) {
                if (c.isAssignableFrom(tag.object.getClass())) {
                    return (T) tag.object;
                }
            }
        }

        return null;
    }

    public List<TournamentRecord> getFightRecords(int x, int y) {
        return getTagAtPosition(List.class, x, y);
    }

    public Player getPlayer(int x, int y) {
        return (Player) getTagAtPosition(Player.class, x, y);
    }

    public String getButtonCaption(int x, int y) {
        return getTagAtPosition(String.class, x, y);
    }
}

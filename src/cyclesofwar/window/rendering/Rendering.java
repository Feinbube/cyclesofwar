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

public class Rendering {

    final int StarCount = 1000;
    final int MaxRenderedFleet = 20;

    abstract class DrawContentProvider {

        abstract String getString(TournamentBook tournament, int i);

        Object getTag(TournamentBook tournament, int i) {
            return null;
        }
    }

    enum HAlign {

        LEFT, CENTER, RIGHT
    }

    enum VAlign {

        TOP, CENTER, BOTTOM
    }

    private Dimension size = new Dimension(800, 480);
    private double universeSize = 0.0;
    private int borderSize = 20;

    private final List<Star> stars = new ArrayList<>();

    private final Random random = new Random();
    private final List<Tag> tags = new ArrayList<>();
    
    private Background background;

    public Rendering() {
        stars.clear();
        for (int i = 0; i < StarCount; i++) {
            stars.add(new Star(random));
        }
        this.background = new Background(size.width, size.height);
    }

    public void setSize(Dimension size) {
        if (!this.size.equals(size)) {
            this.size = size;
            this.borderSize = planetSize(size.width, 5.0) / 2;
        }
        if (this.background.getWidth() != size.width || this.background.getHeight() != size.height) {
            this.background = new Background(size.width, size.height);	
        }
    }

    public Font getFont(int style, int fontSize) {
        return new Font("Courier New", style, (int) (fontSize * 0.5 + fontSize * 0.5 * size.width / 1000.0));
    }

    public void drawUniverse(Graphics g, Universe universe) {
        if (this.universeSize != universe.getSize()) {
            this.universeSize = universe.getSize();
        }

        drawBackground(g);

        if (!universe.isGameOver()) {
            drawPlanets(g, universe.getAllPlanets());
            drawFleets(g, universe.getAllFleets(), universe.getNow());
            drawPlayers(g, universe.getPlayers());
        } else {
            drawGameOverScreen(g, universe.getWinner().getName());
        }
    }

    private void drawBackground(Graphics g) {
//        g.setColor(Color.black);
//        g.fillRect(0, 0, size.width, size.height);
//
//        drawStars(g);
        
        g.drawImage(this.background.getImage(), 0, 0, null);
    }

    public void drawSeed(Graphics g, long seed) {
        drawText(g, size.width - 5, 5, "seed: " + seed, Color.yellow, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
    }

    public void drawControlInfo(Graphics g, String s) {
        drawControlInfo(g, s, 12);
    }

    public void drawControlInfo(Graphics g, String s, int fontsize) {
        drawText(g, size.width - fontsize / 2, size.height - fontsize / 2, s, Color.yellow, null, HAlign.RIGHT, VAlign.TOP, fontsize);
    }

    public void drawInfo(Graphics g, String s) {
        drawText(g, size.width / 2, size.height / 2, s, Color.yellow, null, HAlign.CENTER, VAlign.CENTER, getFont(Font.BOLD, 32));
    }

    public void drawTitleScreen(Graphics g) {
        drawBackground(g);
        drawText(g, size.width / 2, size.height / 2, "Cycles of War", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.BOLD, 48));
    }

    private void drawGameOverScreen(Graphics g, String winnerName) {
        drawText(g, size.width / 2, size.height / 3 + 20, "GAME OVER", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.BOLD, 48));

        drawText(g, size.width / 2, size.height / 3 + 100, winnerName + " has won!", Color.yellow, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.PLAIN, 32));
    }

    private void drawStars(Graphics g) {
        for (Star star : stars) {
            g.setColor(star.c);

            int starSize = (int) (star.d * 4 + 1);
            int x = (int) (size.width * star.x);
            int y = (int) (size.height * star.y);
            g.fillOval(x - starSize / 2, y - starSize / 2, starSize, starSize);
        }
    }

    private void drawPlanets(Graphics g, List<Planet> planets) {
        for (Planet planet : planets) {
            final int x = (int) getX(g, planet.getX());
            final int y = (int) getY(g, planet.getY());
            final int planetSize = planetSize(g, planet.getProductionRatePerSecond());
            
            final int uX = x - (planetSize >> 1);
            final int uY = y - (planetSize >> 1);

            g.setColor(planet.getPlayer().getPlayerBackColor());
            g.fillOval(uX, uY, planetSize, planetSize);

            drawText(g, x, y, ((int) planet.getForces()) + "", planet.getPlayer().getPlayerForeColor(), null, HAlign.CENTER, VAlign.CENTER,
                    10);
        }
    }

    private int planetSize(Graphics g, double productionRatePerSecond) {
        return planetSize(size.width, productionRatePerSecond);
    }

    private int planetSize(int size, double productionRatePerSecond) {
        return (int) (productionRatePerSecond * 7.0 * size / 1000.0);
    }

    private double getX(Graphics g, double x) {
        return ((size.width - borderSize * 2) * x / universeSize) + borderSize;
    }

    private double getY(Graphics g, double y) {
        return ((size.height - borderSize * 2) * y / universeSize) + borderSize;
    }

    private void drawFleets(Graphics g, List<Fleet> fleets, double time) {
        for (Fleet fleet : fleets) {
            g.setColor(fleet.getPlayer().getPlayerBackColor());

            double x = getX(g, fleet.getX());
            double y = getY(g, fleet.getY());
            int d = fleet.getForce();

            if (d > MaxRenderedFleet) {
                d = MaxRenderedFleet;
            }

            double localTime = time - fleet.getTimeToTarget();

            if (fleet.getFormation() == Fleet.Formation.ARROW) {
                drawArrowFormation(g, fleet, x, y, d, true, localTime);
            } else if (fleet.getFormation() == Fleet.Formation.V) {
                drawArrowFormation(g, fleet, x, y, d, false, localTime);
            }
            if (fleet.getFormation() == Fleet.Formation.SWARM) {
                drawSwarmFormation(g, fleet, x, y, d, true, localTime);
            } else if (fleet.getFormation() == Fleet.Formation.O) {
                drawSwarmFormation(g, fleet, x, y, d, false, localTime);
            } else if (fleet.getFormation() == Fleet.Formation.EYE) {
                drawSwarmFormation(g, fleet, x, y, d, false, localTime);
                if (d < MaxRenderedFleet) {
                    g.setColor(fleet.getPlayer().getPlayerForeColor());
                    g.fillOval((int) x - 1, (int) y - 1, 5, 5);
                }
            } else if (fleet.getFormation() == Fleet.Formation.SPIRAL) {
                drawSpiralFormation(g, fleet, x, y, d, localTime);
            }

            if (d == MaxRenderedFleet) {
                drawText(g, (int) x, (int) y, fleet.getForce() + "", fleet.getPlayer().getPlayerForeColor(), null, HAlign.CENTER,
                        VAlign.CENTER, 10);
            }
        }
    }

    private void drawArrowFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
        double xDiff = fleet.getTarget().getX() - fleet.getX();
        double yDiff = fleet.getTarget().getY() - fleet.getY();

        xDiff *= 2.0;

        double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

        double sinAngle = yDiff / dist;
        double cosAngle = xDiff / dist;

        double length = Math.sqrt(d) * 5;
        for (int i = 0; i < d; i++) {
            double localx = random.nextDouble() * length;
            double localy;
            if (filled) {
                localy = random.nextDouble() * localx - localx / 2.0;
            } else {
                localy = (random.nextDouble() * 0.2 + 0.8) * localx - localx / 2.0;
                localy *= random.nextDouble() > 0.5 ? 1.0 : -1.0;
            }
            localy = localy * Math.sin(time);
            localx -= length / 2;

            double renderx = localx * cosAngle + localy * sinAngle;
            double rendery = -localx * sinAngle + localy * cosAngle;

            renderx = x - renderx;
            rendery = y + rendery;

            g.fillOval((int) renderx, (int) rendery, 3, 3);
        }
    }

    private void drawSwarmFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
        for (int i = 0; i < d; i++) {
            double r;
            if (filled) {
                r = random.nextDouble() * Math.sqrt(d) * 5;
            } else {
                r = (random.nextDouble() * 0.1 + 0.9) * Math.sqrt(d) * 5;
            }
            r = r * 0.5 + r * Math.sin(time);
            double v = random.nextDouble() * r;
            double localX = r * Math.cos(v);
            double localY = r * Math.sin(v);

            g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 3, 3);
        }
    }

    private void drawSpiralFormation(Graphics g, Fleet fleet, double x, double y, int d, double time) {
        for (int i = 0; i < d; i++) {
            double rnd = random.nextDouble();
            double r = rnd * Math.sqrt(d) * 7;
            double v = rnd * -10;

            double localX = r * Math.cos(v);
            double localY = -r * Math.sin(v);

            g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 2, 2);
        }
    }

    private void drawPlayers(Graphics g, List<Player> players) {
        int h = g.getFontMetrics().getHeight();

        players = Player.sortedBy(Player.PlanetCountComparator, players);
        int row = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(!player.isAlive())
                continue;

            drawText(g, 5, row * (h + 4) + 5, shortInfo(player), player.getPlayerForeColor(), player.getPlayerBackColor(), HAlign.LEFT,
                    VAlign.CENTER, 12);
            row++;
        }
    }

    private String shortInfo(Player player) {
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

    public void drawPlayerSelection(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers,
            List<Integer> possibleNumbersOfRounds, Integer selectedNumberOfRounds, List<Integer> possiblePlanetsPerPlayer,
            Integer selectedNumberOfPlanetsPerPlayer, List<Double> possibleUniverseSizes, double selectedUniverseSize) {
        drawBackground(g);

        int marginLeft = 60;
        int marginTop = 40;

        tags.clear();

        Font f = getFont(Font.BOLD, 18);
        drawText(g, marginLeft, marginTop - 12, "chose champions:", Color.yellow, null, f);

        drawPlayers(g, selectedPlayers, allPlayers, marginLeft, marginTop);

        int left = 0;
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 190, "chose number of matches:",
                        Color.yellow, null, f));
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 150,
                        "chose number of planets per player:", Color.yellow, null, f));
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 110, "chose universe size factor:",
                        Color.yellow, null, f));

        drawSelection(g, "rounds", possibleNumbersOfRounds, selectedNumberOfRounds, marginLeft, marginTop + 190, left);
        drawSelection(g, "planets", possiblePlanetsPerPlayer, selectedNumberOfPlanetsPerPlayer, marginLeft, marginTop + 150, left);
        drawSelection(g, "sizefactors", possibleUniverseSizes, selectedUniverseSize, marginLeft, marginTop + 110, left);

        drawText(g, marginLeft, size.height - marginTop - 100, "chose game mode:", Color.yellow, null, f);

        int wSteps = (size.width-marginLeft*2) / 5;
        int w = (int)(wSteps * 0.7);
        drawFancyButton(g, "Live Mode", "LIVE", marginLeft + wSteps, size.height - marginTop, w, 60, HAlign.CENTER, 22, 5, Color.blue.brighter(), Color.cyan);
        drawFancyButton(g, "Tournament Mode", "TOURNAMENT", marginLeft + wSteps*2, size.height - marginTop, w, 60, HAlign.CENTER, 20, 5, Color.blue.brighter(), Color.cyan);
        drawFancyButton(g, "Arena Mode", "ARENA",  marginLeft + wSteps*3, size.height - marginTop, w, 60, HAlign.CENTER, 22, 5, Color.blue.brighter(), Color.cyan);
        drawFancyButton(g, "Demo Mode","DEMO", marginLeft + wSteps*4, size.height - marginTop, w, 60, HAlign.CENTER, 22, 5, Color.blue.brighter(), Color.cyan);
    }

    private <T> void drawSelection(Graphics g, String id, List<T> possibleValues, T selectedValue, int marginLeft, int marginTop, int left) {
        int borderSize = (int) (size.width * 7.0 / 1000);
        int w = size.width - marginLeft * 3 - left;
        int tile = (int) (w / (double) (possibleValues.size() - 1));
        for (int i = 0; i < possibleValues.size(); i++) {
            T numberAtI = possibleValues.get(i);
            if (numberAtI.equals(selectedValue)) {
                drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
                        borderSize, Color.yellow);
            } else {
                drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
                        borderSize, Color.white);
            }
        }
    }

    private void drawPlayers(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers, int marginLeft, int marginTop) {
        Font f = getFont(Font.PLAIN, 16);

        int h = g.getFontMetrics(f).getHeight();
        int posX = marginLeft;
        int posY = marginTop + 24;
        for (Player player : allPlayers) {
            int w = g.getFontMetrics(f).stringWidth(player.getName());
            if (posX + w > size.width - marginLeft) {
                posX = marginLeft;
                posY += h + 6;
            }
            drawText(g, posX, posY, player.getName(), player.getPlayerForeColor(), player.getPlayerBackColor(), f);
            remember(posX, posY, w, h, player);
            if (selectedPlayers.contains(player)) {
                drawBorder(g, posX, posY, w, h);
            }
            posX += w + 12;
        }
    }

    private void drawButton(Graphics g, String id, String caption, int x, int y, HAlign hAlign, int fontSize, int buttonBorderSize, Color c) {
        Font f = getFont(Font.BOLD, fontSize);

        int w = g.getFontMetrics(f).stringWidth(caption);
        int h = g.getFontMetrics(f).getHeight();

        x = alignHorizontally(hAlign, x, w);

        float[] hsv = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);

        drawText(g, x, y - h, caption, Color.black, Color.white, f);
        remember(x - buttonBorderSize, y - h - buttonBorderSize, w + buttonBorderSize * 2, h + buttonBorderSize * 2, id);
        for (int i = 1; i < buttonBorderSize; i++) {
            g.setColor(Color.getHSBColor(hsv[0], hsv[1], (float) ((2.0 * buttonBorderSize) / (i + buttonBorderSize))));
            g.drawRect(x - i - 2, y - i + 4 - h, w + 2 * i + 2, h + 2 * i - 1);
        }
    }
    
    private void drawFancyButton(Graphics g, String id, String caption, int x, int y, int w, int h, HAlign hAlign, int fontSize, int buttonBorderSize, Color bc, Color fc) {
        Font f = getFont(Font.BOLD, fontSize);

        if (hAlign == HAlign.RIGHT) {
            x = size.width - w - x;
        } else {
            if (hAlign == HAlign.CENTER) {
                x = x - w / 2;
            }
        }

        bc = new Color(bc.getRed(), bc.getGreen(), bc.getBlue(), 32);
        
        g.setColor(bc);
        g.fillRect(x, y-h, w, h);
        
        fc = new Color(fc.getRed(), fc.getGreen(), fc.getBlue(), 128);
        g.setColor(fc);
        
        g.fillRect(x, y-h, w, buttonBorderSize);
        g.fillRect(x, y-buttonBorderSize, w, buttonBorderSize);
        g.fillRect(x, y-h+buttonBorderSize, buttonBorderSize, h-buttonBorderSize*2);
        g.fillRect(x+w-buttonBorderSize, y-h+buttonBorderSize, buttonBorderSize, h-buttonBorderSize*2);
        
        g.fillPolygon(
                new int[]{x+buttonBorderSize, x+buttonBorderSize+buttonBorderSize*4+buttonBorderSize*3, x+buttonBorderSize+buttonBorderSize*4, x+buttonBorderSize}, 
                new int[]{y-h+buttonBorderSize, y-h+buttonBorderSize, y-h+buttonBorderSize+buttonBorderSize*2, y-h+buttonBorderSize+buttonBorderSize*2}, 4);
        
        g.fillPolygon(
                new int[]{x+w-buttonBorderSize, x+w-buttonBorderSize-buttonBorderSize*4-buttonBorderSize*3, x+w-buttonBorderSize-buttonBorderSize*4, x+w-buttonBorderSize}, 
                new int[]{y-buttonBorderSize, y-buttonBorderSize, y-buttonBorderSize-buttonBorderSize*2, y-buttonBorderSize-buttonBorderSize*2}, 4);
        
        fc = new Color(fc.getRed(), fc.getGreen(), fc.getBlue(), 255);
        drawText(g, x+w/2, y-h/2, caption, fc, null, HAlign.CENTER, VAlign.CENTER, f);
        
        remember(x, y - h, w, h, id);
    }

    private void drawBorder(Graphics g, int posX, int posY, int w, int h) {
        g.setColor(Color.white);
        g.drawRect(posX - 3 - 1, posY - 3 + 7, w + 6, h + 6 - 6);
        g.setColor(Color.green);
        g.drawRect(posX - 2 - 1, posY - 2 + 7, w + 4, h + 4 - 6);
        g.setColor(Color.black);
        g.drawRect(posX - 1 - 1, posY - 1 + 7, w + 2, h + 2 - 6);
    }

    public void drawStatistics(Graphics g, TournamentBook tournament, String s, boolean showDetails) {
        drawBackground(g);

        Font f = getFont(Font.PLAIN, 14);

        int marginLeft = 50;
        int padding = 10;
        int marginTop = 120;

        drawText(g, marginLeft, marginLeft, s, Color.yellow, null, HAlign.LEFT, VAlign.CENTER, getFont(Font.BOLD, 32));

        tournament = tournament.lightWeightClone();

        tags.clear();
        drawLines(g, tournament, f, marginLeft, marginTop);

        int pos = drawRank(g, tournament, f, marginLeft, marginTop);
        pos = drawWins(g, tournament, f, pos + padding, marginTop);
        pos = drawPerformance(g, tournament, f, pos + padding, marginTop);
        pos = drawNames(g, tournament, f, pos + padding, marginTop);

        if (showDetails) {
            for (int i = 0; i < tournament.getChampions().size(); i++) {
                pos = drawPerformanceAgainst(g, tournament, i, f, pos + padding, marginTop);
            }
        }

        int h = g.getFontMetrics(f).getHeight();
        for (int i = 0; i < tournament.getRankings().size(); i++) {
            Player player = tournament.getRankings().get(i).getPlayer();
            if (tournament.hasPriority(player)) {
                drawBorder(g, marginLeft, marginTop + (h + 2) * (i + 1), size.width - marginLeft * 2, h);
            }
        }

        String text = tournament.getGamesPlayedCount() + " games played.";
        if (tournament.getGamesToPlayCount() > 0) {
            text = tournament.getGamesToPlayCount() + " games left, " + text;
        }
        drawText(g, size.width - marginLeft, marginTop + (h + 2) * (tournament.getRankings().size() + 1), text, Color.white, Color.black, HAlign.RIGHT, VAlign.BOTTOM, f);
    }

    private String percentage(double value) {
        return (int) (value * 100) + "%";
    }

    private void drawLines(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        int h = g.getFontMetrics(f).getHeight();
        for (int i = 0; i < tournament.getRankings().size(); i++) {
            Player player = tournament.getRankings().get(i).getPlayer();
            g.setColor(player.getPlayerBackColor());
            g.fillRect(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight());
            remember(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight(), player);
        }
    }

    private int drawRank(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return (i + 1) + ".";
            }
        });
    }

    private int drawWins(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
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

    private int drawPerformance(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
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

    private int drawNames(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        return drawContent(g, tournament, f, marginLeft, marginTop, new DrawContentProvider() {
            @Override
            public String getString(TournamentBook tournament, int i) {
                return tournament.getRankings().get(i).getPlayer().getName();
            }
        });
    }

    private int drawPerformanceAgainst(Graphics g, TournamentBook tournament, int rank, Font f, int marginLeft, int marginTop) {
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

    private void remember(int x, int y, int w, int h, Object tag) {
        tags.add(0, new Tag(x, y, w, h, tag));
    }

    private int drawContent(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop, DrawContentProvider drawContentProvider) {
        return drawContent(g, tournament, f, marginLeft, marginTop, 0, drawContentProvider);
    }

    private int drawContent(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop, int maxpos, DrawContentProvider drawContentProvider) {
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

    private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, Font font) {
        return drawText(g, x, y, s, fc, bc, HAlign.LEFT, VAlign.BOTTOM, font);
    }

    private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, int fontSize) {
        return drawText(g, x, y, s, fc, bc, hAlgin, vAlign, getFont(Font.PLAIN, fontSize));
    }

    private int drawText(Graphics g, int x, int y, String s, Color fc, Color bc, HAlign hAlgin, VAlign vAlign, Font font) {
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

    private int alignHorizontally(HAlign hAlgin, int x, int w) {
        if (hAlgin == HAlign.CENTER) {
            x -= w / 2;
        } else if (hAlgin == HAlign.RIGHT) {
            x -= w;
        }
        return x;
    }
    
    private int alignVertically(VAlign vAlign, int y, int h) {
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

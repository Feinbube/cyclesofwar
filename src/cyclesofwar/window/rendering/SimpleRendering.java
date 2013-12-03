package cyclesofwar.window.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Fleet;
import cyclesofwar.Planet;
import cyclesofwar.Player;
import cyclesofwar.tournament.TournamentBook;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class SimpleRendering extends Rendering {

    protected class Star {

        double x;
        double y;
        Color c;
        double d;

        Star(Random random) {
            super();

            x = random.nextDouble();
            y = random.nextDouble();
            c = Color.getHSBColor(0, 0, (float) random.nextDouble());
            d = random.nextDouble();
        }
    }

    protected final int StarCount = 1000;
    protected final List<Star> stars = new ArrayList<>();

    protected Color textColor = Color.YELLOW;

    public SimpleRendering() {
        stars.clear();
        for (int i = 0; i < StarCount; i++) {
            stars.add(new Star(random));
        }
    }

    @Override
    public Font getFont(int style, int fontSize) {
        return new Font("Courier New", style, (int) getScaled(fontSize));
    }

    @Override
    protected void applyNewSize() {
    }

    @Override
    public void drawBackground(Graphics g, long universeSeed) {
        g.setColor(Color.black);
        g.fillRect(0, 0, size.width, size.height);

        drawStars(g);
    }

    @Override
    public void drawSeed(Graphics g, long seed) {
        drawText(g, size.width - 5, 5, "seed: " + seed, textColor, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
    }

    @Override
    protected void drawUpdatedFps(Graphics g) {
        drawText(g, size.width - 10, 0, fps + "fps", textColor, null, HAlign.RIGHT, VAlign.BOTTOM, 12);
    }

    @Override
    public void drawControlInfo(Graphics g, String s) {
        drawControlInfo(g, s, 12);
    }

    @Override
    public void drawControlInfo(Graphics g, String s, int fontsize) {
        drawText(g, size.width - fontsize / 2, size.height - fontsize / 2, s, textColor, null, HAlign.RIGHT, VAlign.TOP, fontsize);
    }

    @Override
    public void drawInfo(Graphics g, String s) {
        drawText(g, size.width / 2, size.height / 2, s, textColor, null, HAlign.CENTER, VAlign.CENTER, getFont(Font.BOLD, 32));
    }

    @Override
    public void drawTitleScreen(Graphics g) {
        drawBackground(g, 0);
        drawText(g, size.width / 2, size.height / 2, gameTitle, textColor, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.BOLD, 48));
    }

    @Override
    protected void drawGameOverScreen(Graphics g, String winnerName) {
        drawText(g, size.width / 2, size.height / 3 + 20, "GAME OVER", textColor, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.BOLD, 48));

        drawText(g, size.width / 2, size.height / 3 + 100, winnerName + " has won!", textColor, null, HAlign.CENTER, VAlign.CENTER,
                getFont(Font.PLAIN, 32));
    }

    protected void drawStars(Graphics g) {
        for (Star star : stars) {
            g.setColor(star.c);

            int starSize = (int) (star.d * 4 + 1);
            int x = (int) (size.width * star.x);
            int y = (int) (size.height * star.y);
            g.fillOval(x - starSize / 2, y - starSize / 2, starSize, starSize);
        }
    }

    @Override
    public void drawPlanet(Graphics g, Planet planet, int id) {
        final int x = (int) getX(g, planet.getX());
        final int y = (int) getY(g, planet.getY());
        final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());

        final int uX = x - (planetSize >> 1);
        final int uY = y - (planetSize >> 1);

        g.setColor(planet.getPlayer().getPlayerBackColor());
        g.fillOval(uX, uY, planetSize, planetSize);

        drawText(g, x, y, ((int) planet.getForces()) + "", getPlayerTextColor(planet.getPlayer()), null, HAlign.CENTER, VAlign.CENTER, 10);
    }

    @Override
    public void drawSelectedPlanet(Graphics g, Planet planet) {
        if (planet == null) {
            return;
        }

        final int x = (int) getX(g, planet.getX());
        final int y = (int) getY(g, planet.getY());
        final int planetSize = planetSize(size.width, planet.getProductionRatePerSecond());

        final int uX = x - (planetSize >> 1);
        final int uY = y - (planetSize >> 1);

        g.setColor(Player.GoldenPlayer.getPlayerForeColor());
        ((Graphics2D) g).setStroke(new BasicStroke(4));
        int margin = (int) (0.1 * planetSize) + 2;
        g.drawRoundRect(uX - margin, uY - margin, planetSize + 2 * margin, planetSize + 2 * margin, 10, 10);
        ((Graphics2D) g).setStroke(new BasicStroke(1));
    }

    public void drawSelectedForce(Graphics g, double selectedForceRatio) {
        g.setColor(textColor.darker());

        int stepY = 40;
        int height = stepY * 4;
        int minY = size.height - height - borderSize - 20;

        int x = size.width - this.borderSize + borderSize / 4;
        drawMarker(g, x, minY + 0 * stepY, "0%", (int) (this.borderSize / 1.5), 3);
        drawMarker(g, x, minY + 1 * stepY, "25%", (int) (this.borderSize / 1.5), 1);
        drawMarker(g, x, minY + 2 * stepY, "50%", (int) (this.borderSize / 1.5), 5);
        drawMarker(g, x, minY + 3 * stepY, "75%", (int) (this.borderSize / 1.5), 1);
        drawMarker(g, x, minY + 4 * stepY, "100%", (int) (this.borderSize / 1.5), 3);

        g.setColor(Color.WHITE);
        int y = (int)(selectedForceRatio * height) + minY;
        g.fillRect(x, y, (int) (this.borderSize / 1.5), 3);
    }

    protected void drawMarker(Graphics g, int x, int y, String s, int width, int height) {
        Font f = getFont(Font.PLAIN, 12);

        drawText(g, x, y, s, this.textColor, null, HAlign.RIGHT, VAlign.CENTER, f);
        g.setColor(textColor);
        g.fillRect(x, y - height / 2, width, height);
    }

    @Override
    public double getUpdatedSelectedForce(int x, int y) {
        int stepY = 40;
        int height = stepY * 4;
        int minY = size.height - height - borderSize - 20;
        int maxY = height + minY;
        int minX = size.width - this.borderSize;

        if (y < minY || y > maxY || x < minX) {
            return -1;
        }

        return (y - minY) / (double) (maxY - minY);
    }

    @Override
    public void drawFleets(Graphics g, List<Fleet> fleets, double time) {
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
                    g.setColor(getPlayerTextColor(fleet.getPlayer()));
                    g.fillOval((int) x - 1, (int) y - 1, 5, 5);
                }
            } else if (fleet.getFormation() == Fleet.Formation.SPIRAL) {
                drawSpiralFormation(g, fleet, x, y, d, localTime);
            }

            if (d == MaxRenderedFleet) {
                drawText(g, (int) x, (int) y, fleet.getForce() + "", getPlayerTextColor(fleet.getPlayer()), null, HAlign.CENTER,
                        VAlign.CENTER, 10);
            }
        }
    }

    protected void drawArrowFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
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

    protected void drawSwarmFormation(Graphics g, Fleet fleet, double x, double y, int d, boolean filled, double time) {
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

    protected void drawSpiralFormation(Graphics g, Fleet fleet, double x, double y, int d, double time) {
        for (int i = 0; i < d; i++) {
            double rnd = random.nextDouble();
            double r = rnd * Math.sqrt(d) * 7;
            double v = rnd * -10;

            double localX = r * Math.cos(v);
            double localY = -r * Math.sin(v);

            g.fillOval((int) (x + localX / 2), (int) (y + localY / 2), 2, 2);
        }
    }

    @Override
    public void drawPlayers(Graphics g, List<Player> players) {
        int h = g.getFontMetrics().getHeight();

        players = Player.sortedBy(Player.PlanetCountComparator, players);
        int row = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.isAlive()) {
                continue;
            }

            drawText(g, 5, row * (h + 4) + 5, shortInfo(player), getPlayerTextColor(player), player.getPlayerBackColor(), HAlign.LEFT,
                    VAlign.CENTER, 12);
            row++;
        }
    }

    @Override
    public void drawMainMenu(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers,
            List<String> possibleRenderings, String selectedRendering,
            List<Integer> possibleNumbersOfRounds, Integer selectedNumberOfRounds, List<Integer> possiblePlanetsPerPlayer,
            Integer selectedNumberOfPlanetsPerPlayer, List<Double> possibleUniverseSizes, double selectedUniverseSize) {
        drawBackground(g, 0);

        int marginLeft = 60;
        int marginTop = 40;

        tags.clear();

        Font f = getFont(Font.BOLD, 18);
        drawText(g, marginLeft, marginTop - 12, "chose champions:", textColor, null, f);

        drawPlayers(g, selectedPlayers, allPlayers, marginLeft, marginTop);

        int left = 0;
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 230, "chose rendering style:",
                        textColor, null, f));
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 190, "chose number of matches:",
                        textColor, null, f));
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 150,
                        "chose number of planets per player:", textColor, null, f));
        left = Math.max(
                left,
                drawText(g, marginLeft, size.height - marginTop - g.getFontMetrics(f).getHeight() - 110, "chose universe size factor:",
                        textColor, null, f));

        drawSelection(g, "rendering", possibleRenderings, selectedRendering, marginLeft, marginTop + 230, left);
        drawSelection(g, "rounds", possibleNumbersOfRounds, selectedNumberOfRounds, marginLeft, marginTop + 190, left);
        drawSelection(g, "planets", possiblePlanetsPerPlayer, selectedNumberOfPlanetsPerPlayer, marginLeft, marginTop + 150, left);
        drawSelection(g, "sizefactors", possibleUniverseSizes, selectedUniverseSize, marginLeft, marginTop + 110, left);

        drawText(g, marginLeft, size.height - marginTop - 100, "chose game mode:", textColor, null, f);

        int borderSize = (int) (size.width * 7.0 / 1000);

        int wSteps = (size.width - marginLeft * 2) / 6;
        drawBigButton(g, "Play Mode", "PLAY!", marginLeft + wSteps, size.height - marginTop, HAlign.CENTER, 22, borderSize, textColor);
        drawBigButton(g, "Live Mode", "LIVE", marginLeft + wSteps * 2, size.height - marginTop, HAlign.CENTER, 22, borderSize, textColor);
        drawBigButton(g, "Tournament Mode", "ARENA", marginLeft + wSteps * 3, size.height - marginTop, HAlign.CENTER, 22, borderSize, textColor);
        drawBigButton(g, "Arena Mode", "DUEL", marginLeft + wSteps * 4, size.height - marginTop, HAlign.CENTER, 22, borderSize, textColor);
        drawBigButton(g, "Demo Mode", "DEMO", marginLeft + wSteps * 5, size.height - marginTop, HAlign.CENTER, 22, borderSize, textColor);
    }

    protected <T> void drawSelection(Graphics g, String id, List<T> possibleValues, T selectedValue, int marginLeft, int marginTop, int left) {
        int borderSize = (int) (size.height * 7.0 / 1000);
        int w = size.width - marginLeft * 3 - left;
        int tile = (int) (w / (double) (possibleValues.size() - 1));
        for (int i = 0; i < possibleValues.size(); i++) {
            T numberAtI = possibleValues.get(i);
            if (numberAtI.equals(selectedValue)) {
                drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
                        borderSize, textColor);
            } else {
                drawButton(g, id + numberAtI, numberAtI + "", left + marginLeft + i * tile, size.height - marginTop, HAlign.CENTER, 14,
                        borderSize, Color.white);
            }
        }
    }

    protected void drawPlayers(Graphics g, List<Player> selectedPlayers, List<Player> allPlayers, int marginLeft, int marginTop) {
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
            drawText(g, posX, posY, player.getName(), getPlayerTextColor(player), player.getPlayerBackColor(), f);
            remember(posX, posY, w, h, player);
            if (selectedPlayers.contains(player)) {
                drawBorder(g, posX, posY, w, h);
            }
            posX += w + 12;
        }
    }

    protected void drawBigButton(Graphics g, String id, String caption, int x, int y, HAlign hAlign, int fontSize, int buttonBorderSize, Color c) {
        drawButton(g, id, caption, x, y, hAlign, fontSize, buttonBorderSize, c);
    }

    protected void drawButton(Graphics g, String id, String caption, int x, int y, HAlign hAlign, int fontSize, int buttonBorderSize, Color c) {
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

    protected void drawBorder(Graphics g, int posX, int posY, int w, int h) {
        g.setColor(Color.white);
        g.drawRect(posX - 3 - 1, posY - 3 + 7, w + 6, h + 6 - 6);
        g.setColor(Color.green);
        g.drawRect(posX - 2 - 1, posY - 2 + 7, w + 4, h + 4 - 6);
        g.setColor(Color.black);
        g.drawRect(posX - 1 - 1, posY - 1 + 7, w + 2, h + 2 - 6);
    }

    @Override
    public void drawStatistics(Graphics g, TournamentBook tournament, String s, boolean showDetails) {
        drawBackground(g, 0);

        Font f = getFont(Font.PLAIN, 14);

        int marginLeft = 50;
        int padding = 10;
        int marginTop = 120;

        drawText(g, marginLeft, marginLeft, s, textColor, null, HAlign.LEFT, VAlign.CENTER, getFont(Font.BOLD, 32));

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
        drawText(g, size.width - marginLeft, marginTop + (h + 2) * (tournament.getRankings().size() + 1), text, Color.white, null, HAlign.RIGHT, VAlign.BOTTOM, f);
    }

    protected void drawLines(Graphics g, TournamentBook tournament, Font f, int marginLeft, int marginTop) {
        int h = g.getFontMetrics(f).getHeight();
        for (int i = 0; i < tournament.getRankings().size(); i++) {
            Player player = tournament.getRankings().get(i).getPlayer();
            g.setColor(player.getPlayerBackColor());
            g.fillRect(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight());
            remember(marginLeft - 2, marginTop + (h + 2) * (i + 1) + 4, size.width - marginLeft * 2 + 3, g.getFontMetrics(f).getHeight(), player);
        }
    }
}

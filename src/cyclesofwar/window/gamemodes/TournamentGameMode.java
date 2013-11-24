package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import cyclesofwar.Arena;
import cyclesofwar.Universe;
import cyclesofwar.tournament.LastManStandingTournament;
import cyclesofwar.tournament.TournamentRecord;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class TournamentGameMode extends GameMode {

	private LastManStandingTournament tournament;

	public TournamentGameMode(GamePanel gamePanel) {
		super(gamePanel);

		tournament = new LastManStandingTournament(Arena.tournamentSeed, this.getThreadCount(), this.getSelectedPlayers(), this.getSelectedNumberOfRounds(),
				this.getSelectedNumberOfPlanetsPerPlayer(), this.getSelectedUniverseSizeFactor());
		tournament.start();
	}

	@Override
	protected void updateGame() {
	}

	@Override
	protected void paintGame(Graphics g) {
		getRendering().drawStatistics(g, tournament, "Last Man Standing Tournament", false);
	}

	@Override
	protected void drawControls(Graphics g) {
		if (!getShowControls()) {
			getRendering().drawControlInfo(g, "F1/1 toggle info");
		} else {
			String pauseString = tournament.isPaused() ? "continue" : "pause";
			getRendering().drawControlInfo(g, "[Key Mapping]: ESC Menue ... CLICK stats: see battle ... SPACE: " + pauseString
					+ " ... 5: new combat");
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.switchTo(GameModes.PLAYERSELECTION);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_5) {
			reset();
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			tournament.togglePause();
		}
	}

	@Override
	public void mouseMoved(int x, int y) {
	}

	@Override
	public void mousePressed(int x, int y) {
	}

	@Override
	public void mouseReleased(int x, int y) {
		List<TournamentRecord> winRecords = getRendering().getFightRecords(x, y);
		if (winRecords != null && winRecords.size() > 0) {
			TournamentRecord winRecord = winRecords.get(random.nextInt(winRecords.size()));
			this.setLiveUniverse(new Universe(winRecord.getUniverseSeed(), winRecord.getPlayers(), this
					.getSelectedNumberOfPlanetsPerPlayer(), this.getSelectedUniverseSizeFactor()));

			this.switchTo(GameModes.LIVE);
		}
	}

	@Override
	public void resume() {
		if (tournament.isPaused()) {
			tournament.togglePause();
		}
	}

	@Override
	public void pause() {
		if (!tournament.isPaused()) {
			tournament.togglePause();
		}
	}

	@Override
	public void reset() {
		tournament.abort();
		tournament = new LastManStandingTournament(Arena.tournamentSeed, this.getThreadCount(), this.getSelectedPlayers(), this.getSelectedNumberOfRounds(),
				this.getSelectedNumberOfPlanetsPerPlayer(), this.getSelectedUniverseSizeFactor());
		tournament.start();
	}
}
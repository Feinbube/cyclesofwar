package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import cyclesofwar.Universe;
import cyclesofwar.tournament.LastManStandingTournament;
import cyclesofwar.tournament.TournamentRecord;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class TournamentGameMode extends GameMode {

	private LastManStandingTournament tournament;

	public TournamentGameMode(GamePanel gamePanel) {
		super(gamePanel);

		tournament = new LastManStandingTournament(this.getThreadCount(), this.getSelectedPlayers(), this.getSelectedNumberOfRounds(),
				this.getSelectedNumberOfPlanetsPerPlayer(), this.getSelectedUniverseSizeFactor());
		tournament.start();
	}

	@Override
	protected void updateGame() {
	}

	@Override
	protected void paintGame(Graphics g) {
		rendering.drawStatistics(g, tournament, "Last Man Standing Tournament");
	}

	@Override
	protected void drawControls(Graphics g) {
		if (!getShowControls()) {
			rendering.drawControlInfo(g, "F1/1 toggle info");
		} else {
			String pauseString = tournament.isPaused() ? "continue" : "pause";
			rendering.drawControlInfo(g, "[Key Mapping]: ESC Menue ... CLICK stats: see battle ... SPACE: " + pauseString
					+ " ... 5: new combat");
		}
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {
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
	protected void mouseMovedGame(int x, int y) {
	}

	@Override
	protected void mousePressedGame(int x, int y) {
	}

	@Override
	protected void mouseReleasedGame(int x, int y) {
		List<TournamentRecord> winRecords = rendering.getFightRecords(x, y);
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
		tournament = new LastManStandingTournament(this.getThreadCount(), this.getSelectedPlayers(), this.getSelectedNumberOfRounds(),
				this.getSelectedNumberOfPlanetsPerPlayer(), this.getSelectedUniverseSizeFactor());
		tournament.start();
	}
}
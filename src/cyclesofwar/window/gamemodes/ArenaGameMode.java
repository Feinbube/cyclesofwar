package cyclesofwar.window.gamemodes;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;
import cyclesofwar.tournament.OneOnOneTournament;
import cyclesofwar.tournament.TournamentRecord;
import cyclesofwar.window.GameModes;
import cyclesofwar.window.GamePanel;

public class ArenaGameMode extends GameMode {

	private OneOnOneTournament tournament;
	private int threads;
	private int matches;

	public ArenaGameMode(GamePanel gamePanel, int threads, int matches) {
		super(gamePanel);

		this.threads = threads;
		this.matches = matches;

		tournament = new OneOnOneTournament(threads, this.getSelectedPlayers(), matches);
		tournament.start();
	}

	@Override
	protected void updateGame() {
	}

	@Override
	protected void paintGame(Graphics g) {
		rendering.drawStatistics(g, tournament, "1-On-1 Tournament");
	}

	@Override
	protected void drawControls(Graphics g) {
		if (!getShowControls()) {
			rendering.drawControlInfo(g, "F1 toggle info");
		} else {
			String pauseString = tournament.isPaused() ? "continue" : "pause";
			rendering.drawControlInfo(g, "[Key Mapping]: ESC Menue ... CLICK player: toogle priority ... CLICK stats: see battle ... SPACE: " + pauseString
					+ " ... F5: new combat");
		}
	}

	@Override
	protected void keyPressedGame(KeyEvent arg0) {		
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.switchTo(GameModes.PLAYERSELECTION);
		}

		if (arg0.getKeyCode() == KeyEvent.VK_F5) {
			reset();
		}

		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			tournament.togglePause();
		}
	}

	@Override
	protected void mousePressedGame(int x, int y) {
	}

	@Override
	protected void mouseMovedGame(int x, int y) {
	}
	
	@Override
	protected void mouseReleasedGame(int x, int y) {
		List<TournamentRecord> winRecords = rendering.getFightRecords(x, y);
		if (winRecords != null && winRecords.size() > 0) {
			TournamentRecord winRecord = winRecords.get(random.nextInt(winRecords.size()));
			setLiveUniverse(new Universe(winRecord.getUniverseSeed(), winRecord.getPlayers()));

			this.switchTo(GameModes.LIVE);
		} else {
			Player player = rendering.getPlayer(x, y, tournament);
			if (player != null) {
				tournament.switchPriority(player);
			}
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
		tournament = new OneOnOneTournament(threads, this.getSelectedPlayers(), matches);
		tournament.start();
	}
}
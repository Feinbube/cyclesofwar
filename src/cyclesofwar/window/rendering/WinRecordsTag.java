package cyclesofwar.window.rendering;

import java.util.List;

import cyclesofwar.tournament.TournamentRecord;

class WinRecordsTag {

	int x;
	int y;
	int w;
	int h;
	List<TournamentRecord> winRecords;

	WinRecordsTag(int x, int y, int w, int h, List<TournamentRecord> winRecords) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.winRecords = winRecords;
	}

	public boolean intersects(int x, int y) {
		return x > this.x && x < this.x + this.w && y > this.y && y < this.y + this.h;
	}
}
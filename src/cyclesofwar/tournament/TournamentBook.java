package cyclesofwar.tournament;

import java.util.ArrayList;
import java.util.List;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public class TournamentBook {

    List<Player> champions = new ArrayList<>();
    List<Universe> gamesPlayed = new ArrayList<>();

    List<TournamentRecord> records = new ArrayList<>();

    int planetsPerPlayer;
    double universeSizeFactor;
    int maxRecords;

    public TournamentBook(List<Player> champions, int planetsPerPlayer, double universeSizeFactor, int maxRecords) {
        this.champions = champions;

        this.planetsPerPlayer = planetsPerPlayer;
        this.universeSizeFactor = universeSizeFactor;
        this.maxRecords = maxRecords;
    }

    public TournamentBook(List<Player> champions, int planetsPerPlayer, double universeSizeFactor) {
        this(champions, planetsPerPlayer, universeSizeFactor, Integer.MAX_VALUE);
    }

    public TournamentBook lightWeightClone() {
        return this;
    }

	// getters
    public List<Player> getChampions() {
        return champions;
    }

    public int getGamesToPlayCount() {
        return 0;
    }

    public int getGamesPlayedCount() {
        return records.size();
    }

    public boolean hasPriority(Player player) {
        return false;
    }

    public void addFinishedGame(Universe universe) {
        synchronized (gamesPlayed) {
            if (maxRecords <= gamesPlayed.size()) {
                gamesPlayed.remove(gamesPlayed.get(0));
            }
            gamesPlayed.add(universe);

            synchronized (records) {
                if (maxRecords <= records.size()) {
                    records.remove(records.get(0));
                }
                records.add(new TournamentRecord(universe));
            }
        }
    }

	// Rankings
    public List<Player> rankedPlayers() {
        List<Player> result = new ArrayList<>();

        for (Ranking ranking : getRankings()) {
            result.add(ranking.player);
        }

        return result;
    }

    // TODO caching this function may be a very good idea!
    public List<Ranking> getRankings() {
        List<Ranking> result = new ArrayList<>();
        for (Player player : champions) {
            result.add(new Ranking(player, wonBy(player).size(), participatedIn(player).size()));
        }

        Ranking.sort(result);

        result.add(new Ranking(Player.NonePlayer, wonBy(Player.NonePlayer).size(), participatedIn(Player.NonePlayer).size()));
        return result;
    }

    public List<TournamentRecord> winsOver(Player player, Player competitor) {
        return TournamentRecord.participatedIn(wonBy(player), competitor);
    }

    public List<TournamentRecord> wonBy(Player player) {
        return TournamentRecord.wonBy(records, player);
    }

    public List<TournamentRecord> fightsAgainst(Player player, Player competitor) {
        return TournamentRecord.participatedIn(participatedIn(player), competitor);
    }

    public List<TournamentRecord> participatedIn(Player player) {
        return TournamentRecord.participatedIn(records, player);
    }
}

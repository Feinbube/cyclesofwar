package cyclesofwar.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cyclesofwar.Player;
import cyclesofwar.Universe;

public abstract class Tournament extends TournamentBook {

    Random random;

    List<Player> prioritized = new ArrayList<>();

    List<Universe> gamesToPlay = new ArrayList<>();

    int gamesToPlayCount = 0;
    int gamesPlayedCount = 0;

    List<WorkerThread> workerThreads = new ArrayList<>();
    boolean pause = true;

    boolean wasAborted = false;
    Player responsibleForAbort;

    final int planetsPerPlayer;
    final double universeSizeFactor;

    public Tournament(long randomSeed, int threads, List<Player> champions, int matches, int planetsPerPlayer, double universeSizeFactor) {
        super(champions);

        this.planetsPerPlayer = planetsPerPlayer;
        this.universeSizeFactor = universeSizeFactor;

        random = new Random(randomSeed);

        setupGames(matches);
        gamesToPlayCount = gamesToPlay.size();

        setupThreads(threads);
    }

    protected abstract void setupGames(int matches);

    // Lightweight Clone for Rendering
    protected Tournament(Tournament other) {
        super(null);

        this.champions = new ArrayList<>();
        for (Player player : other.champions) {
            this.champions.add(player);
        }

        synchronized (other.records) {
            for (TournamentRecord record : other.records) {
                this.records.add(record);
            }
        }

        for (Player player : other.prioritized) {
            this.prioritized.add(player);
        }

        this.gamesPlayedCount = other.gamesPlayedCount;
        this.gamesToPlayCount = other.gamesToPlayCount;

        this.planetsPerPlayer = other.planetsPerPlayer;
        this.universeSizeFactor = other.universeSizeFactor;
    }

    public abstract TournamentBook lightWeightClone();

    public void runToCompletion() {
        if (isPaused()) {
            togglePause();
        }

        start();

        for (WorkerThread thread : workerThreads) {
            thread.join();
        }
    }

	// getters
    @Override
    public int getGamesToPlayCount() {
        return gamesToPlayCount;
    }

    @Override
    public int getGamesPlayedCount() {
        return gamesPlayedCount;
    }

	// Priority
    public void switchPriority(Player player) {
        if (prioritized.contains(player)) {
            prioritized.remove(player);
        } else {
            prioritized.add(player);
        }
    }

    @Override
    public boolean hasPriority(Player player) {
        return prioritized.contains(player);
    }

	// workerthreads
    private void setupThreads(int workerThreadCount) {
        for (int t = 0; t < workerThreadCount; t++) {
            WorkerThread workerThread = new WorkerThread(this);
            workerThread.pause = pause;
            workerThreads.add(workerThread);
        }
    }

    public void start() {
        for (WorkerThread workerThread : workerThreads) {
            workerThread.start();
        }
    }

    public void abort() {
        for (WorkerThread workerThread : workerThreads) {
            workerThread.running = false;
        }

        wasAborted = true;
    }

    public boolean isPaused() {
        return pause;
    }

    public void togglePause() {
        pause = !pause;

        for (WorkerThread workerThread : workerThreads) {
            workerThread.pause = pause;

            if (!pause) {
                synchronized (workerThread) {
                    workerThread.notifyAll();
                }
            }
        }
    }

    public Universe getUniverse() {
        Universe result;
        synchronized (gamesToPlay) {
            if (gamesToPlay.isEmpty()) {
                return null;
            }

            result = getPrioritizedGame();
            if (result == null) {
                result = gamesToPlay.get(random.nextInt(gamesToPlay.size()));
            }

            gamesToPlay.remove(result);
        }
        return result;
    }

    private Universe getPrioritizedGame() {
        List<Universe> prioritizedGames = new ArrayList<>();
        for (Universe universe : gamesToPlay) {
            for (Player player : prioritized) {
                if (universe.inhabitedByPlayer(player)) {
                    prioritizedGames.add(universe);
                    break;
                }
            }
        }

        if (prioritizedGames.size() > 0) {
            return prioritizedGames.get(random.nextInt(prioritizedGames.size()));
        } else {
            return null;
        }
    }

    @Override
    public void addFinishedGame(Universe universe) {
        super.addFinishedGame(universe);
        synchronized (gamesPlayed) {
            gamesPlayedCount++;
            gamesToPlayCount--;
        }
    }

    public void abort(Player responsilbe) {
        this.abort();
        responsibleForAbort = responsilbe;
    }

    public boolean wasAborted() {
        return responsibleForAbort != null;
    }

    public Player getPlayerResponsibleForAbort() {
        return responsibleForAbort;
    }
}

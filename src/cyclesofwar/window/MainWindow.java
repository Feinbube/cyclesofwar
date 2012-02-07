package cyclesofwar.window;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import cyclesofwar.Arena;
import cyclesofwar.Player;
import cyclesofwar.tournament.LastManStandingTournament;
import cyclesofwar.tournament.Tournament;

public class MainWindow {
	public static void main(String[] args) {
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));

		int threadcount = Runtime.getRuntime().availableProcessors();

		if (parameters.contains("--noGUI")) {
			startTournament(threadcount);
		} else {
			startGUI(threadcount);
		}
	}

	private static void startTournament(int threadcount) {
		List<Player> champions = Arena.champions();

		int place = 0;
		Tournament tournament = null;
		while (champions.size() > 1) {
			tournament = new LastManStandingTournament(threadcount, champions, Arena.matchesInTournament);
			tournament.runToCompletion();
			Player winner = tournament.rankedPlayers().get(0);
			printWinner(++place, tournament, winner);
			champions.remove(winner);
		}
		printWinner(++place, tournament, champions.get(0));
	}

	private static void printWinner(int place, Tournament tournament, Player winner) {
		System.out.println(place + "," + winner.getName() + "," + tournament.wonBy(winner).size() + ","
				+ tournament.participatedIn(winner).size());
	}

	private static void startGUI(int threads) {
		JFrame f = new JFrame("Cycles of War");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// TODO: remember last session
		Container panel = new GamePanel(threads, Arena.champions(), Arena.registeredPlayers(), Arena.matchesInTournament);
		f.getContentPane().add(panel, BorderLayout.CENTER);

		f.addKeyListener((KeyListener) panel);
		f.setFocusTraversalKeysEnabled(false);
		panel.setFocusTraversalKeysEnabled(false);

		f.addMouseListener((MouseListener) panel);

		// TODO: remember last session
		f.setSize(800, 480); // f.pack();
		f.setVisible(true);
	}
}
package cyclesofwar.window;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;

import javax.swing.JFrame;

import cyclesofwar.Arena;

public class MainWindow {
	public static void main(String[] args) {
		JFrame f = new JFrame("Cycles of War");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// TODO: remember last session
		Container panel = new GamePanel(Runtime.getRuntime().availableProcessors(), Arena.champions(), Arena.registeredPlayers(), Arena.matchesInTournament);
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
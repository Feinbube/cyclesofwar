package cyclesofwar.window;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.JFrame;

import cyclesofwar.Arena;

public class MainWindow {

	public static void main(String[] args) {
		JFrame f = new JFrame("Cycles of War");

		ConfigManager configManager = new ConfigManager();
		f.addWindowListener(configManager);

		GamePanel panel = new GamePanel(Runtime.getRuntime().availableProcessors(), configManager.getSelectedPlayers(),
				Arena.registeredPlayers(), configManager.getNumberOfRounds());
		configManager.setGamePanel(panel);

		f.getContentPane().add(panel, BorderLayout.CENTER);

		f.addKeyListener((KeyListener) panel);
		f.setFocusTraversalKeysEnabled(false);
		panel.setFocusTraversalKeysEnabled(false);

		f.addMouseListener((MouseListener) panel);

		f.setBounds(configManager.getX(), configManager.getY(), configManager.getWidth(), configManager.getHeight());
		f.setVisible(true);
	}
}
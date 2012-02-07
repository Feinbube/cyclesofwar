package cyclesofwar.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cyclesofwar.Arena;
import cyclesofwar.Player;

public class ConfigManager extends WindowAdapter {
	GamePanel panel;
	Properties properties;

	public ConfigManager() {
		File f = new File("cow.config");
		if(f.exists()) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream("cow.config"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				properties = null;
			} catch (IOException e) {
				e.printStackTrace();
				properties = null;
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		saveProperties(e);
		System.exit(0);
	}

	public void setGamePanel(GamePanel panel) {
		this.panel = panel;
	}

	public int getX() {
		if (properties == null) {
			return 0;
		} else {
			return new Integer(properties.getProperty("x"));
		}
	}

	public int getY() {
		if (properties == null) {
			return 0;
		} else {
			return new Integer(properties.getProperty("y"));
		}
	}

	public int getWidth() {
		if (properties == null) {
			return 800;
		} else {
			return new Integer(properties.getProperty("width"));
		}
	}

	public int getHeight() {
		if (properties == null) {
			return 480;
		} else {
			return new Integer(properties.getProperty("height"));
		}
	}

	public List<Player> getSelectedPlayers() {
		return getPlayers(Arena.champions(), "Champion");
	}

	public List<Player> getPlayers(List<Player> defaultResult, String s) {
		List<Player> result = new ArrayList<Player>();
		if (properties == null) {
			return defaultResult;
		} else {
			int i = 0;
			while (true) {
				i++;
				String id = s + i;
				if (properties.containsKey(id)) {
					String playerName = properties.getProperty(id);
					result.add(getPlayerByName(playerName));
				} else {
					break;
				}
			}

			return result;
		}
	}

	private Player getPlayerByName(String playerName) {
		for(Player player : Arena.registeredPlayers()) {
			if(player.getName().equals(playerName)) {
				return player;
			}
		}
		return null;
	}

	protected void saveProperties(WindowEvent e) {	
		Properties properties = new Properties();

		properties.setProperty("x", "" + e.getWindow().getX());
		properties.setProperty("y", "" + e.getWindow().getY());
		properties.setProperty("width", "" + e.getWindow().getSize().width);
		properties.setProperty("height", "" + e.getWindow().getSize().height);
		savePlayers(properties, panel.getSelectedPlayers(), "Champion");

		try {
			properties.store(new FileOutputStream("cow.config"), "cow last session config");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void savePlayers(Properties properties, List<Player> players, String s) {
		for (int i = 0; i < players.size(); i++) {
			properties.setProperty(s + (i + 1), players.get(i).getName());
		}

	}
}
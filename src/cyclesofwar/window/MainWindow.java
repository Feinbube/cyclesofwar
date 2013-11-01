package cyclesofwar.window;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import cyclesofwar.Arena;
import java.awt.Window;
import java.lang.reflect.Method;

public class MainWindow {

    public static void main(String[] args) {
        JFrame f = new JFrame("Cycles of War");

        ConfigManager configManager = new ConfigManager();
        f.addWindowListener(configManager);

        GamePanel panel = new GamePanel(Runtime.getRuntime().availableProcessors() - 1, configManager.getSelectedPlayers(),
                Arena.registeredPlayers(), configManager.getNumberOfRounds(), configManager.getNumberOfPlanetsPerPlayer(),
                configManager.getUniverseSizeFactor());
        configManager.setGamePanel(panel);

        f.getContentPane().add(panel, BorderLayout.CENTER);

        f.addKeyListener((KeyListener) panel);
        f.setFocusTraversalKeysEnabled(false);
        panel.setFocusTraversalKeysEnabled(false);

        f.addMouseListener((MouseListener) panel);

        f.setBounds(configManager.getX(), configManager.getY(), configManager.getWidth(), configManager.getHeight());

        if (isMacOSX()) {
            enableFullScreenMode(f);
        }

        f.setVisible(true);
    }

    private static boolean isMacOSX() {
        return System.getProperty("os.name").indexOf("Mac OS") >= 0;
    }

    public static void enableFullScreenMode(Window window) {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[]{
                Window.class, boolean.class});
            method.invoke(null, window, true);
        } catch (Throwable t) {
            System.err.println("Full screen mode is not supported");
            t.printStackTrace();
        }
    }
}

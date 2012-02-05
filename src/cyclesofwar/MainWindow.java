package cyclesofwar;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MainWindow {
    public static void main(String[] args) {
    	 JFrame f = new JFrame("Cycles of War");
         f.addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e) {
                 System.exit(0);
             }
         });
         Container panel = new GamePanel();
         f.getContentPane().add(panel, BorderLayout.CENTER);
         
         f.addKeyListener((KeyListener)panel);
         f.setFocusTraversalKeysEnabled(false);
         panel.setFocusTraversalKeysEnabled(false);
         
         f.addMouseListener((MouseListener)panel);
                  
         f.setSize(800, 480); //f.pack();
         f.setVisible(true);
    }
}
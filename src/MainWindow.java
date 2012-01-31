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
         
         f.setSize(800, 480); //f.pack();
         f.setVisible(true);
    }
}
package com.smu8.game;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class E20ShuttingGame extends JFrame {
   public E20ShuttingGame() {
      this.setTitle("Infinite Space Shooter");
      this.setSize(800, 600);
      this.setDefaultCloseOperation(3);
      this.setLocationRelativeTo((Component)null);
      this.setContentPane(new GamePanel());
      this.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(E20ShuttingGame::new);
   }
}

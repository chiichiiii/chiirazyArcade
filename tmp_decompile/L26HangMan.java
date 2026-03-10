package com.smu8.game;

import javax.swing.SwingUtilities;

public class L26HangMan {
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         WordManager wordManager = new WordManager();
         UI ui = new UI();
         Game game = new Game(wordManager, ui, 6);
         ui.bindGame(game);
         game.startNewRound();
      });
   }
}

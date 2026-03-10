package com.smu8.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class UI extends JFrame {
   private final JLabel wordLabel = new JLabel("", 0);
   private final JLabel usedLabel = new JLabel("Used: []");
   private final JLabel lifeLabel = new JLabel("Life: 6 / 6");
   private final JLabel frameLabel = new JLabel("Frame: [------]");
   private final JLabel messageLabel = new JLabel(" ");
   private final JTextField inputField = new JTextField();
   private final JButton guessButton = new JButton("Guess");
   private final JButton newGameButton = new JButton("New Game");
   private Game game;

   UI() {
      super("L26 Hangman");
      this.setDefaultCloseOperation(3);
      this.wordLabel.setFont(new Font("SansSerif", 1, 34));
      this.usedLabel.setFont(new Font("SansSerif", 0, 16));
      this.lifeLabel.setFont(new Font("SansSerif", 0, 16));
      this.frameLabel.setFont(new Font("Monospaced", 0, 16));
      this.messageLabel.setFont(new Font("SansSerif", 1, 14));
      this.inputField.setFont(new Font("SansSerif", 1, 18));
      this.inputField.setHorizontalAlignment(0);
      JPanel center = new JPanel();
      center.setLayout(new BoxLayout(center, 1));
      center.add(Box.createVerticalStrut(20));
      center.add(this.wordLabel);
      center.add(Box.createVerticalStrut(20));
      center.add(this.usedLabel);
      center.add(Box.createVerticalStrut(8));
      center.add(this.lifeLabel);
      center.add(Box.createVerticalStrut(8));
      center.add(this.frameLabel);
      center.add(Box.createVerticalStrut(16));
      center.add(this.messageLabel);
      JPanel bottom = new JPanel(new FlowLayout(1, 8, 8));
      this.inputField.setPreferredSize(new Dimension(80, 34));
      bottom.add(new JLabel("Letter:"));
      bottom.add(this.inputField);
      bottom.add(this.guessButton);
      bottom.add(this.newGameButton);
      this.setLayout(new BorderLayout());
      this.add(center, "Center");
      this.add(bottom, "South");
      this.guessButton.addActionListener((e) -> this.onGuess());
      this.inputField.addActionListener((e) -> this.onGuess());
      this.newGameButton.addActionListener((e) -> {
         if (this.game != null) {
            this.game.startNewRound();
         }

      });
      this.setSize(520, 360);
      this.setLocation(640, 220);
      this.setVisible(true);
   }

   void bindGame(Game game) {
      this.game = game;
   }

   void onGuess() {
      if (this.game != null) {
         String input = this.inputField.getText().trim().toLowerCase();
         this.inputField.setText("");
         if (input.length() == 1 && input.charAt(0) >= 'a' && input.charAt(0) <= 'z') {
            this.game.submitGuess(input.charAt(0));
         } else {
            this.showMessage("Enter one alphabet letter (a-z).");
         }
      }
   }

   void render(char[] masked, Set guessed, int wrongCount, int maxWrongCount, boolean win, boolean lose, String answer) {
      this.wordLabel.setText(this.formatMasked(masked));
      this.usedLabel.setText("Used: " + String.valueOf(guessed));
      this.lifeLabel.setText("Life: " + (maxWrongCount - wrongCount) + " / " + maxWrongCount);
      JLabel var10000 = this.frameLabel;
      String var10001 = this.hangmanFrame(wrongCount, maxWrongCount);
      var10000.setText("Frame: " + var10001);
      if (win) {
         this.messageLabel.setText("You win! Answer: " + answer);
      } else if (lose) {
         this.messageLabel.setText("You lose. Answer: " + answer);
      }

   }

   void showMessage(String message) {
      this.messageLabel.setText(message);
   }

   private String formatMasked(char[] masked) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < masked.length; ++i) {
         sb.append(masked[i]);
         if (i < masked.length - 1) {
            sb.append(' ');
         }
      }

      return sb.toString();
   }

   private String hangmanFrame(int wrongCount, int maxWrongCount) {
      int safeWrong = Math.min(wrongCount, maxWrongCount);
      String var10000 = "#".repeat(safeWrong);
      return "[" + var10000 + "-".repeat(Math.max(0, maxWrongCount - safeWrong)) + "]";
   }
}

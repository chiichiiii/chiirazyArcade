package com.smu8.game;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

class Game {
   private final WordManager wordManager;
   private final UI ui;
   private final int maxWrongCount;
   private String answer;
   private char[] masked;
   private final Set guessed = new LinkedHashSet();
   private int wrongCount;
   private boolean finished;

   Game(WordManager wordManager, UI ui, int maxWrongCount) {
      this.wordManager = wordManager;
      this.ui = ui;
      this.maxWrongCount = maxWrongCount;
   }

   public void startNewRound() {
      this.answer = this.wordManager.getRandomWord();
      this.masked = new char[this.answer.length()];
      Arrays.fill(this.masked, '_');
      this.guessed.clear();
      this.wrongCount = 0;
      this.finished = false;
      this.ui.showMessage("New game started.");
      this.ui.render(this.masked, this.guessed, this.wrongCount, this.maxWrongCount, false, false, (String)null);
   }

   public void submitGuess(char guess) {
      if (this.finished) {
         this.ui.showMessage("Game finished. Click New Game.");
      } else if (this.guessed.contains(guess)) {
         this.ui.showMessage("Already used: " + guess);
      } else {
         this.guessed.add(guess);
         int revealed = this.reveal(guess);
         if (revealed == 0) {
            ++this.wrongCount;
            this.ui.showMessage("Wrong: " + guess);
         } else {
            this.ui.showMessage("Correct: " + guess);
         }

         boolean win = this.isWin();
         boolean lose = this.isLose();
         this.finished = win || lose;
         String answerText = this.finished ? this.answer : null;
         this.ui.render(this.masked, this.guessed, this.wrongCount, this.maxWrongCount, win, lose, answerText);
      }
   }

   private int reveal(char guess) {
      int revealed = 0;

      for(int i = 0; i < this.answer.length(); ++i) {
         if (this.answer.charAt(i) == guess && this.masked[i] == '_') {
            this.masked[i] = guess;
            ++revealed;
         }
      }

      return revealed;
   }

   private boolean isWin() {
      for(char c : this.masked) {
         if (c == '_') {
            return false;
         }
      }

      return true;
   }

   private boolean isLose() {
      return this.wrongCount >= this.maxWrongCount;
   }
}

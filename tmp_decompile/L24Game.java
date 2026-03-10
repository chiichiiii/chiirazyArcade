package com.smu8.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

public class L24Game extends JFrame {
   private final JTextField[][] cells = new JTextField[9][9];
   private static final int[][] BASE_PUZZLE = new int[][]{{5, 3, 0, 0, 7, 0, 0, 0, 0}, {6, 0, 0, 1, 9, 5, 0, 0, 0}, {0, 9, 8, 0, 0, 0, 0, 6, 0}, {8, 0, 0, 0, 6, 0, 0, 0, 3}, {4, 0, 0, 8, 0, 3, 0, 0, 1}, {7, 0, 0, 0, 2, 0, 0, 0, 6}, {0, 6, 0, 0, 0, 0, 2, 8, 0}, {0, 0, 0, 4, 1, 9, 0, 0, 5}, {0, 0, 0, 0, 8, 0, 0, 7, 9}};
   private static final int[][] BASE_SOLUTION = new int[][]{{5, 3, 4, 6, 7, 8, 9, 1, 2}, {6, 7, 2, 1, 9, 5, 3, 4, 8}, {1, 9, 8, 3, 4, 2, 5, 6, 7}, {8, 5, 9, 7, 6, 1, 4, 2, 3}, {4, 2, 6, 8, 5, 3, 7, 9, 1}, {7, 1, 3, 9, 2, 4, 8, 5, 6}, {9, 6, 1, 5, 3, 7, 2, 8, 4}, {2, 8, 7, 4, 1, 9, 6, 3, 5}, {3, 4, 5, 2, 8, 6, 1, 7, 9}};
   private int[][] puzzle;
   private int[][] solution;
   private final JLabel statusLabel = new JLabel("Enter numbers and click a button.");
   private final Random random = new Random();

   public L24Game() {
      super("L24 Sudoku MVP");
      this.setDefaultCloseOperation(3);
      this.startNewGame();
      JPanel root = new JPanel(new BorderLayout(12, 12));
      root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
      JPanel board = this.createBoardPanel();
      JPanel controls = this.createControlPanel();
      this.statusLabel.setFont(new Font("SansSerif", 0, 14));
      root.add(board, "Center");
      root.add(controls, "South");
      root.add(this.statusLabel, "North");
      this.setContentPane(root);
      this.applyPuzzleToCells();
      this.pack();
      this.setLocation(520, 120);
      this.setResizable(false);
      this.setVisible(true);
   }

   private void startNewGame() {
      int[][][] boards = this.createRandomizedBoards();
      this.puzzle = boards[0];
      this.solution = boards[1];
   }

   private int[][][] createRandomizedBoards() {
      int[] map = this.createNumberMap();
      int[] rowOrder = this.createBandOrder();
      int[] colOrder = this.createBandOrder();
      int[][] randomizedPuzzle = this.transformBoard(BASE_PUZZLE, map, rowOrder, colOrder);
      int[][] randomizedSolution = this.transformBoard(BASE_SOLUTION, map, rowOrder, colOrder);
      return new int[][][]{randomizedPuzzle, randomizedSolution};
   }

   private int[] createNumberMap() {
      List<Integer> shuffled = new ArrayList();

      for(int n = 1; n <= 9; ++n) {
         shuffled.add(n);
      }

      Collections.shuffle(shuffled, this.random);
      int[] map = new int[10];

      for(int n = 1; n <= 9; ++n) {
         map[n] = (Integer)shuffled.get(n - 1);
      }

      return map;
   }

   private int[] createBandOrder() {
      List<Integer> bands = new ArrayList();

      for(int b = 0; b < 3; ++b) {
         bands.add(b);
      }

      Collections.shuffle(bands, this.random);
      int[] order = new int[9];
      int idx = 0;

      for(int band : bands) {
         List<Integer> inner = new ArrayList();
         inner.add(0);
         inner.add(1);
         inner.add(2);
         Collections.shuffle(inner, this.random);

         for(int in : inner) {
            order[idx++] = band * 3 + in;
         }
      }

      return order;
   }

   private int[][] transformBoard(int[][] src, int[] numberMap, int[] rowOrder, int[] colOrder) {
      int[][] out = new int[9][9];

      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            int v = src[rowOrder[r]][colOrder[c]];
            out[r][c] = v == 0 ? 0 : numberMap[v];
         }
      }

      return out;
   }

   private JPanel createBoardPanel() {
      JPanel panel = new JPanel(new GridLayout(9, 9));
      panel.setPreferredSize(new Dimension(540, 540));

      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            JTextField tf = new JTextField();
            tf.setHorizontalAlignment(0);
            tf.setFont(new Font("SansSerif", 1, 24));
            tf.setPreferredSize(new Dimension(60, 60));
            int top = r % 3 == 0 ? 2 : 1;
            int left = c % 3 == 0 ? 2 : 1;
            int bottom = r == 8 ? 2 : 1;
            int right = c == 8 ? 2 : 1;
            tf.setBorder(new MatteBorder(top, left, bottom, right, Color.DARK_GRAY));
            this.cells[r][c] = tf;
            panel.add(tf);
         }
      }

      return panel;
   }

   private void applyPuzzleToCells() {
      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            JTextField tf = this.cells[r][c];
            tf.setText("");
            tf.setEditable(true);
            tf.setBackground(Color.WHITE);
            tf.setForeground(Color.BLACK);
            int value = this.puzzle[r][c];
            if (value != 0) {
               tf.setText(String.valueOf(value));
               tf.setEditable(false);
               tf.setBackground(new Color(235, 235, 235));
               tf.setForeground(new Color(40, 40, 40));
            }
         }
      }

   }

   private JPanel createControlPanel() {
      JPanel panel = new JPanel(new FlowLayout(1, 8, 0));
      JButton newGameButton = new JButton("New Game");
      JButton validateButton = new JButton("Validate");
      JButton checkAnswerButton = new JButton("Check Answer");
      JButton hintButton = new JButton("Hint");
      JButton resetButton = new JButton("Reset");
      newGameButton.addActionListener((e) -> {
         this.startNewGame();
         this.applyPuzzleToCells();
         this.statusLabel.setText("New game started.");
      });
      validateButton.addActionListener((e) -> this.validateCurrentBoard());
      checkAnswerButton.addActionListener((e) -> this.checkAnswer());
      hintButton.addActionListener((e) -> this.fillHintOneCell());
      resetButton.addActionListener((e) -> this.resetToPuzzle());
      panel.add(newGameButton);
      panel.add(validateButton);
      panel.add(checkAnswerButton);
      panel.add(hintButton);
      panel.add(resetButton);
      return panel;
   }

   private int readCellValue(int r, int c) {
      String text = this.cells[r][c].getText().trim();
      if (text.isEmpty()) {
         return 0;
      } else {
         return !text.matches("[1-9]") ? -1 : Integer.parseInt(text);
      }
   }

   private int[][] readBoard() {
      int[][] board = new int[9][9];

      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            int value = this.readCellValue(r, c);
            if (value == -1) {
               this.statusLabel.setText("Only digits 1-9 are allowed.");
               return null;
            }

            board[r][c] = value;
         }
      }

      return board;
   }

   private void validateCurrentBoard() {
      int[][] board = this.readBoard();
      if (board != null) {
         String error = this.findRuleError(board);
         if (error == null) {
            this.statusLabel.setText("Rule check OK.");
         } else {
            this.statusLabel.setText("Rule error: " + error);
         }

      }
   }

   private String findRuleError(int[][] board) {
      for(int r = 0; r < 9; ++r) {
         boolean[] seen = new boolean[10];

         for(int c = 0; c < 9; ++c) {
            int v = board[r][c];
            if (v != 0) {
               if (seen[v]) {
                  return "duplicate " + v + " in row " + (r + 1);
               }

               seen[v] = true;
            }
         }
      }

      for(int c = 0; c < 9; ++c) {
         boolean[] seen = new boolean[10];

         for(int r = 0; r < 9; ++r) {
            int v = board[r][c];
            if (v != 0) {
               if (seen[v]) {
                  return "duplicate " + v + " in col " + (c + 1);
               }

               seen[v] = true;
            }
         }
      }

      for(int boxR = 0; boxR < 3; ++boxR) {
         for(int boxC = 0; boxC < 3; ++boxC) {
            boolean[] seen = new boolean[10];

            for(int r = boxR * 3; r < boxR * 3 + 3; ++r) {
               for(int c = boxC * 3; c < boxC * 3 + 3; ++c) {
                  int v = board[r][c];
                  if (v != 0) {
                     if (seen[v]) {
                        return "duplicate " + v + " in box " + (boxR + 1) + "," + (boxC + 1);
                     }

                     seen[v] = true;
                  }
               }
            }
         }
      }

      return null;
   }

   private void checkAnswer() {
      int[][] board = this.readBoard();
      if (board != null) {
         String error = this.findRuleError(board);
         if (error != null) {
            this.statusLabel.setText("Fix rule errors first: " + error);
         } else {
            for(int r = 0; r < 9; ++r) {
               for(int c = 0; c < 9; ++c) {
                  if (board[r][c] != this.solution[r][c]) {
                     this.statusLabel.setText("Not solved yet.");
                     return;
                  }
               }
            }

            this.statusLabel.setText("Correct. Sudoku solved.");
         }
      }
   }

   private void fillHintOneCell() {
      List<Point> candidates = new ArrayList();

      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            if (this.puzzle[r][c] == 0) {
               int value = this.readCellValue(r, c);
               if (value == -1 || value != this.solution[r][c]) {
                  candidates.add(new Point(r, c));
               }
            }
         }
      }

      if (candidates.isEmpty()) {
         this.statusLabel.setText("No hint available.");
      } else {
         Point pick = (Point)candidates.get(this.random.nextInt(candidates.size()));
         this.cells[pick.x][pick.y].setText(String.valueOf(this.solution[pick.x][pick.y]));
         int var10001 = pick.x + 1;
         this.statusLabel.setText("Hint filled at row " + var10001 + ", col " + (pick.y + 1));
      }
   }

   private void resetToPuzzle() {
      for(int r = 0; r < 9; ++r) {
         for(int c = 0; c < 9; ++c) {
            if (this.puzzle[r][c] == 0) {
               this.cells[r][c].setText("");
            }
         }
      }

      this.statusLabel.setText("Board reset.");
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L24Game::new);
   }
}

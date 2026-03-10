package com.smu8.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class L23Game extends JFrame {
   public L23Game() {
      super("L23 PicoPop");
      this.setContentPane(new GamePanel());
      this.pack();
      this.setLocation(600, 180);
      this.setDefaultCloseOperation(3);
      this.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L23Game::new);
   }

   static class Cell {
      int row;
      int col;

      Cell(int row, int col) {
         this.row = row;
         this.col = col;
      }
   }

   static class Block {
      int row;
      int value;

      Block(int row, int value) {
         this.row = row;
         this.value = value;
      }
   }

   static class FallingTile {
      int value;
      int col;
      double startRow;
      double endRow;

      FallingTile(int value, int col, double startRow, double endRow) {
         this.value = value;
         this.col = col;
         this.startRow = startRow;
         this.endRow = endRow;
      }
   }

   class GamePanel extends JPanel {
      final int rows = 10;
      final int cols = 8;
      final int cellSize = 56;
      final int infoHeight = 70;
      final int[][] board = new int[10][8];
      final int fruitKinds = 5;
      final int mixItem = 100;
      final Color[] palette = new Color[]{new Color(236, 95, 95), new Color(79, 170, 255), new Color(255, 196, 79), new Color(110, 214, 138), new Color(193, 134, 255)};
      final Image[] fruitImages = new Image[5];
      Image mixItemImage;
      final Random random = new Random();
      int score = 0;
      int selectedRow = -1;
      int selectedCol = -1;
      boolean animating = false;
      double animationProgress = (double)0.0F;
      double animationStep = 0.1;
      Timer animationTimer;
      List fallingTiles = new ArrayList();

      GamePanel() {
         this.setPreferredSize(new Dimension(448, 630));
         this.setBackground(new Color(247, 247, 247));
         this.loadImages();
         this.refillPlayableBoard();
         this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
               GamePanel.this.onMousePressed(e.getX(), e.getY());
            }
         });
      }

      void loadImages() {
         this.fruitImages[0] = this.loadImage("/com/smu8/game/fruit_a.png");
         this.fruitImages[1] = this.loadImage("/com/smu8/game/fruit_b.png");
         this.fruitImages[2] = this.loadImage("/com/smu8/game/fruit_c.png");
         this.fruitImages[3] = this.loadImage("/com/smu8/game/fruit_d.png");
         this.fruitImages[4] = this.loadImage("/com/smu8/game/fruit_e.png");
         this.mixItemImage = this.loadImage("/com/smu8/game/item_mix.png");
      }

      Image loadImage(String path) {
         URL url = L23Game.class.getResource(path);
         return url == null ? null : (new ImageIcon(url)).getImage();
      }

      boolean isFruit(int value) {
         return value >= 1 && value <= 5;
      }

      boolean isItem(int value) {
         return value == 100;
      }

      void refillPlayableBoard() {
         do {
            this.fillRandomBoardNoImmediateMatches();
         } while(!this.hasPossibleMove());

      }

      void fillRandomBoardNoImmediateMatches() {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               this.board[r][c] = this.randomValueAvoidingStreak(r, c);
            }
         }

      }

      int randomValueAvoidingStreak(int row, int col) {
         int value;
         boolean makesHorizontal3;
         boolean makesVertical3;
         do {
            value = this.randomFruit();
            makesHorizontal3 = col >= 2 && this.board[row][col - 1] == value && this.board[row][col - 2] == value;
            makesVertical3 = row >= 2 && this.board[row - 1][col] == value && this.board[row - 2][col] == value;
         } while(makesHorizontal3 || makesVertical3);

         return value;
      }

      int randomFruit() {
         return 1 + this.random.nextInt(5);
      }

      void onMousePressed(int x, int y) {
         if (!this.animating && y >= 70) {
            int col = x / 56;
            int row = (y - 70) / 56;
            if (row >= 0 && row < 10 && col >= 0 && col < 8) {
               if (this.selectedRow == -1) {
                  this.selectedRow = row;
                  this.selectedCol = col;
                  this.repaint();
               } else if (this.selectedRow == row && this.selectedCol == col) {
                  this.clearSelection();
                  this.repaint();
               } else if (!this.isAdjacent(this.selectedRow, this.selectedCol, row, col)) {
                  this.selectedRow = row;
                  this.selectedCol = col;
                  this.repaint();
               } else {
                  int firstValue = this.board[this.selectedRow][this.selectedCol];
                  int secondValue = this.board[row][col];
                  boolean firstIsItem = this.isItem(firstValue);
                  boolean secondIsItem = this.isItem(secondValue);
                  this.swap(this.selectedRow, this.selectedCol, row, col);
                  if (!firstIsItem && !secondIsItem) {
                     if (this.hasAnyMatch()) {
                        this.clearSelection();
                        this.animating = true;
                        this.resolveMatchesCascade();
                     } else {
                        this.swap(this.selectedRow, this.selectedCol, row, col);
                        this.clearSelection();
                        this.repaint();
                     }

                  } else {
                     int removed;
                     int scoreMultiplier;
                     if (firstIsItem && secondIsItem) {
                        removed = this.clearAllBlocks();
                        scoreMultiplier = 4;
                     } else {
                        int itemRow = firstIsItem ? row : this.selectedRow;
                        int itemCol = firstIsItem ? col : this.selectedCol;
                        removed = this.activateMixItem(itemRow, itemCol);
                        scoreMultiplier = 2;
                     }

                     this.score += removed * scoreMultiplier;
                     this.clearSelection();
                     this.animating = true;
                     this.resolveMatchesCascade();
                  }
               }
            }
         }
      }

      int activateMixItem(int row, int col) {
         boolean[][] remove = new boolean[10][8];
         if (row >= 0 && row < 10 && col >= 0 && col < 8) {
            remove[row][col] = true;

            for(int c = 0; c < 8; ++c) {
               if (this.board[row][c] != 0) {
                  remove[row][c] = true;
               }
            }

            for(int r = 0; r < 10; ++r) {
               if (this.board[r][col] != 0) {
                  remove[r][col] = true;
               }
            }
         }

         int removed = 0;

         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (remove[r][c] && this.board[r][c] != 0) {
                  this.board[r][c] = 0;
                  ++removed;
               }
            }
         }

         return removed;
      }

      int clearAllBlocks() {
         int removed = 0;

         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (this.board[r][c] != 0) {
                  this.board[r][c] = 0;
                  ++removed;
               }
            }
         }

         return removed;
      }

      boolean isAdjacent(int r1, int c1, int r2, int c2) {
         int dist = Math.abs(r1 - r2) + Math.abs(c1 - c2);
         return dist == 1;
      }

      void clearSelection() {
         this.selectedRow = -1;
         this.selectedCol = -1;
      }

      void swap(int r1, int c1, int r2, int c2) {
         int temp = this.board[r1][c1];
         this.board[r1][c1] = this.board[r2][c2];
         this.board[r2][c2] = temp;
      }

      boolean hasAnyMatch() {
         boolean[][] matched = this.findMatchedCells();

         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (matched[r][c]) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean[][] findMatchedCells() {
         boolean[][] matched = new boolean[10][8];

         for(int r = 0; r < 10; ++r) {
            int c = 0;

            while(c < 8) {
               int value = this.board[r][c];
               if (!this.isFruit(value)) {
                  ++c;
               } else {
                  int start;
                  for(start = c; c + 1 < 8 && this.board[r][c + 1] == value; ++c) {
                  }

                  int len = c - start + 1;
                  if (len >= 3) {
                     for(int k = start; k <= c; ++k) {
                        matched[r][k] = true;
                     }
                  }

                  ++c;
               }
            }
         }

         for(int c = 0; c < 8; ++c) {
            int r = 0;

            while(r < 10) {
               int value = this.board[r][c];
               if (!this.isFruit(value)) {
                  ++r;
               } else {
                  int start;
                  for(start = r; r + 1 < 10 && this.board[r + 1][c] == value; ++r) {
                  }

                  int len = r - start + 1;
                  if (len >= 3) {
                     for(int k = start; k <= r; ++k) {
                        matched[k][c] = true;
                     }
                  }

                  ++r;
               }
            }
         }

         return matched;
      }

      int removeMatchedCellsAndCreateItems() {
         boolean[][] matched = this.findMatchedCells();
         boolean hasMatched = false;

         for(int r = 0; r < 10 && !hasMatched; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (matched[r][c]) {
                  hasMatched = true;
                  break;
               }
            }
         }

         if (!hasMatched) {
            return 0;
         } else {
            boolean[][] visited = new boolean[10][8];
            List<Cell> spawnItemPositions = new ArrayList();
            int[] dr = new int[]{-1, 1, 0, 0};
            int[] dc = new int[]{0, 0, -1, 1};

            for(int r = 0; r < 10; ++r) {
               for(int c = 0; c < 8; ++c) {
                  if (matched[r][c] && !visited[r][c] && this.isFruit(this.board[r][c])) {
                     int fruit = this.board[r][c];
                     ArrayDeque<Cell> queue = new ArrayDeque();
                     List<Cell> component = new ArrayList();
                     queue.add(new Cell(r, c));
                     visited[r][c] = true;

                     while(!queue.isEmpty()) {
                        Cell cur = (Cell)queue.poll();
                        component.add(cur);

                        for(int i = 0; i < 4; ++i) {
                           int nr = cur.row + dr[i];
                           int nc = cur.col + dc[i];
                           if (nr >= 0 && nr < 10 && nc >= 0 && nc < 8 && !visited[nr][nc] && matched[nr][nc] && this.board[nr][nc] == fruit) {
                              visited[nr][nc] = true;
                              queue.add(new Cell(nr, nc));
                           }
                        }
                     }

                     if (component.size() >= 5) {
                        Cell spawn = (Cell)component.get(0);

                        for(Cell cell : component) {
                           if (cell.row > spawn.row || cell.row == spawn.row && cell.col < spawn.col) {
                              spawn = cell;
                           }
                        }

                        spawnItemPositions.add(spawn);
                     }
                  }
               }
            }

            int removed = 0;

            for(int r = 0; r < 10; ++r) {
               for(int c = 0; c < 8; ++c) {
                  if (matched[r][c]) {
                     this.board[r][c] = 0;
                     ++removed;
                  }
               }
            }

            for(Cell pos : spawnItemPositions) {
               if (this.board[pos.row][pos.col] == 0) {
                  this.board[pos.row][pos.col] = 100;
               }
            }

            return removed;
         }
      }

      void resolveMatchesCascade() {
         int removed = this.removeMatchedCellsAndCreateItems();
         if (removed > 0) {
            this.score += removed;
            this.startFallAnimation();
         } else if (this.hasEmptyCell()) {
            this.startFallAnimation();
         } else {
            this.animating = false;
            if (!this.hasPossibleMove()) {
               this.refillPlayableBoard();
            }

            this.repaint();
         }
      }

      boolean hasEmptyCell() {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (this.board[r][c] == 0) {
                  return true;
               }
            }
         }

         return false;
      }

      void startFallAnimation() {
         int[][] targetBoard = new int[10][8];
         List<FallingTile> moving = new ArrayList();

         for(int c = 0; c < 8; ++c) {
            List<Block> survivors = new ArrayList();

            for(int r = 9; r >= 0; --r) {
               if (this.board[r][c] != 0) {
                  survivors.add(new Block(r, this.board[r][c]));
               }
            }

            int writeRow = 9;

            for(Block s : survivors) {
               targetBoard[writeRow][c] = s.value;
               moving.add(new FallingTile(s.value, c, (double)s.row, (double)writeRow));
               --writeRow;
            }

            int emptyCount = writeRow + 1;

            for(int r = writeRow; r >= 0; --r) {
               int value = this.randomFruit();
               targetBoard[r][c] = value;
               double startRow = (double)(r - emptyCount);
               moving.add(new FallingTile(value, c, startRow, (double)r));
            }
         }

         for(int r = 0; r < 10; ++r) {
            System.arraycopy(targetBoard[r], 0, this.board[r], 0, 8);
         }

         this.fallingTiles = moving;
         this.animationProgress = (double)0.0F;
         if (this.animationTimer != null && this.animationTimer.isRunning()) {
            this.animationTimer.stop();
         }

         this.animationTimer = new Timer(16, (e) -> {
            this.animationProgress += this.animationStep;
            if (this.animationProgress >= (double)1.0F) {
               this.animationProgress = (double)1.0F;
               this.animationTimer.stop();
               this.resolveMatchesCascade();
            } else {
               this.repaint();
            }
         });
         this.animationTimer.start();
      }

      boolean hasPossibleMove() {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               if (this.isItem(this.board[r][c]) && (c + 1 < 8 && this.board[r][c + 1] != 0 || c - 1 >= 0 && this.board[r][c - 1] != 0 || r + 1 < 10 && this.board[r + 1][c] != 0 || r - 1 >= 0 && this.board[r - 1][c] != 0)) {
                  return true;
               }

               if (c + 1 < 8) {
                  if (this.isItem(this.board[r][c]) || this.isItem(this.board[r][c + 1])) {
                     return true;
                  }

                  this.swap(r, c, r, c + 1);
                  boolean ok = this.hasAnyMatch();
                  this.swap(r, c, r, c + 1);
                  if (ok) {
                     return true;
                  }
               }

               if (r + 1 < 10) {
                  if (this.isItem(this.board[r][c]) || this.isItem(this.board[r + 1][c])) {
                     return true;
                  }

                  this.swap(r, c, r + 1, c);
                  boolean ok = this.hasAnyMatch();
                  this.swap(r, c, r + 1, c);
                  if (ok) {
                     return true;
                  }
               }
            }
         }

         return false;
      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(new Color(35, 35, 35));
         g2.setFont(new Font("SansSerif", 1, 24));
         g2.drawString("L23 PicoPop", 12, 30);
         g2.setColor(new Color(70, 70, 70));
         g2.setFont(new Font("SansSerif", 0, 18));
         g2.drawString("Score: " + this.score, 12, 58);

         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 8; ++c) {
               int x = c * 56;
               int y = 70 + r * 56;
               g2.setColor(new Color(230, 230, 230));
               g2.fillRoundRect(x + 3, y + 3, 50, 50, 14, 14);
            }
         }

         if (this.animating) {
            for(FallingTile tile : this.fallingTiles) {
               double drawRow = tile.startRow + (tile.endRow - tile.startRow) * this.animationProgress;
               int x = tile.col * 56;
               int y = 70 + (int)Math.round(drawRow * (double)56.0F);
               this.drawTile(g2, tile.value, x, y);
            }
         } else {
            for(int r = 0; r < 10; ++r) {
               for(int c = 0; c < 8; ++c) {
                  int value = this.board[r][c];
                  if (value != 0) {
                     int x = c * 56;
                     int y = 70 + r * 56;
                     this.drawTile(g2, value, x, y);
                  }
               }
            }
         }

         if (!this.animating && this.selectedRow >= 0 && this.selectedCol >= 0) {
            int sx = this.selectedCol * 56;
            int sy = 70 + this.selectedRow * 56;
            g2.setColor(new Color(255, 255, 255));
            g2.setStroke(new BasicStroke(3.0F));
            g2.drawRoundRect(sx + 4, sy + 4, 48, 48, 12, 12);
         }

      }

      void drawTile(Graphics2D g2, int value, int x, int y) {
         if (this.isItem(value)) {
            if (this.mixItemImage != null) {
               g2.drawImage(this.mixItemImage, x + 6, y + 6, 44, 44, (ImageObserver)null);
            } else {
               g2.setColor(new Color(255, 255, 255));
               g2.fillRoundRect(x + 8, y + 8, 40, 40, 12, 12);
               g2.setColor(new Color(50, 50, 50));
               g2.setFont(new Font("SansSerif", 1, 12));
               g2.drawString("MIX", x + 14, y + 33);
            }
         } else {
            Image fruit = this.fruitImages[value - 1];
            if (fruit != null) {
               g2.drawImage(fruit, x + 6, y + 6, 44, 44, (ImageObserver)null);
            } else {
               g2.setColor(this.palette[value - 1]);
               g2.fillOval(x + 8, y + 8, 40, 40);
            }
         }
      }
   }
}

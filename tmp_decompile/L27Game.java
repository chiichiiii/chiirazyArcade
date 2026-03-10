package com.smu8.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class L27Game extends JFrame {
   public L27Game() {
      super("L27 Breakout");
      this.setDefaultCloseOperation(3);
      BreakoutPanel panel = new BreakoutPanel();
      JButton newGameButton = new JButton("New Game");
      newGameButton.addActionListener((e) -> panel.startNewGame());
      JPanel bottom = new JPanel(new FlowLayout(1, 8, 8));
      bottom.add(new JLabel("Left/Right: Move, Space: Start/Pause, R: Restart"));
      bottom.add(newGameButton);
      this.setLayout(new BorderLayout());
      this.add(panel, "Center");
      this.add(bottom, "South");
      this.pack();
      this.setLocation(560, 140);
      this.setResizable(false);
      this.setVisible(true);
      panel.requestFocusInWindow();
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L27Game::new);
   }

   static class BreakoutPanel extends JPanel {
      private static final int WIDTH = 720;
      private static final int HEIGHT = 520;
      private static final int PADDLE_W = 120;
      private static final int PADDLE_H = 14;
      private static final int PADDLE_Y = 478;
      private static final int BALL_R = 10;
      private static final double BALL_INIT_SPEED = 5.2;
      private static final double BALL_MAX_SPEED = 9.8;
      private static final double BALL_TICK_ACCEL = 1.00032;
      private static final int BRICK_ROWS = 10;
      private static final int BRICK_COLS = 14;
      private static final int BRICK_W = 44;
      private static final int BRICK_H = 16;
      private static final int BRICK_GAP = 4;
      private static final int BRICK_TOP = 72;
      private static final int EMPTY = -1;
      private final int[][] bricks = new int[10][14];
      private final Color[] palette = new Color[]{new Color(236, 95, 95), new Color(255, 173, 79), new Color(255, 215, 96), new Color(125, 212, 125), new Color(92, 173, 245), new Color(179, 136, 255)};
      private final Timer timer;
      private final Random random = new Random();
      private int paddleX;
      private double ballX;
      private double ballY;
      private double ballDx;
      private double ballDy;
      private int ballColor;
      private int score;
      private int lives;
      private boolean leftPressed;
      private boolean rightPressed;
      private boolean running;
      private boolean waitingServe;
      private boolean gameOver;
      private boolean cleared;

      BreakoutPanel() {
         this.setPreferredSize(new Dimension(720, 520));
         this.setBackground(new Color(17, 22, 30));
         this.setFocusable(true);
         this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == 37) {
                  BreakoutPanel.this.leftPressed = true;
               } else if (e.getKeyCode() == 39) {
                  BreakoutPanel.this.rightPressed = true;
               } else if (e.getKeyCode() == 32) {
                  BreakoutPanel.this.onSpace();
               } else if (e.getKeyCode() == 82) {
                  BreakoutPanel.this.startNewGame();
               }

            }

            public void keyReleased(KeyEvent e) {
               if (e.getKeyCode() == 37) {
                  BreakoutPanel.this.leftPressed = false;
               } else if (e.getKeyCode() == 39) {
                  BreakoutPanel.this.rightPressed = false;
               }

            }
         });
         this.timer = new Timer(16, this::onTick);
         this.timer.start();
         this.startNewGame();
      }

      void startNewGame() {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 14; ++c) {
               this.bricks[r][c] = this.random.nextInt(this.palette.length);
            }
         }

         this.score = 0;
         this.lives = 3;
         this.gameOver = false;
         this.cleared = false;
         this.running = false;
         this.waitingServe = true;
         this.paddleX = 300;
         this.ballColor = this.random.nextInt(this.palette.length);
         this.resetBallOnPaddle();
         this.repaint();
         this.requestFocusInWindow();
      }

      private void onSpace() {
         if (!this.gameOver && !this.cleared) {
            if (this.waitingServe) {
               this.launchBall();
               this.running = true;
               this.waitingServe = false;
            } else {
               this.running = !this.running;
            }
         }
      }

      private void launchBall() {
         double angle = Math.toRadians((double)(55 + this.random.nextInt(70)));
         if (this.random.nextBoolean()) {
            angle = Math.PI - angle;
         }

         this.ballDx = Math.cos(angle) * 5.2;
         this.ballDy = -Math.abs(Math.sin(angle) * 5.2);
      }

      private void resetBallOnPaddle() {
         this.ballX = (double)this.paddleX + (double)60.0F;
         this.ballY = (double)466.0F;
         this.ballDx = (double)0.0F;
         this.ballDy = (double)0.0F;
      }

      private int randomDifferentColor(int current) {
         int next;
         for(next = current; next == current; next = this.random.nextInt(this.palette.length)) {
         }

         return next;
      }

      private void onTick(ActionEvent e) {
         this.updatePaddle();
         if (!this.running) {
            if (this.waitingServe) {
               this.ballX = (double)this.paddleX + (double)60.0F;
            }

            this.repaint();
         } else {
            this.updateBall();
            this.repaint();
         }
      }

      private void updatePaddle() {
         int speed = 13;
         if (this.leftPressed) {
            this.paddleX -= speed;
         }

         if (this.rightPressed) {
            this.paddleX += speed;
         }

         this.paddleX = Math.max(0, Math.min(600, this.paddleX));
      }

      private void updateBall() {
         double prevX = this.ballX;
         double prevY = this.ballY;
         this.ballX += this.ballDx;
         this.ballY += this.ballDy;
         this.accelerateBall();
         if (this.ballX - (double)10.0F <= (double)0.0F) {
            this.ballX = (double)10.0F;
            this.ballDx = Math.abs(this.ballDx);
         } else if (this.ballX + (double)10.0F >= (double)720.0F) {
            this.ballX = (double)710.0F;
            this.ballDx = -Math.abs(this.ballDx);
         }

         if (this.ballY - (double)10.0F <= (double)0.0F) {
            this.ballY = (double)10.0F;
            this.ballDy = Math.abs(this.ballDy);
         }

         Rectangle paddleRect = new Rectangle(this.paddleX, 478, 120, 14);
         Rectangle ballRect = this.ballBounds();
         if (this.ballDy > (double)0.0F && ballRect.intersects(paddleRect)) {
            this.ballY = (double)468.0F;
            double speed = this.ballSpeed();
            double hit = (this.ballX - ((double)this.paddleX + (double)60.0F)) / (double)60.0F;
            this.ballDx = hit * speed * 0.95;
            double dyAbs = Math.sqrt(Math.max((double)4.5F, speed * speed - this.ballDx * this.ballDx));
            this.ballDy = -dyAbs;
            this.accelerateBall();
         }

         if (this.handleBrickCollision(prevX, prevY)) {
         }

         if (this.ballY - (double)10.0F > (double)520.0F) {
            --this.lives;
            if (this.lives <= 0) {
               this.running = false;
               this.waitingServe = false;
               this.gameOver = true;
            } else {
               this.running = false;
               this.waitingServe = true;
               this.ballColor = this.random.nextInt(this.palette.length);
               this.resetBallOnPaddle();
            }
         }

      }

      private double ballSpeed() {
         return Math.sqrt(this.ballDx * this.ballDx + this.ballDy * this.ballDy);
      }

      private void accelerateBall() {
         double speed = this.ballSpeed();
         if (!(speed < 1.0E-4) && !(speed >= 9.8)) {
            double next = Math.min(9.8, speed * 1.00032);
            double factor = next / speed;
            this.ballDx *= factor;
            this.ballDy *= factor;
         }
      }

      private boolean handleBrickCollision(double prevX, double prevY) {
         Rectangle ballRect = this.ballBounds();

         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 14; ++c) {
               if (this.bricks[r][c] != -1) {
                  int bx = this.brickX(c);
                  int by = this.brickY(r);
                  Rectangle brickRect = new Rectangle(bx, by, 44, 16);
                  if (ballRect.intersects(brickRect)) {
                     double prevBallLeft = prevX - (double)10.0F;
                     double prevBallRight = prevX + (double)10.0F;
                     double prevBallTop = prevY - (double)10.0F;
                     double prevBallBottom = prevY + (double)10.0F;
                     boolean hitFromLeft = prevBallRight <= (double)bx;
                     boolean hitFromRight = prevBallLeft >= (double)(bx + 44);
                     boolean hitFromTop = prevBallBottom <= (double)by;
                     boolean hitFromBottom = prevBallTop >= (double)(by + 16);
                     if (!hitFromLeft && !hitFromRight) {
                        if (!hitFromTop && !hitFromBottom) {
                           this.ballDy = -this.ballDy;
                        } else {
                           this.ballDy = -this.ballDy;
                        }
                     } else {
                        this.ballDx = -this.ballDx;
                     }

                     if (this.bricks[r][c] == this.ballColor) {
                        this.bricks[r][c] = -1;
                        this.score += 120;
                        this.ballColor = this.randomDifferentColor(this.ballColor);
                        if (this.allBricksCleared()) {
                           this.running = false;
                           this.waitingServe = false;
                           this.cleared = true;
                        }
                     }

                     return true;
                  }
               }
            }
         }

         return false;
      }

      private boolean allBricksCleared() {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 14; ++c) {
               if (this.bricks[r][c] != -1) {
                  return false;
               }
            }
         }

         return true;
      }

      private int brickX(int col) {
         int totalWidth = 668;
         int startX = (720 - totalWidth) / 2;
         return startX + col * 48;
      }

      private int brickY(int row) {
         return 72 + row * 20;
      }

      private Rectangle ballBounds() {
         return new Rectangle((int)Math.round(this.ballX - (double)10.0F), (int)Math.round(this.ballY - (double)10.0F), 20, 20);
      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         this.drawBackground(g2);
         this.drawHud(g2);
         this.drawBricks(g2);
         this.drawPaddle(g2);
         this.drawBall(g2);
         this.drawOverlay(g2);
      }

      private void drawBackground(Graphics2D g2) {
         GradientPaint gp = new GradientPaint(0.0F, 0.0F, new Color(19, 26, 38), 0.0F, 520.0F, new Color(10, 14, 20));
         g2.setPaint(gp);
         g2.fillRect(0, 0, 720, 520);
      }

      private void drawHud(Graphics2D g2) {
         g2.setColor(new Color(230, 237, 248));
         g2.setFont(new Font("SansSerif", 1, 24));
         g2.drawString("L27 Breakout", 20, 32);
         g2.setFont(new Font("SansSerif", 0, 18));
         g2.drawString("Score: " + this.score, 20, 58);
         g2.drawString("Lives: " + this.lives, 610, 58);
         g2.drawString("Ball:", 475, 58);
         g2.setColor(this.palette[this.ballColor]);
         g2.fillOval(535, 43, 20, 20);
         g2.setColor(new Color(255, 255, 255, 180));
         g2.drawOval(535, 43, 20, 20);
      }

      private void drawBricks(Graphics2D g2) {
         for(int r = 0; r < 10; ++r) {
            for(int c = 0; c < 14; ++c) {
               int colorIndex = this.bricks[r][c];
               if (colorIndex != -1) {
                  int x = this.brickX(c);
                  int y = this.brickY(r);
                  g2.setColor(this.palette[colorIndex]);
                  g2.fillRoundRect(x, y, 44, 16, 8, 8);
                  g2.setColor(new Color(255, 255, 255, 120));
                  g2.drawRoundRect(x, y, 44, 16, 8, 8);
               }
            }
         }

      }

      private void drawPaddle(Graphics2D g2) {
         g2.setColor(new Color(241, 244, 250));
         g2.fillRoundRect(this.paddleX, 478, 120, 14, 10, 10);
      }

      private void drawBall(Graphics2D g2) {
         g2.setColor(this.palette[this.ballColor]);
         g2.fillOval((int)Math.round(this.ballX - (double)10.0F), (int)Math.round(this.ballY - (double)10.0F), 20, 20);
         g2.setColor(new Color(255, 255, 255, 170));
         g2.drawOval((int)Math.round(this.ballX - (double)10.0F), (int)Math.round(this.ballY - (double)10.0F), 20, 20);
      }

      private void drawOverlay(Graphics2D g2) {
         if (this.gameOver) {
            this.drawMessage(g2, "Game Over - Press R or New Game");
         } else if (this.cleared) {
            this.drawMessage(g2, "Stage Clear - Press R or New Game");
         } else if (this.waitingServe) {
            this.drawMessage(g2, "Press Space to Launch");
         } else {
            if (!this.running) {
               this.drawMessage(g2, "Paused");
            }

         }
      }

      private void drawMessage(Graphics2D g2, String text) {
         g2.setColor(new Color(0, 0, 0, 120));
         g2.fillRoundRect(170, 225, 380, 70, 16, 16);
         g2.setColor(new Color(255, 255, 255));
         g2.setFont(new Font("SansSerif", 1, 24));
         FontMetrics fm = g2.getFontMetrics();
         int x = (720 - fm.stringWidth(text)) / 2;
         g2.drawString(text, x, 268);
      }
   }
}

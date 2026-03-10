package com.smu8.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

class GamePanel extends JPanel {
   private Player player;
   private List enemies;
   private List missiles;
   private Random random = new Random();
   private boolean gameOver = false;
   private int gameOverTimer = 0;
   private int spawnTimer = 0;
   private int score = 0;

   public GamePanel() {
      this.setBackground(Color.BLACK);
      this.setFocusable(true);
      this.initGame();
      this.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (!GamePanel.this.gameOver) {
               GamePanel.this.player.keyPressed(e.getKeyCode());
               if (e.getKeyCode() == 32) {
                  GamePanel.this.missiles.add(new Missile((double)(GamePanel.this.player.x + 12), (double)GamePanel.this.player.y));
               }

            }
         }
      });
      Timer timer = new Timer(16, (e) -> {
         if (!this.gameOver) {
            this.updateGame();
            this.checkCollision();
         } else {
            ++this.gameOverTimer;
            if (this.gameOverTimer > 120) {
               this.initGame();
            }
         }

         this.repaint();
      });
      timer.start();
   }

   private void initGame() {
      this.player = new Player(380, 500);
      this.enemies = new ArrayList();
      this.missiles = new ArrayList();
      this.score = 0;
      this.gameOver = false;
      this.gameOverTimer = 0;
      this.spawnTimer = 0;
   }

   private void updateGame() {
      this.player.move(this.getWidth(), this.getHeight());
      Iterator<Missile> mIter = this.missiles.iterator();

      while(mIter.hasNext()) {
         Missile m = (Missile)mIter.next();
         m.move();
         if (m.y < (double)0.0F) {
            mIter.remove();
         }
      }

      for(Enemy enemy : this.enemies) {
         enemy.chase(this.player);
      }

      ++this.spawnTimer;
      if (this.spawnTimer > 60) {
         this.enemies.add(new Enemy(this.random.nextInt(750), 0));
         this.spawnTimer = 0;
      }

   }

   private void checkCollision() {
      Iterator<Missile> mIter = this.missiles.iterator();

      while(mIter.hasNext()) {
         Missile m = (Missile)mIter.next();
         Iterator<Enemy> eIter = this.enemies.iterator();

         while(eIter.hasNext()) {
            Enemy enemy = (Enemy)eIter.next();
            double dx = m.x - enemy.x;
            double dy = m.y - enemy.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < (double)20.0F) {
               mIter.remove();
               eIter.remove();
               this.score += 10;
               break;
            }
         }
      }

      for(Enemy enemy : this.enemies) {
         double dx = (double)this.player.x - enemy.x;
         double dy = (double)this.player.y - enemy.y;
         if (Math.sqrt(dx * dx + dy * dy) < (double)25.0F) {
            this.gameOver = true;
         }
      }

   }

   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      this.player.draw(g);

      for(Missile m : this.missiles) {
         m.draw(g);
      }

      for(Enemy enemy : this.enemies) {
         enemy.draw(g);
      }

      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", 1, 20));
      g.drawString("Score: " + this.score, 10, 20);
      if (this.gameOver) {
         g.setFont(new Font("Arial", 1, 50));
         g.drawString("YOU DIED", 250, 300);
      }

   }
}

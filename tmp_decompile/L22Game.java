package com.smu8.game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class L22Game extends JFrame {
   public L22Game() {
      super("Money Eat Game");
      GamePanel canvas = new GamePanel();
      this.setContentPane(canvas);
      this.setSize(500, 500);
      this.setLocationRelativeTo((Component)null);
      this.setDefaultCloseOperation(3);
      this.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L22Game::new);
   }

   static class Player {
      int x;
      int y;
      int size;
      int speed = 6;
      Image image;

      public Player(int x, int y, int size, Image image) {
         this.x = x;
         this.y = y;
         this.size = 80;
         this.image = image;
      }

      public void update(boolean[] keys, int panelWidth, int panelHeight) {
         if (keys[65] || keys[37]) {
            this.x -= this.speed;
         }

         if (keys[68] || keys[39]) {
            this.x += this.speed;
         }

         if (this.x < 0) {
            this.x = 0;
         }

         if (this.x > panelWidth - this.size) {
            this.x = panelWidth - this.size;
         }

      }

      public Rectangle getBounds() {
         return new Rectangle(this.x, this.y, this.size, this.size);
      }

      public void draw(Graphics g) {
         if (this.image != null) {
            g.drawImage(this.image, this.x, this.y, this.size, this.size, (ImageObserver)null);
         } else {
            g.setColor(new Color(60, 120, 200));
            g.fillOval(this.x, this.y, this.size, this.size);
         }
      }
   }

   static class Money {
      double x;
      double y;
      int width;
      int height;
      int value;
      double speed;
      Image image;

      public Money(double x, double y, int width, int height, int value, double speed, Image image) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.value = value;
         this.speed = speed;
         this.image = image;
      }

      public void update() {
         this.y += this.speed;
      }

      public Rectangle getBounds() {
         return new Rectangle((int)this.x, (int)this.y, this.width, this.height);
      }

      public void draw(Graphics g) {
         if (this.image != null) {
            g.drawImage(this.image, (int)this.x, (int)this.y, this.width, this.height, (ImageObserver)null);
         } else {
            g.setColor(this.value == 100 ? new Color(90, 180, 90) : new Color(220, 180, 40));
            if (this.value == 100) {
               g.fillRoundRect((int)this.x, (int)this.y, this.width, this.height, 8, 8);
               g.setColor(Color.BLACK);
               g.drawString("100", (int)this.x + 6, (int)this.y + this.height - 6);
            } else {
               g.fillOval((int)this.x, (int)this.y, this.width, this.height);
               g.setColor(Color.BLACK);
               g.drawString("50", (int)this.x + 4, (int)this.y + this.height - 4);
            }

         }
      }
   }

   class GamePanel extends JPanel {
      Player player;
      List moneyList = new ArrayList();
      Timer timer;
      Random random = new Random();
      boolean[] keys = new boolean[256];
      int score = 0;
      int playerSize = 50;
      int billWidth = 40;
      int billHeight = 22;
      int coinSize = 22;
      Image playerImage;
      Image billImage;
      Image coinImage;

      public GamePanel() {
         this.setBackground(new Color(220, 235, 200));
         this.setFocusable(true);
         this.loadImages();
         this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               int code = e.getKeyCode();
               if (code >= 0 && code < GamePanel.this.keys.length) {
                  GamePanel.this.keys[code] = true;
               }

            }

            public void keyReleased(KeyEvent e) {
               int code = e.getKeyCode();
               if (code >= 0 && code < GamePanel.this.keys.length) {
                  GamePanel.this.keys[code] = false;
               }

            }
         });
         this.initGame();
         this.timer = new Timer(16, (e) -> this.onTick());
         this.timer.start();
      }

      private void loadImages() {
         this.playerImage = this.loadScaled("/com/smu8/game/player.png", this.playerSize, this.playerSize);
         this.billImage = this.loadScaled("/com/smu8/game/bill.png", this.billWidth, this.billHeight);
         this.coinImage = this.loadScaled("/com/smu8/game/coin.png", this.coinSize, this.coinSize);
      }

      private Image loadScaled(String path, int width, int height) {
         URL url = L22Game.class.getResource(path);
         if (url == null) {
            return null;
         } else {
            ImageIcon icon = new ImageIcon(url);
            return icon.getImage().getScaledInstance(width, height, 4);
         }
      }

      private void initGame() {
         int startX = 240;
         int startY = 400;
         this.player = new Player(startX, startY, this.playerSize, this.playerImage);
         this.moneyList.clear();
         this.score = 0;

         for(int i = 0; i < 12; ++i) {
            this.moneyList.add(this.createMoney());
         }

         this.repaint();
         this.requestFocusInWindow();
      }

      private Money createMoney() {
         int width;
         int height;
         int value;
         Image image;
         if (this.random.nextBoolean()) {
            width = this.billWidth;
            height = this.billHeight;
            value = 100;
            image = this.billImage;
         } else {
            width = this.coinSize;
            height = this.coinSize;
            value = 50;
            image = this.coinImage;
         }

         int maxX = Math.max(1, this.getWidth() - width);
         double x = (double)this.random.nextInt(maxX);
         double y = (double)(-this.random.nextInt(300) - height);
         double speed = (double)2.0F + this.random.nextDouble() * 2.2;
         return new Money(x, y, width, height, value, speed, image);
      }

      private void resetMoney(Money money) {
         int maxX = Math.max(1, this.getWidth() - money.width);
         money.x = (double)this.random.nextInt(maxX);
         money.y = (double)(-this.random.nextInt(300) - money.height);
         money.speed = (double)2.0F + this.random.nextDouble() * 2.2;
      }

      private void onTick() {
         this.player.update(this.keys, this.getWidth(), this.getHeight());
         Rectangle playerBounds = this.player.getBounds();
         int height = this.getHeight();

         for(Money money : this.moneyList) {
            money.update();
            if (money.y > (double)(height + money.height)) {
               this.resetMoney(money);
            } else if (playerBounds.intersects(money.getBounds())) {
               this.score += money.value;
               this.resetMoney(money);
            }
         }

         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         this.player.draw(g);

         for(Money money : this.moneyList) {
            money.draw(g);
         }

         g.setColor(Color.BLACK);
         g.setFont(new Font("SansSerif", 1, 16));
         g.drawString("Score: " + this.score, 10, 20);
      }
   }
}

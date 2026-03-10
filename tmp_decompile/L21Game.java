package com.smu8.game;

import java.awt.Color;
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

public class L21Game extends JFrame {
   public L21Game() {
      super("Dodge Game");
      GamePanel canvas = new GamePanel();
      this.setContentPane(canvas);
      this.setBounds(550, 250, 500, 500);
      this.setDefaultCloseOperation(3);
      this.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L21Game::new);
   }

   static class Player {
      int x;
      int y;
      int size = 50;
      int speed = 6;
      Color color = new Color(0, 0, 0);

      public Player(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public void update(boolean[] keys, int panelWidth, int panelHeight) {
         if (keys[87] || keys[38]) {
            this.y -= this.speed;
         }

         if (keys[83] || keys[40]) {
            this.y += this.speed;
         }

         if (keys[65] || keys[37]) {
            this.x -= this.speed;
         }

         if (keys[68] || keys[39]) {
            this.x += this.speed;
         }

         if (this.x < 0) {
            this.x = 0;
         }

         if (this.y < 0) {
            this.y = 0;
         }

         if (this.x > panelWidth - this.size) {
            this.x = panelWidth - this.size;
         }

         if (this.y > panelHeight - this.size) {
            this.y = panelHeight - this.size;
         }

      }

      public Rectangle getBounds() {
         int hitSize = Math.max(14, (int)((double)this.size * 0.45));
         int offset = (this.size - hitSize) / 2;
         return new Rectangle(this.x + offset, this.y + offset, hitSize, hitSize);
      }

      public void draw(Graphics g, Image image) {
         if (image != null) {
            g.drawImage(image, this.x, this.y, this.size, this.size, (ImageObserver)null);
         } else {
            g.setColor(this.color);
            g.fillOval(this.x, this.y, this.size, this.size);
         }
      }
   }

   static class Enemy {
      double x;
      double y;
      int size;
      double vx;
      double vy;
      Color color;

      public Enemy(double x, double y, int size, double vx, double vy, Color color) {
         this.x = x;
         this.y = y;
         this.size = size;
         this.vx = vx;
         this.vy = vy;
         this.color = color;
      }

      public void update() {
         this.x += this.vx;
         this.y += this.vy;
      }

      public Rectangle getBounds() {
         int hitSize = Math.max(12, (int)((double)this.size * 0.6));
         int offset = (this.size - hitSize) / 2;
         return new Rectangle((int)this.x + offset, (int)this.y + offset, hitSize, hitSize);
      }

      public void draw(Graphics g, Image image) {
         if (image != null) {
            g.drawImage(image, (int)this.x, (int)this.y, this.size, this.size, (ImageObserver)null);
         } else {
            g.setColor(this.color);
            g.fillOval((int)this.x, (int)this.y, this.size, this.size);
         }
      }
   }

   static enum ItemType {
      FIRE,
      PUSH,
      SHIELD;

      // $FF: synthetic method
      private static ItemType[] $values() {
         return new ItemType[]{FIRE, PUSH, SHIELD};
      }
   }

   static class Item {
      int x;
      int y;
      int size;
      ItemType type;
      Color color;

      public Item(int x, int y, int size, ItemType type, Color color) {
         this.x = x;
         this.y = y;
         this.size = size;
         this.type = type;
         this.color = color;
      }

      public Rectangle getBounds() {
         int hitSize = Math.max(12, (int)((double)this.size * (double)0.5F));
         int offset = (this.size - hitSize) / 2;
         return new Rectangle(this.x + offset, this.y + offset, hitSize, hitSize);
      }

      public void draw(Graphics g, Image image) {
         if (image != null) {
            g.drawImage(image, this.x, this.y, this.size, this.size, (ImageObserver)null);
         } else {
            g.setColor(this.color);
            g.fillOval(this.x, this.y, this.size, this.size);
         }
      }
   }

   class GamePanel extends JPanel {
      Player player;
      List enemies = new ArrayList();
      List items = new ArrayList();
      Timer timer;
      Random random = new Random();
      boolean[] keys = new boolean[256];
      int spawnCounter = 0;
      int score = 0;
      boolean gameOver = false;
      int roachSize = 30;
      int itemSize = 50;
      int shieldItemSize = 40;
      int fireRadius = 120;
      int pushRadius = 140;
      double pushPower = (double)4.5F;
      int invincibleTicks = 0;
      int invincibleDuration = 180;
      Image roachImage;
      Image backgroundImage;
      Image playerImage;
      Image playerLeftImage;
      Image playerRightImage;
      int playerFacingDirection = 0;
      Image fireItemImage;
      Image pushItemImage;
      Image shieldItemImage;

      public GamePanel() {
         this.setBackground(new Color(200, 200, 200));
         this.setFocusable(true);
         this.loadRoachImage();
         this.loadBackgroundImage();
         this.loadPlayerImage();
         this.loadItemImages();
         this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               int code = e.getKeyCode();
               if (code >= 0 && code < GamePanel.this.keys.length) {
                  GamePanel.this.keys[code] = true;
               }

               if (GamePanel.this.gameOver && code == 82) {
                  GamePanel.this.initGame();
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

      private void loadRoachImage() {
         URL url = L21Game.class.getResource("/com/smu8/game/roach.png");
         if (url == null) {
            this.roachImage = null;
         } else {
            ImageIcon icon = new ImageIcon(url);
            this.roachImage = icon.getImage().getScaledInstance(this.roachSize, this.roachSize, 4);
         }
      }

      private void loadBackgroundImage() {
         this.backgroundImage = this.loadImage("/com/smu8/game/floor.png");
      }

      private void loadPlayerImage() {
         this.playerImage = this.loadImageFirstAvailable("/com/smu8/game/player_idle.png", "/com/smu8/game/player.png", "/com/smu8/game/player_custom.png");
         this.playerLeftImage = this.loadImage("/com/smu8/game/player_left.png");
         this.playerRightImage = this.loadImage("/com/smu8/game/player_right.png");
      }

      private void loadItemImages() {
         this.fireItemImage = this.loadImage("/com/smu8/game/item_fire.png");
         this.pushItemImage = this.loadImage("/com/smu8/game/item_push.png");
         this.shieldItemImage = this.loadImage("/com/smu8/game/item_shield.png");
      }

      private Image loadImage(String path) {
         URL url = L21Game.class.getResource(path);
         return url == null ? null : (new ImageIcon(url)).getImage();
      }

      private Image loadImageFirstAvailable(String... paths) {
         for(String path : paths) {
            Image image = this.loadImage(path);
            if (image != null) {
               return image;
            }
         }

         return null;
      }

      private int safeWidth() {
         return Math.max(1, this.getWidth() == 0 ? 500 : this.getWidth());
      }

      private int safeHeight() {
         return Math.max(1, this.getHeight() == 0 ? 500 : this.getHeight());
      }

      private void initGame() {
         int startX = 240;
         int startY = 240;
         this.player = new Player(startX, startY);
         this.enemies.clear();
         this.items.clear();
         this.spawnCounter = 0;
         this.score = 0;
         this.gameOver = false;
         this.invincibleTicks = 0;
         this.playerFacingDirection = 0;

         for(int i = 0; i < 3; ++i) {
            this.items.add(this.spawnItem());
         }

         this.repaint();
         this.requestFocusInWindow();
      }

      private void spawnEnemy() {
         int size = this.roachSize;
         int width = this.getWidth();
         int height = this.getHeight();
         int side = this.random.nextInt(4);
         double x;
         double y;
         switch (side) {
            case 0:
               x = (double)this.random.nextInt(Math.max(1, width - size));
               y = (double)(-size);
               break;
            case 1:
               x = (double)this.random.nextInt(Math.max(1, width - size));
               y = (double)(height + size);
               break;
            case 2:
               x = (double)(-size);
               y = (double)this.random.nextInt(Math.max(1, height - size));
               break;
            default:
               x = (double)(width + size);
               y = (double)this.random.nextInt(Math.max(1, height - size));
         }

         double targetX = (double)this.random.nextInt(Math.max(1, width));
         double targetY = (double)this.random.nextInt(Math.max(1, height));
         double dx = targetX - x;
         double dy = targetY - y;
         double distance = Math.sqrt(dx * dx + dy * dy);
         if (distance == (double)0.0F) {
            distance = (double)1.0F;
         }

         double baseSpeed = 1.6 + this.random.nextDouble() * 2.2 + (double)this.score / (double)900.0F;
         double vx = dx / distance * baseSpeed;
         double vy = dy / distance * baseSpeed;
         Color color = new Color(220, 50, 50);
         this.enemies.add(new Enemy(x, y, size, vx, vy, color));
      }

      private Item spawnItem() {
         int width = this.safeWidth();
         int height = this.safeHeight();
         int maxItemSize = Math.max(this.itemSize, this.shieldItemSize);
         int maxX = Math.max(1, width - maxItemSize);
         int maxY = Math.max(1, height - maxItemSize);
         int x = this.random.nextInt(maxX);
         int y = this.random.nextInt(maxY);
         int pick = this.random.nextInt(3);
         ItemType type;
         Color color;
         int size;
         if (pick == 0) {
            type = L21Game.ItemType.FIRE;
            color = new Color(220, 40, 40);
            size = this.itemSize;
         } else if (pick == 1) {
            type = L21Game.ItemType.PUSH;
            color = new Color(40, 90, 220);
            size = this.itemSize;
         } else {
            type = L21Game.ItemType.SHIELD;
            color = new Color(230, 210, 40);
            size = this.shieldItemSize;
         }

         return new Item(x, y, size, type, color);
      }

      private void applyFireItem() {
         int centerX = this.player.x + this.player.size / 2;
         int centerY = this.player.y + this.player.size / 2;

         for(int i = this.enemies.size() - 1; i >= 0; --i) {
            Enemy enemy = (Enemy)this.enemies.get(i);
            double dx = enemy.x + (double)enemy.size / (double)2.0F - (double)centerX;
            double dy = enemy.y + (double)enemy.size / (double)2.0F - (double)centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= (double)this.fireRadius) {
               this.enemies.remove(i);
            }
         }

      }

      private void applyPushItem() {
         int centerX = this.player.x + this.player.size / 2;
         int centerY = this.player.y + this.player.size / 2;

         for(Enemy enemy : this.enemies) {
            double dx = enemy.x + (double)enemy.size / (double)2.0F - (double)centerX;
            double dy = enemy.y + (double)enemy.size / (double)2.0F - (double)centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= (double)this.pushRadius) {
               if (distance == (double)0.0F) {
                  distance = (double)1.0F;
               }

               enemy.vx += dx / distance * this.pushPower;
               enemy.vy += dy / distance * this.pushPower;
            }
         }

      }

      private void applyShieldItem() {
         this.invincibleTicks = this.invincibleDuration;
      }

      private Image getItemImage(ItemType type) {
         if (type == L21Game.ItemType.FIRE) {
            return this.fireItemImage;
         } else {
            return type == L21Game.ItemType.PUSH ? this.pushItemImage : this.shieldItemImage;
         }
      }

      private Image getCurrentPlayerImage() {
         if (this.playerFacingDirection < 0 && this.playerLeftImage != null) {
            return this.playerLeftImage;
         } else {
            return this.playerFacingDirection > 0 && this.playerRightImage != null ? this.playerRightImage : this.playerImage;
         }
      }

      private void onTick() {
         if (this.gameOver) {
            this.repaint();
         } else {
            if (this.invincibleTicks > 0) {
               --this.invincibleTicks;
            }

            this.player.update(this.keys, this.getWidth(), this.getHeight());
            if (this.keys[65] && !this.keys[68]) {
               this.playerFacingDirection = -1;
            } else if (this.keys[68] && !this.keys[65]) {
               this.playerFacingDirection = 1;
            } else {
               this.playerFacingDirection = 0;
            }

            ++this.spawnCounter;
            ++this.score;
            if (this.spawnCounter % 8 == 0) {
               for(int i = 0; i < 2; ++i) {
                  this.spawnEnemy();
               }
            }

            int width = this.getWidth();
            int height = this.getHeight();

            for(int i = this.enemies.size() - 1; i >= 0; --i) {
               Enemy enemy = (Enemy)this.enemies.get(i);
               enemy.update();
               if (enemy.x < (double)(-enemy.size * 2) || enemy.x > (double)(width + enemy.size * 2) || enemy.y < (double)(-enemy.size * 2) || enemy.y > (double)(height + enemy.size * 2)) {
                  this.enemies.remove(i);
               }
            }

            Rectangle playerBounds = this.player.getBounds();

            for(int i = 0; i < this.items.size(); ++i) {
               Item item = (Item)this.items.get(i);
               if (playerBounds.intersects(item.getBounds())) {
                  if (item.type == L21Game.ItemType.FIRE) {
                     this.applyFireItem();
                  } else if (item.type == L21Game.ItemType.PUSH) {
                     this.applyPushItem();
                  } else {
                     this.applyShieldItem();
                  }

                  this.items.set(i, this.spawnItem());
               }
            }

            if (this.invincibleTicks == 0) {
               for(Enemy enemy : this.enemies) {
                  if (playerBounds.intersects(enemy.getBounds())) {
                     this.gameOver = true;
                     break;
                  }
               }
            }

            this.repaint();
         }
      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (this.backgroundImage != null) {
            g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
         }

         this.player.draw(g, this.getCurrentPlayerImage());

         for(Item item : this.items) {
            item.draw(g, this.getItemImage(item.type));
         }

         for(Enemy enemy : this.enemies) {
            enemy.draw(g, this.roachImage);
         }

         g.setColor(Color.BLACK);
         g.setFont(new Font("SansSerif", 1, 16));
         g.drawString("Score: " + this.score, 10, 20);
         if (this.invincibleTicks > 0) {
            g.setColor(new Color(230, 210, 40));
            g.drawString("INVINCIBLE", 10, 40);
         }

         if (this.gameOver) {
            g.setFont(new Font("SansSerif", 1, 28));
            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", 150, 220);
            g.setFont(new Font("SansSerif", 0, 16));
            g.drawString("Press R to restart", 170, 250);
         }

      }
   }
}

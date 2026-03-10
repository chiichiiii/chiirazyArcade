package com.smu8.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class L29Game {
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         JFrame frame = new JFrame("Tower Defense");
         frame.setDefaultCloseOperation(3);
         GamePanel panel = new GamePanel();
         frame.setContentPane(panel);
         frame.pack();
         frame.setResizable(false);
         frame.setLocationRelativeTo((Component)null);
         frame.setVisible(true);
         panel.requestFocusInWindow();
      });
   }

   static class GamePanel extends JPanel {
      private static final int FRAME_MS = 16;
      private static final Color GRID_COLOR = new Color(65, 65, 65);
      private static final Color BOARD_BG = new Color(38, 38, 38);
      private static final Color PATH_COLOR = new Color(137, 110, 79);
      private static final Color SPAWN_COLOR = new Color(70, 170, 70);
      private static final Color GOAL_COLOR = new Color(180, 70, 70);
      private static final Color TOWER_COLOR = new Color(100, 170, 240);
      private static final Color ENEMY_COLOR = new Color(232, 138, 48);
      private static final Color MINI_BOSS_COLOR = new Color(255, 120, 70);
      private static final Color KING_BOSS_COLOR = new Color(255, 75, 120);
      private static final Color UI_BG = new Color(25, 25, 25);
      private final GameState state = new GameState();
      private final Timer timer;
      private long lastTickNanos;
      private boolean buildMode = true;
      private int hoverTileX = -1;
      private int hoverTileY = -1;
      private Point selectedTowerTile;
      private final Rectangle towerButton;
      private final Rectangle nextWaveButton;
      private final Rectangle upgradeButton;

      GamePanel() {
         int uiX = 640;
         this.towerButton = new Rectangle(uiX + 20, 70, 230, 42);
         this.upgradeButton = new Rectangle(uiX + 20, 120, 230, 42);
         this.nextWaveButton = new Rectangle(uiX + 20, 170, 230, 42);
         this.setPreferredSize(new Dimension(910, 480));
         this.setFocusable(true);
         this.setupInput();
         this.lastTickNanos = System.nanoTime();
         this.timer = new Timer(16, (e) -> this.onTick());
         this.timer.start();
      }

      public void addNotify() {
         super.addNotify();
         this.requestFocusInWindow();
      }

      private void onTick() {
         long now = System.nanoTime();
         double dtMs = (double)(now - this.lastTickNanos) / (double)1000000.0F;
         this.lastTickNanos = now;
         if (dtMs < (double)0.0F || dtMs > (double)100.0F) {
            dtMs = (double)16.0F;
         }

         this.state.update(dtMs);
         this.repaint();
      }

      private void setupInput() {
         InputMap im = this.getInputMap(2);
         ActionMap am = this.getActionMap();
         im.put(KeyStroke.getKeyStroke("SPACE"), "startWave");
         am.put("startWave", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               if (!GamePanel.this.state.isGameOver() && !GamePanel.this.state.isWaveInProgress()) {
                  GamePanel.this.state.startNextWave();
               }

            }
         });
         im.put(KeyStroke.getKeyStroke("U"), "upgradeTower");
         am.put("upgradeTower", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               if (!GamePanel.this.state.isGameOver() && GamePanel.this.selectedTowerTile != null) {
                  GamePanel.this.state.upgradeTowerAt(GamePanel.this.selectedTowerTile.x, GamePanel.this.selectedTowerTile.y);
               }

            }
         });
         im.put(KeyStroke.getKeyStroke("R"), "restart");
         am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               if (GamePanel.this.state.isGameOver()) {
                  GamePanel.this.state.reset();
                  GamePanel.this.buildMode = true;
                  GamePanel.this.selectedTowerTile = null;
               }

            }
         });
         im.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
         am.put("exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
               if (window != null) {
                  window.dispose();
               }

               System.exit(0);
            }
         });
         MouseAdapter mouseHandler = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
               GamePanel.this.updateHoverTile(e.getX(), e.getY());
            }

            public void mouseExited(MouseEvent e) {
               GamePanel.this.hoverTileX = -1;
               GamePanel.this.hoverTileY = -1;
            }

            public void mouseClicked(MouseEvent e) {
               GamePanel.this.handleClick(e.getX(), e.getY());
            }
         };
         this.addMouseMotionListener(mouseHandler);
         this.addMouseListener(mouseHandler);
      }

      private void handleClick(int mx, int my) {
         if (!this.state.isGameOver()) {
            if (this.towerButton.contains(mx, my)) {
               this.buildMode = true;
               this.selectedTowerTile = null;
            } else if (this.upgradeButton.contains(mx, my)) {
               if (this.selectedTowerTile != null) {
                  this.state.upgradeTowerAt(this.selectedTowerTile.x, this.selectedTowerTile.y);
               }

            } else if (this.nextWaveButton.contains(mx, my)) {
               if (!this.state.isWaveInProgress()) {
                  this.state.startNextWave();
               }

            } else {
               int boardWidth = 640;
               if (mx >= 0 && mx < boardWidth && my >= 0 && my < 480) {
                  int tx = mx / 40;
                  int ty = my / 40;
                  Tower clickedTower = this.state.getTowerAt(tx, ty);
                  if (clickedTower != null) {
                     this.selectedTowerTile = new Point(tx, ty);
                     this.buildMode = false;
                  } else {
                     if (this.buildMode) {
                        boolean placed = this.state.placeTower(tx, ty);
                        if (placed) {
                           this.selectedTowerTile = null;
                        }
                     } else {
                        this.selectedTowerTile = null;
                     }

                  }
               }
            }
         }
      }

      private void updateHoverTile(int mx, int my) {
         int boardWidth = 640;
         if (mx >= 0 && mx < boardWidth && my >= 0 && my < 480) {
            this.hoverTileX = mx / 40;
            this.hoverTileY = my / 40;
         } else {
            this.hoverTileX = -1;
            this.hoverTileY = -1;
         }

      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         this.drawBoard(g2);
         this.drawPath(g2);
         this.drawTowers(g2);
         this.drawEnemies(g2);
         this.drawEffects(g2);
         this.drawPlacementPreview(g2);
         this.drawUI(g2);
         if (this.state.isGameOver()) {
            this.drawGameOver(g2);
         }

         g2.dispose();
      }

      private void drawBoard(Graphics2D g2) {
         int boardWidth = 640;
         int boardHeight = 480;
         g2.setColor(BOARD_BG);
         g2.fillRect(0, 0, boardWidth, boardHeight);
         g2.setColor(GRID_COLOR);

         for(int x = 0; x <= 16; ++x) {
            int px = x * 40;
            g2.drawLine(px, 0, px, boardHeight);
         }

         for(int y = 0; y <= 12; ++y) {
            int py = y * 40;
            g2.drawLine(0, py, boardWidth, py);
         }

      }

      private void drawPath(Graphics2D g2) {
         g2.setColor(PATH_COLOR);

         for(Point p : this.state.getPathTiles()) {
            g2.fillRect(p.x * 40, p.y * 40, 40, 40);
         }

         Point spawn = this.state.getSpawnTile();
         Point goal = this.state.getGoalTile();
         g2.setColor(SPAWN_COLOR);
         g2.fillRect(spawn.x * 40 + 6, spawn.y * 40 + 6, 28, 28);
         g2.setColor(GOAL_COLOR);
         g2.fillRect(goal.x * 40 + 6, goal.y * 40 + 6, 28, 28);
      }

      private void drawTowers(Graphics2D g2) {
         for(Tower tower : this.state.getTowers()) {
            Point t = tower.getTile();
            int x = t.x * 40;
            int y = t.y * 40;
            if (this.selectedTowerTile != null && this.selectedTowerTile.x == t.x && this.selectedTowerTile.y == t.y) {
               int range = (int)Math.round(tower.getRangePx());
               int cx = L29Game.Path.tileCenterX(t.x, 40);
               int cy = L29Game.Path.tileCenterY(t.y, 40);
               g2.setColor(new Color(190, 220, 255, 55));
               g2.fillOval(cx - range, cy - range, range * 2, range * 2);
               g2.setColor(new Color(220, 240, 255, 140));
               g2.drawOval(cx - range, cy - range, range * 2, range * 2);
            }

            g2.setColor(TOWER_COLOR);
            g2.fillRect(x + 6, y + 6, 28, 28);
            g2.setColor(Color.BLACK);
            g2.drawRect(x + 6, y + 6, 28, 28);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", 1, 12));
            g2.drawString("L" + tower.getLevel(), x + 10, y + 22);
         }

         if (this.buildMode && this.hoverTileX >= 0 && this.hoverTileY >= 0 && this.state.canPlaceTower(this.hoverTileX, this.hoverTileY)) {
            int cx = L29Game.Path.tileCenterX(this.hoverTileX, 40);
            int cy = L29Game.Path.tileCenterY(this.hoverTileY, 40);
            int r = (int)Math.round((double)120.0F);
            g2.setColor(new Color(120, 180, 255, 90));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(170, 210, 255, 150));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
         }

      }

      private void drawEnemies(Graphics2D g2) {
         for(Enemy enemy : this.state.getEnemies()) {
            int size = enemy.getDrawSize();
            int x = (int)Math.round(enemy.getX()) - size / 2;
            int y = (int)Math.round(enemy.getY()) - size / 2;
            if (enemy.getType() == L29Game.EnemyType.KING_BOSS) {
               g2.setColor(KING_BOSS_COLOR);
            } else if (enemy.getType() == L29Game.EnemyType.MINI_BOSS) {
               g2.setColor(MINI_BOSS_COLOR);
            } else {
               g2.setColor(ENEMY_COLOR);
            }

            g2.fillOval(x, y, size, size);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, size, size);
            int barW = size + (enemy.isBoss() ? 18 : 0);
            int barH = enemy.isBoss() ? 7 : 5;
            int barX = x - (barW - size) / 2;
            int barY = y - 9;
            g2.setColor(new Color(70, 70, 70));
            g2.fillRect(barX, barY, barW, barH);
            double ratio = enemy.getMaxHp() <= 0 ? (double)0.0F : (double)enemy.getHp() / (double)enemy.getMaxHp();
            ratio = Math.max((double)0.0F, Math.min((double)1.0F, ratio));
            g2.setColor(enemy.isBoss() ? new Color(255, 90, 90) : new Color(80, 220, 100));
            g2.fillRect(barX, barY, (int)Math.round((double)barW * ratio), barH);
         }

      }

      private void drawEffects(Graphics2D g2) {
         for(Effect effect : this.state.getEffects()) {
            float alpha = (float)effect.getAlpha();
            g2.setComposite(AlphaComposite.getInstance(3, alpha));
            g2.setColor(new Color(255, 245, 180));
            g2.drawLine((int)Math.round(effect.getX1()), (int)Math.round(effect.getY1()), (int)Math.round(effect.getX2()), (int)Math.round(effect.getY2()));
         }

         g2.setComposite(AlphaComposite.getInstance(3, 1.0F));
      }

      private void drawPlacementPreview(Graphics2D g2) {
         if (this.buildMode && this.hoverTileX >= 0 && this.hoverTileY >= 0 && !this.state.isGameOver()) {
            int x = this.hoverTileX * 40;
            int y = this.hoverTileY * 40;
            boolean canPlace = this.state.canPlaceTower(this.hoverTileX, this.hoverTileY) && this.state.getGold() >= 50;
            g2.setColor(canPlace ? new Color(100, 180, 255, 120) : new Color(230, 70, 70, 120));
            g2.fillRect(x + 4, y + 4, 32, 32);
         }
      }

      private void drawUI(Graphics2D g2) {
         int uiX = 640;
         g2.setColor(UI_BG);
         g2.fillRect(uiX, 0, 270, 480);
         g2.setColor(Color.WHITE);
         g2.setFont(new Font("SansSerif", 1, 18));
         g2.drawString("Tower Defense", uiX + 20, 34);
         g2.setFont(new Font("SansSerif", 0, 15));
         g2.drawString("Gold: " + this.state.getGold(), uiX + 20, 240);
         g2.drawString("Lives: " + this.state.getLives(), uiX + 20, 264);
         g2.drawString("Wave: " + this.state.getWave(), uiX + 20, 288);
         g2.drawString("Kills: " + this.state.getKills(), uiX + 20, 312);
         if (this.state.isWaveInProgress()) {
            g2.drawString("Spawning: " + this.state.getSpawnedThisWave() + "/" + this.state.getEnemiesToSpawn(), uiX + 20, 336);
         } else {
            g2.drawString("Wave Clear", uiX + 20, 336);
         }

         this.drawButton(g2, this.towerButton, "Build Tower ($50)", this.buildMode);
         int upCost = this.selectedTowerTile == null ? 0 : this.state.getUpgradeCostAt(this.selectedTowerTile.x, this.selectedTowerTile.y);
         String upLabel = this.selectedTowerTile == null ? "Select Tower to Upgrade" : "Upgrade Tower ($" + upCost + ")";
         this.drawButton(g2, this.upgradeButton, upLabel, this.selectedTowerTile != null);
         String nextLabel = this.state.isWaveInProgress() ? "Wave Running..." : "Next Wave (Space)";
         this.drawButton(g2, this.nextWaveButton, nextLabel, !this.state.isWaveInProgress());
         Tower selectedTower = this.selectedTowerTile == null ? null : this.state.getTowerAt(this.selectedTowerTile.x, this.selectedTowerTile.y);
         if (selectedTower != null) {
            g2.setFont(new Font("SansSerif", 0, 12));
            g2.setColor(new Color(220, 220, 220));
            g2.drawString("Selected L" + selectedTower.getLevel(), uiX + 20, 372);
            g2.drawString("DMG: " + selectedTower.getDamage(), uiX + 20, 388);
            g2.drawString("RNG: " + (int)Math.round(selectedTower.getRangePx()), uiX + 20, 404);
            g2.drawString("SPD: " + selectedTower.getFireRateLabel(), uiX + 20, 420);
         }

         g2.setFont(new Font("SansSerif", 0, 12));
         g2.setColor(new Color(210, 210, 210));
         g2.drawString("Click tower to select upgrade", uiX + 20, 424);
         g2.drawString("U: Upgrade, R: Restart", uiX + 20, 442);
         g2.drawString("ESC: Exit", uiX + 20, 460);
      }

      private void drawButton(Graphics2D g2, Rectangle r, String label, boolean active) {
         g2.setColor(active ? new Color(80, 130, 200) : new Color(65, 65, 65));
         g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
         g2.setColor(Color.WHITE);
         g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
         FontMetrics fm = g2.getFontMetrics();
         int tx = r.x + (r.width - fm.stringWidth(label)) / 2;
         int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
         g2.drawString(label, tx, ty);
      }

      private void drawGameOver(Graphics2D g2) {
         int boardW = 640;
         int boardH = 480;
         g2.setColor(new Color(0, 0, 0, 165));
         g2.fillRect(0, 0, boardW, boardH);
         g2.setColor(Color.WHITE);
         g2.setFont(new Font("SansSerif", 1, 42));
         this.drawCentered(g2, "Game Over", boardW / 2, boardH / 2 - 30);
         g2.setFont(new Font("SansSerif", 0, 20));
         this.drawCentered(g2, "Wave: " + this.state.getWave() + "   Score: " + this.state.getKills(), boardW / 2, boardH / 2 + 10);
         this.drawCentered(g2, "R: Restart / ESC: Exit", boardW / 2, boardH / 2 + 42);
      }

      private void drawCentered(Graphics2D g2, String text, int cx, int y) {
         FontMetrics fm = g2.getFontMetrics();
         int x = cx - fm.stringWidth(text) / 2;
         g2.drawString(text, x, y);
      }
   }

   static class GameState {
      static final int BOARD_COLS = 16;
      static final int BOARD_ROWS = 12;
      static final int TILE_SIZE = 40;
      static final int UI_WIDTH = 270;
      static final int SCREEN_WIDTH = 910;
      static final int SCREEN_HEIGHT = 480;
      static final int INITIAL_GOLD = 100;
      static final int INITIAL_LIVES = 10;
      static final int TOWER_COST = 50;
      static final int TOWER_BASE_DAMAGE = 20;
      static final double TOWER_BASE_RANGE_PX = (double)120.0F;
      static final double TOWER_BASE_COOLDOWN_MS = (double)550.0F;
      private static final int BASE_ENEMIES_PER_WAVE = 6;
      private static final int ENEMIES_INCREASE_PER_WAVE = 2;
      private static final int BASE_SPAWN_INTERVAL_MS = 820;
      private static final int SPAWN_INTERVAL_REDUCE_PER_WAVE = 32;
      private static final int MIN_SPAWN_INTERVAL_MS = 220;
      private final List pathTiles = L29Game.Path.createDefaultPath();
      private final boolean[][] pathMask = new boolean[16][12];
      private final List enemies;
      private final List towers;
      private final List effects;
      private final List waveQueue;
      private int gold;
      private int lives;
      private int wave;
      private int kills;
      private boolean gameOver;
      private boolean waveInProgress;
      private int enemiesToSpawn;
      private int spawnedThisWave;
      private int spawnIntervalMs;
      private double spawnTimerMs;

      GameState() {
         for(Point p : this.pathTiles) {
            if (this.isInsideBoard(p.x, p.y)) {
               this.pathMask[p.x][p.y] = true;
            }
         }

         this.enemies = new ArrayList();
         this.towers = new ArrayList();
         this.effects = new ArrayList();
         this.waveQueue = new ArrayList();
         this.reset();
      }

      void reset() {
         this.enemies.clear();
         this.towers.clear();
         this.effects.clear();
         this.waveQueue.clear();
         this.gold = 100;
         this.lives = 10;
         this.wave = 0;
         this.kills = 0;
         this.gameOver = false;
         this.waveInProgress = false;
         this.enemiesToSpawn = 0;
         this.spawnedThisWave = 0;
         this.spawnIntervalMs = 820;
         this.spawnTimerMs = (double)0.0F;
      }

      void startNextWave() {
         if (!this.gameOver && !this.waveInProgress) {
            ++this.wave;
            this.waveInProgress = true;
            this.spawnedThisWave = 0;
            this.spawnTimerMs = (double)0.0F;
            this.spawnIntervalMs = Math.max(220, 820 - this.wave * 32);
            this.buildWaveQueue(this.wave);
            this.enemiesToSpawn = this.waveQueue.size();
         }
      }

      private void buildWaveQueue(int waveNumber) {
         this.waveQueue.clear();
         int normalCount = 6 + waveNumber * 2;
         int baseHp = (int)Math.round((double)(40 + waveNumber * 12) + (double)(waveNumber * waveNumber) * 0.55);
         double baseSpeed = (double)58.0F + (double)waveNumber * 3.4 + Math.min((double)40.0F, (double)waveNumber * 1.8);
         int baseReward = 11 + waveNumber * 2;

         for(int i = 0; i < normalCount; ++i) {
            int hp = baseHp + i % 3 * (waveNumber + 4);
            double speed = baseSpeed + (double)(i % 4) * 1.8;
            int reward = baseReward + i % 2;
            this.waveQueue.add(new EnemySpec(hp, speed, reward, L29Game.EnemyType.NORMAL));
         }

         if (waveNumber % 10 == 0) {
            int hp = baseHp * 24;
            double speed = Math.max((double)45.0F, baseSpeed * 0.9);
            int reward = baseReward * 11;
            this.waveQueue.add(new EnemySpec(hp, speed, reward, L29Game.EnemyType.KING_BOSS));
         } else if (waveNumber % 5 == 0) {
            int hp = baseHp * 9;
            double speed = Math.max((double)50.0F, baseSpeed * 0.95);
            int reward = baseReward * 6;
            this.waveQueue.add(new EnemySpec(hp, speed, reward, L29Game.EnemyType.MINI_BOSS));
         }

      }

      void update(double dtMs) {
         if (!this.gameOver) {
            double dtSec = dtMs / (double)1000.0F;
            if (this.waveInProgress) {
               this.handleSpawning(dtMs);
            }

            for(Enemy enemy : this.enemies) {
               enemy.update(dtSec, this.pathTiles, 40);
            }

            Iterator<Enemy> goalIt = this.enemies.iterator();

            while(goalIt.hasNext()) {
               Enemy enemy = (Enemy)goalIt.next();
               if (enemy.hasReachedGoal()) {
                  goalIt.remove();
                  --this.lives;
                  if (this.lives <= 0) {
                     this.lives = 0;
                     this.gameOver = true;
                  }
               }
            }

            for(Tower tower : this.towers) {
               tower.update(dtMs, this.enemies, this.effects, 40);
            }

            Iterator<Enemy> deadIt = this.enemies.iterator();

            while(deadIt.hasNext()) {
               Enemy enemy = (Enemy)deadIt.next();
               if (enemy.isDead()) {
                  deadIt.remove();
                  this.gold += enemy.getRewardGold();
                  ++this.kills;
               }
            }

            Iterator<Effect> effectIt = this.effects.iterator();

            while(effectIt.hasNext()) {
               Effect effect = (Effect)effectIt.next();
               effect.update(dtMs);
               if (!effect.isAlive()) {
                  effectIt.remove();
               }
            }

            if (this.waveInProgress && this.spawnedThisWave >= this.enemiesToSpawn && this.enemies.isEmpty()) {
               this.waveInProgress = false;
            }

         }
      }

      private void handleSpawning(double dtMs) {
         for(this.spawnTimerMs += dtMs; this.spawnedThisWave < this.enemiesToSpawn && this.spawnTimerMs >= (double)this.spawnIntervalMs; ++this.spawnedThisWave) {
            this.spawnTimerMs -= (double)this.spawnIntervalMs;
            EnemySpec spec = (EnemySpec)this.waveQueue.get(this.spawnedThisWave);
            this.enemies.add(new Enemy(this.pathTiles, 40, spec.hp, spec.speed, spec.reward, spec.type));
         }

      }

      boolean canPlaceTower(int tileX, int tileY) {
         if (this.isInsideBoard(tileX, tileY) && !this.pathMask[tileX][tileY]) {
            return this.getTowerAt(tileX, tileY) == null;
         } else {
            return false;
         }
      }

      boolean placeTower(int tileX, int tileY) {
         if (this.canPlaceTower(tileX, tileY) && this.gold >= 50) {
            this.gold -= 50;
            this.towers.add(new Tower(tileX, tileY, 50, (double)120.0F, (double)550.0F, 20));
            return true;
         } else {
            return false;
         }
      }

      Tower getTowerAt(int tileX, int tileY) {
         for(Tower tower : this.towers) {
            Point p = tower.getTile();
            if (p.x == tileX && p.y == tileY) {
               return tower;
            }
         }

         return null;
      }

      int getUpgradeCostAt(int tileX, int tileY) {
         Tower tower = this.getTowerAt(tileX, tileY);
         return tower == null ? 0 : tower.getUpgradeCost();
      }

      boolean upgradeTowerAt(int tileX, int tileY) {
         Tower tower = this.getTowerAt(tileX, tileY);
         if (tower == null) {
            return false;
         } else {
            int cost = tower.getUpgradeCost();
            if (this.gold < cost) {
               return false;
            } else {
               this.gold -= cost;
               tower.upgrade();
               return true;
            }
         }
      }

      boolean isInsideBoard(int tileX, int tileY) {
         return tileX >= 0 && tileX < 16 && tileY >= 0 && tileY < 12;
      }

      List getPathTiles() {
         return this.pathTiles;
      }

      Point getSpawnTile() {
         return (Point)this.pathTiles.get(0);
      }

      Point getGoalTile() {
         return (Point)this.pathTiles.get(this.pathTiles.size() - 1);
      }

      List getEnemies() {
         return Collections.unmodifiableList(this.enemies);
      }

      List getTowers() {
         return Collections.unmodifiableList(this.towers);
      }

      List getEffects() {
         return Collections.unmodifiableList(this.effects);
      }

      int getGold() {
         return this.gold;
      }

      int getLives() {
         return this.lives;
      }

      int getWave() {
         return this.wave;
      }

      int getKills() {
         return this.kills;
      }

      boolean isGameOver() {
         return this.gameOver;
      }

      boolean isWaveInProgress() {
         return this.waveInProgress;
      }

      int getEnemiesToSpawn() {
         return this.enemiesToSpawn;
      }

      int getSpawnedThisWave() {
         return this.spawnedThisWave;
      }
   }

   static class Path {
      private Path() {
      }

      static List createDefaultPath() {
         List<Point> path = new ArrayList();
         addLine(path, 0, 2, 7, 2);
         addLine(path, 7, 3, 7, 8);
         addLine(path, 8, 8, 14, 8);
         addLine(path, 14, 7, 14, 4);
         addLine(path, 15, 4, 15, 4);
         return Collections.unmodifiableList(path);
      }

      private static void addLine(List path, int x1, int y1, int x2, int y2) {
         int dx = Integer.compare(x2, x1);
         int dy = Integer.compare(y2, y1);
         int x = x1;
         int y = y1;
         if (path.isEmpty() || ((Point)path.get(path.size() - 1)).x != x1 || ((Point)path.get(path.size() - 1)).y != y1) {
            path.add(new Point(x1, y1));
         }

         while(x != x2 || y != y2) {
            x += dx;
            y += dy;
            path.add(new Point(x, y));
         }

      }

      static int tileCenterX(int tileX, int tileSize) {
         return tileX * tileSize + tileSize / 2;
      }

      static int tileCenterY(int tileY, int tileSize) {
         return tileY * tileSize + tileSize / 2;
      }
   }

   static enum EnemyType {
      NORMAL,
      MINI_BOSS,
      KING_BOSS;

      // $FF: synthetic method
      private static EnemyType[] $values() {
         return new EnemyType[]{NORMAL, MINI_BOSS, KING_BOSS};
      }
   }

   static class EnemySpec {
      final int hp;
      final double speed;
      final int reward;
      final EnemyType type;

      EnemySpec(int hp, double speed, int reward, EnemyType type) {
         this.hp = Math.max(1, hp);
         this.speed = Math.max((double)1.0F, speed);
         this.reward = Math.max(1, reward);
         this.type = type == null ? L29Game.EnemyType.NORMAL : type;
      }
   }

   static class Enemy {
      private double x;
      private double y;
      private int pathIndex;
      private final int maxHp;
      private int hp;
      private final double speedPixelsPerSec;
      private final int rewardGold;
      private final EnemyType type;
      private boolean reachedGoal;
      private double progress;

      Enemy(List pathTiles, int tileSize, int hp, double speedPixelsPerSec, int rewardGold, EnemyType type) {
         Point spawn = (Point)pathTiles.get(0);
         this.x = (double)L29Game.Path.tileCenterX(spawn.x, tileSize);
         this.y = (double)L29Game.Path.tileCenterY(spawn.y, tileSize);
         this.pathIndex = 1;
         this.maxHp = Math.max(1, hp);
         this.hp = this.maxHp;
         this.speedPixelsPerSec = Math.max((double)1.0F, speedPixelsPerSec);
         this.rewardGold = Math.max(1, rewardGold);
         this.type = type == null ? L29Game.EnemyType.NORMAL : type;
         this.progress = (double)0.0F;
      }

      void update(double dtSec, List pathTiles, int tileSize) {
         if (!this.reachedGoal && this.hp > 0 && this.pathIndex < pathTiles.size()) {
            double move = this.speedPixelsPerSec * dtSec;

            while(move > (double)0.0F && !this.reachedGoal && this.pathIndex < pathTiles.size()) {
               Point t = (Point)pathTiles.get(this.pathIndex);
               double tx = (double)L29Game.Path.tileCenterX(t.x, tileSize);
               double ty = (double)L29Game.Path.tileCenterY(t.y, tileSize);
               double dx = tx - this.x;
               double dy = ty - this.y;
               double dist = Math.hypot(dx, dy);
               if (dist < 1.0E-4) {
                  this.x = tx;
                  this.y = ty;
                  ++this.pathIndex;
                  if (this.pathIndex >= pathTiles.size()) {
                     this.reachedGoal = true;
                  }
               } else if (move >= dist) {
                  this.x = tx;
                  this.y = ty;
                  move -= dist;
                  ++this.pathIndex;
                  if (this.pathIndex >= pathTiles.size()) {
                     this.reachedGoal = true;
                  }
               } else {
                  double ratio = move / dist;
                  this.x += dx * ratio;
                  this.y += dy * ratio;
                  move = (double)0.0F;
               }
            }

            double nodeProgress = (double)Math.min(this.pathIndex, pathTiles.size() - 1);
            if (this.pathIndex < pathTiles.size()) {
               Point t = (Point)pathTiles.get(this.pathIndex);
               double tx = (double)L29Game.Path.tileCenterX(t.x, tileSize);
               double ty = (double)L29Game.Path.tileCenterY(t.y, tileSize);
               double remaining = Math.hypot(tx - this.x, ty - this.y);
               this.progress = nodeProgress - Math.min((double)1.0F, remaining / (double)tileSize);
            } else {
               this.progress = (double)pathTiles.size();
            }

         }
      }

      void takeDamage(int damage) {
         if (damage > 0 && this.hp > 0) {
            this.hp = Math.max(0, this.hp - damage);
         }

      }

      boolean isDead() {
         return this.hp <= 0;
      }

      boolean hasReachedGoal() {
         return this.reachedGoal;
      }

      boolean isBoss() {
         return this.type != L29Game.EnemyType.NORMAL;
      }

      int getDrawSize() {
         if (this.type == L29Game.EnemyType.KING_BOSS) {
            return 36;
         } else {
            return this.type == L29Game.EnemyType.MINI_BOSS ? 31 : 26;
         }
      }

      double getX() {
         return this.x;
      }

      double getY() {
         return this.y;
      }

      int getHp() {
         return this.hp;
      }

      int getMaxHp() {
         return this.maxHp;
      }

      int getRewardGold() {
         return this.rewardGold;
      }

      double getProgress() {
         return this.progress;
      }

      EnemyType getType() {
         return this.type;
      }
   }

   static class Tower {
      private final int tileX;
      private final int tileY;
      private final int baseCost;
      private int level;
      private int damage;
      private double rangePx;
      private double fireCooldownMs;
      private double cooldownMs;

      Tower(int tileX, int tileY, int baseCost, double rangePx, double fireCooldownMs, int damage) {
         this.tileX = tileX;
         this.tileY = tileY;
         this.baseCost = baseCost;
         this.level = 1;
         this.rangePx = rangePx;
         this.fireCooldownMs = fireCooldownMs;
         this.damage = damage;
         this.cooldownMs = (double)0.0F;
      }

      void update(double dtMs, List enemies, List effects, int tileSize) {
         this.cooldownMs -= dtMs;
         if (!(this.cooldownMs > (double)0.0F)) {
            double cx = (double)L29Game.Path.tileCenterX(this.tileX, tileSize);
            double cy = (double)L29Game.Path.tileCenterY(this.tileY, tileSize);
            Enemy best = null;
            double bestProgress = (double)-1.0F;

            for(Enemy enemy : enemies) {
               if (!enemy.isDead() && !enemy.hasReachedGoal()) {
                  double dist = Math.hypot(enemy.getX() - cx, enemy.getY() - cy);
                  if (dist <= this.rangePx && enemy.getProgress() > bestProgress) {
                     bestProgress = enemy.getProgress();
                     best = enemy;
                  }
               }
            }

            if (best != null) {
               best.takeDamage(this.damage);
               effects.add(new Effect(cx, cy, best.getX(), best.getY(), (double)110.0F));
               this.cooldownMs = this.fireCooldownMs;
            }

         }
      }

      void upgrade() {
         ++this.level;
         this.damage = (int)Math.round((double)this.damage * 1.35 + (double)4.0F);
         this.rangePx = Math.min((double)220.0F, this.rangePx + (double)12.0F);
         this.fireCooldownMs = Math.max((double)180.0F, this.fireCooldownMs * 0.92);
      }

      int getUpgradeCost() {
         return this.baseCost + this.level * 35;
      }

      String getFireRateLabel() {
         double shotsPerSec = (double)1000.0F / Math.max((double)1.0F, this.fireCooldownMs);
         return String.format("%.2f/s", shotsPerSec);
      }

      Point getTile() {
         return new Point(this.tileX, this.tileY);
      }

      int getLevel() {
         return this.level;
      }

      int getDamage() {
         return this.damage;
      }

      double getRangePx() {
         return this.rangePx;
      }
   }

   static class Effect {
      private final double x1;
      private final double y1;
      private final double x2;
      private final double y2;
      private double ttlMs;

      Effect(double x1, double y1, double x2, double y2, double ttlMs) {
         this.x1 = x1;
         this.y1 = y1;
         this.x2 = x2;
         this.y2 = y2;
         this.ttlMs = Math.max((double)0.0F, ttlMs);
      }

      void update(double dtMs) {
         this.ttlMs -= dtMs;
      }

      boolean isAlive() {
         return this.ttlMs > (double)0.0F;
      }

      double getAlpha() {
         return Math.min((double)1.0F, Math.max((double)0.0F, this.ttlMs / (double)110.0F));
      }

      double getX1() {
         return this.x1;
      }

      double getY1() {
         return this.y1;
      }

      double getX2() {
         return this.x2;
      }

      double getY2() {
         return this.y2;
      }
   }
}

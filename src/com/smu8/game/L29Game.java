package com.smu8.game;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class L29Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tower Defense");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GamePanel panel = new GamePanel();
            frame.setContentPane(panel);

            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
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

        private final GameState state;
        private final Timer timer;

        private long lastTickNanos;
        private boolean buildMode;
        private int hoverTileX = -1;
        private int hoverTileY = -1;
        private Point selectedTowerTile;

        private final Rectangle towerButton;
        private final Rectangle nextWaveButton;
        private final Rectangle upgradeButton;

        GamePanel() {
            this.state = new GameState();
            this.buildMode = true;

            int uiX = GameState.BOARD_COLS * GameState.TILE_SIZE;
            this.towerButton = new Rectangle(uiX + 20, 70, GameState.UI_WIDTH - 40, 42);
            this.upgradeButton = new Rectangle(uiX + 20, 120, GameState.UI_WIDTH - 40, 42);
            this.nextWaveButton = new Rectangle(uiX + 20, 170, GameState.UI_WIDTH - 40, 42);

            setPreferredSize(new Dimension(GameState.SCREEN_WIDTH, GameState.SCREEN_HEIGHT));
            setFocusable(true);

            setupInput();

            this.lastTickNanos = System.nanoTime();
            this.timer = new Timer(FRAME_MS, e -> onTick());
            this.timer.start();
        }

        @Override
        public void addNotify() {
            super.addNotify();
            requestFocusInWindow();
        }

        private void onTick() {
            long now = System.nanoTime();
            double dtMs = (now - lastTickNanos) / 1_000_000.0;
            lastTickNanos = now;

            if (dtMs < 0 || dtMs > 100) {
                dtMs = FRAME_MS;
            }

            state.update(dtMs);
            repaint();
        }

        private void setupInput() {
            InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();

            im.put(KeyStroke.getKeyStroke("SPACE"), "startWave");
            am.put("startWave", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!state.isGameOver() && !state.isWaveInProgress()) {
                        state.startNextWave();
                    }
                }
            });

            im.put(KeyStroke.getKeyStroke("U"), "upgradeTower");
            am.put("upgradeTower", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!state.isGameOver() && selectedTowerTile != null) {
                        state.upgradeTowerAt(selectedTowerTile.x, selectedTowerTile.y);
                    }
                }
            });

            im.put(KeyStroke.getKeyStroke("R"), "restart");
            am.put("restart", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (state.isGameOver()) {
                        state.reset();
                        buildMode = true;
                        selectedTowerTile = null;
                    }
                }
            });

            im.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
            am.put("exit", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
                    if (window != null) {
                        window.dispose();
                    }
                    System.exit(0);
                }
            });

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    updateHoverTile(e.getX(), e.getY());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverTileX = -1;
                    hoverTileY = -1;
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick(e.getX(), e.getY());
                }
            };

            addMouseMotionListener(mouseHandler);
            addMouseListener(mouseHandler);
        }

        private void handleClick(int mx, int my) {
            if (state.isGameOver()) {
                return;
            }

            if (towerButton.contains(mx, my)) {
                buildMode = true;
                selectedTowerTile = null;
                return;
            }

            if (upgradeButton.contains(mx, my)) {
                if (selectedTowerTile != null) {
                    state.upgradeTowerAt(selectedTowerTile.x, selectedTowerTile.y);
                }
                return;
            }

            if (nextWaveButton.contains(mx, my)) {
                if (!state.isWaveInProgress()) {
                    state.startNextWave();
                }
                return;
            }

            int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
            if (mx < 0 || mx >= boardWidth || my < 0 || my >= GameState.SCREEN_HEIGHT) {
                return;
            }

            int tx = mx / GameState.TILE_SIZE;
            int ty = my / GameState.TILE_SIZE;

            Tower clickedTower = state.getTowerAt(tx, ty);
            if (clickedTower != null) {
                selectedTowerTile = new Point(tx, ty);
                buildMode = false;
                return;
            }

            if (buildMode) {
                boolean placed = state.placeTower(tx, ty);
                if (placed) {
                    selectedTowerTile = null;
                }
            } else {
                selectedTowerTile = null;
            }
        }

        private void updateHoverTile(int mx, int my) {
            int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
            if (mx >= 0 && mx < boardWidth && my >= 0 && my < GameState.SCREEN_HEIGHT) {
                hoverTileX = mx / GameState.TILE_SIZE;
                hoverTileY = my / GameState.TILE_SIZE;
            } else {
                hoverTileX = -1;
                hoverTileY = -1;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawBoard(g2);
            drawPath(g2);
            drawTowers(g2);
            drawEnemies(g2);
            drawEffects(g2);
            drawPlacementPreview(g2);
            drawUI(g2);

            if (state.isGameOver()) {
                drawGameOver(g2);
            }

            g2.dispose();
        }
        private void drawBoard(Graphics2D g2) {
            int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
            int boardHeight = GameState.BOARD_ROWS * GameState.TILE_SIZE;

            g2.setColor(BOARD_BG);
            g2.fillRect(0, 0, boardWidth, boardHeight);

            g2.setColor(GRID_COLOR);
            for (int x = 0; x <= GameState.BOARD_COLS; x++) {
                int px = x * GameState.TILE_SIZE;
                g2.drawLine(px, 0, px, boardHeight);
            }
            for (int y = 0; y <= GameState.BOARD_ROWS; y++) {
                int py = y * GameState.TILE_SIZE;
                g2.drawLine(0, py, boardWidth, py);
            }
        }

        private void drawPath(Graphics2D g2) {
            g2.setColor(PATH_COLOR);
            for (Point p : state.getPathTiles()) {
                g2.fillRect(p.x * GameState.TILE_SIZE, p.y * GameState.TILE_SIZE, GameState.TILE_SIZE, GameState.TILE_SIZE);
            }

            Point spawn = state.getSpawnTile();
            Point goal = state.getGoalTile();

            g2.setColor(SPAWN_COLOR);
            g2.fillRect(spawn.x * GameState.TILE_SIZE + 6, spawn.y * GameState.TILE_SIZE + 6,
                    GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);

            g2.setColor(GOAL_COLOR);
            g2.fillRect(goal.x * GameState.TILE_SIZE + 6, goal.y * GameState.TILE_SIZE + 6,
                    GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);
        }

        private void drawTowers(Graphics2D g2) {
            for (Tower tower : state.getTowers()) {
                Point t = tower.getTile();
                int x = t.x * GameState.TILE_SIZE;
                int y = t.y * GameState.TILE_SIZE;

                if (selectedTowerTile != null && selectedTowerTile.x == t.x && selectedTowerTile.y == t.y) {
                    int range = (int) Math.round(tower.getRangePx());
                    int cx = Path.tileCenterX(t.x, GameState.TILE_SIZE);
                    int cy = Path.tileCenterY(t.y, GameState.TILE_SIZE);
                    g2.setColor(new Color(190, 220, 255, 55));
                    g2.fillOval(cx - range, cy - range, range * 2, range * 2);
                    g2.setColor(new Color(220, 240, 255, 140));
                    g2.drawOval(cx - range, cy - range, range * 2, range * 2);
                }

                g2.setColor(TOWER_COLOR);
                g2.fillRect(x + 6, y + 6, GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);
                g2.setColor(Color.BLACK);
                g2.drawRect(x + 6, y + 6, GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("L" + tower.getLevel(), x + 10, y + 22);
            }

            if (buildMode && hoverTileX >= 0 && hoverTileY >= 0 && state.canPlaceTower(hoverTileX, hoverTileY)) {
                int cx = Path.tileCenterX(hoverTileX, GameState.TILE_SIZE);
                int cy = Path.tileCenterY(hoverTileY, GameState.TILE_SIZE);
                int r = (int) Math.round(GameState.TOWER_BASE_RANGE_PX);

                g2.setColor(new Color(120, 180, 255, 90));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(170, 210, 255, 150));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
        }

        private void drawEnemies(Graphics2D g2) {
            for (Enemy enemy : state.getEnemies()) {
                int size = enemy.getDrawSize();
                int x = (int) Math.round(enemy.getX()) - size / 2;
                int y = (int) Math.round(enemy.getY()) - size / 2;

                if (enemy.getType() == EnemyType.KING_BOSS) {
                    g2.setColor(KING_BOSS_COLOR);
                } else if (enemy.getType() == EnemyType.MINI_BOSS) {
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

                double ratio = enemy.getMaxHp() <= 0 ? 0 : (enemy.getHp() / (double) enemy.getMaxHp());
                ratio = Math.max(0.0, Math.min(1.0, ratio));
                g2.setColor(enemy.isBoss() ? new Color(255, 90, 90) : new Color(80, 220, 100));
                g2.fillRect(barX, barY, (int) Math.round(barW * ratio), barH);
            }
        }

        private void drawEffects(Graphics2D g2) {
            for (Effect effect : state.getEffects()) {
                float alpha = (float) effect.getAlpha();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(new Color(255, 245, 180));
                g2.drawLine((int) Math.round(effect.getX1()), (int) Math.round(effect.getY1()),
                        (int) Math.round(effect.getX2()), (int) Math.round(effect.getY2()));
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        private void drawPlacementPreview(Graphics2D g2) {
            if (!buildMode || hoverTileX < 0 || hoverTileY < 0 || state.isGameOver()) {
                return;
            }

            int x = hoverTileX * GameState.TILE_SIZE;
            int y = hoverTileY * GameState.TILE_SIZE;
            boolean canPlace = state.canPlaceTower(hoverTileX, hoverTileY) && state.getGold() >= GameState.TOWER_COST;
            g2.setColor(canPlace ? new Color(100, 180, 255, 120) : new Color(230, 70, 70, 120));
            g2.fillRect(x + 4, y + 4, GameState.TILE_SIZE - 8, GameState.TILE_SIZE - 8);
        }

        private void drawUI(Graphics2D g2) {
            int uiX = GameState.BOARD_COLS * GameState.TILE_SIZE;

            g2.setColor(UI_BG);
            g2.fillRect(uiX, 0, GameState.UI_WIDTH, GameState.SCREEN_HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Tower Defense", uiX + 20, 34);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g2.drawString("Gold: " + state.getGold(), uiX + 20, 240);
            g2.drawString("Lives: " + state.getLives(), uiX + 20, 264);
            g2.drawString("Wave: " + state.getWave(), uiX + 20, 288);
            g2.drawString("Kills: " + state.getKills(), uiX + 20, 312);

            if (state.isWaveInProgress()) {
                g2.drawString("Spawning: " + state.getSpawnedThisWave() + "/" + state.getEnemiesToSpawn(), uiX + 20, 336);
            } else {
                g2.drawString("Wave Clear", uiX + 20, 336);
            }

            drawButton(g2, towerButton, "Build Tower ($" + GameState.TOWER_COST + ")", buildMode);

            int upCost = selectedTowerTile == null ? 0 : state.getUpgradeCostAt(selectedTowerTile.x, selectedTowerTile.y);
            String upLabel = selectedTowerTile == null ? "Select Tower to Upgrade" : "Upgrade Tower ($" + upCost + ")";
            drawButton(g2, upgradeButton, upLabel, selectedTowerTile != null);

            String nextLabel = state.isWaveInProgress() ? "Wave Running..." : "Next Wave (Space)";
            drawButton(g2, nextWaveButton, nextLabel, !state.isWaveInProgress());

            Tower selectedTower = selectedTowerTile == null ? null : state.getTowerAt(selectedTowerTile.x, selectedTowerTile.y);
            if (selectedTower != null) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(220, 220, 220));
                g2.drawString("Selected L" + selectedTower.getLevel(), uiX + 20, 372);
                g2.drawString("DMG: " + selectedTower.getDamage(), uiX + 20, 388);
                g2.drawString("RNG: " + (int) Math.round(selectedTower.getRangePx()), uiX + 20, 404);
                g2.drawString("SPD: " + selectedTower.getFireRateLabel(), uiX + 20, 420);
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(210, 210, 210));
            g2.drawString("Click tower to select upgrade", uiX + 20, GameState.SCREEN_HEIGHT - 56);
            g2.drawString("U: Upgrade, R: Restart", uiX + 20, GameState.SCREEN_HEIGHT - 38);
            g2.drawString("ESC: Exit", uiX + 20, GameState.SCREEN_HEIGHT - 20);
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
            int boardW = GameState.BOARD_COLS * GameState.TILE_SIZE;
            int boardH = GameState.BOARD_ROWS * GameState.TILE_SIZE;

            g2.setColor(new Color(0, 0, 0, 165));
            g2.fillRect(0, 0, boardW, boardH);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 42));
            drawCentered(g2, "Game Over", boardW / 2, boardH / 2 - 30);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
            drawCentered(g2, "Wave: " + state.getWave() + "   Score: " + state.getKills(), boardW / 2, boardH / 2 + 10);
            drawCentered(g2, "R: Restart / ESC: Exit", boardW / 2, boardH / 2 + 42);
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
        static final int SCREEN_WIDTH = BOARD_COLS * TILE_SIZE + UI_WIDTH;
        static final int SCREEN_HEIGHT = BOARD_ROWS * TILE_SIZE;

        static final int INITIAL_GOLD = 100;
        static final int INITIAL_LIVES = 10;

        static final int TOWER_COST = 50;
        static final int TOWER_BASE_DAMAGE = 20;
        static final double TOWER_BASE_RANGE_PX = 120.0;
        static final double TOWER_BASE_COOLDOWN_MS = 550.0;

        private static final int BASE_ENEMIES_PER_WAVE = 6;
        private static final int ENEMIES_INCREASE_PER_WAVE = 2;
        private static final int BASE_SPAWN_INTERVAL_MS = 820;
        private static final int SPAWN_INTERVAL_REDUCE_PER_WAVE = 32;
        private static final int MIN_SPAWN_INTERVAL_MS = 220;

        private final List<Point> pathTiles;
        private final boolean[][] pathMask;
        private final List<Enemy> enemies;
        private final List<Tower> towers;
        private final List<Effect> effects;

        private final List<EnemySpec> waveQueue;

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
            this.pathTiles = Path.createDefaultPath();
            this.pathMask = new boolean[BOARD_COLS][BOARD_ROWS];
            for (Point p : pathTiles) {
                if (isInsideBoard(p.x, p.y)) {
                    pathMask[p.x][p.y] = true;
                }
            }
            this.enemies = new ArrayList<>();
            this.towers = new ArrayList<>();
            this.effects = new ArrayList<>();
            this.waveQueue = new ArrayList<>();
            reset();
        }

        void reset() {
            enemies.clear();
            towers.clear();
            effects.clear();
            waveQueue.clear();

            gold = INITIAL_GOLD;
            lives = INITIAL_LIVES;
            wave = 0;
            kills = 0;

            gameOver = false;
            waveInProgress = false;
            enemiesToSpawn = 0;
            spawnedThisWave = 0;
            spawnIntervalMs = BASE_SPAWN_INTERVAL_MS;
            spawnTimerMs = 0;
        }

        void startNextWave() {
            if (gameOver || waveInProgress) {
                return;
            }

            wave++;
            waveInProgress = true;
            spawnedThisWave = 0;
            spawnTimerMs = 0;
            spawnIntervalMs = Math.max(MIN_SPAWN_INTERVAL_MS, BASE_SPAWN_INTERVAL_MS - wave * SPAWN_INTERVAL_REDUCE_PER_WAVE);

            buildWaveQueue(wave);
            enemiesToSpawn = waveQueue.size();
        }

        private void buildWaveQueue(int waveNumber) {
            waveQueue.clear();

            int normalCount = BASE_ENEMIES_PER_WAVE + waveNumber * ENEMIES_INCREASE_PER_WAVE;
            int baseHp = (int) Math.round(40 + waveNumber * 12 + waveNumber * waveNumber * 0.55);
            double baseSpeed = 58 + waveNumber * 3.4 + Math.min(40, waveNumber * 1.8);
            int baseReward = 11 + waveNumber * 2;

            for (int i = 0; i < normalCount; i++) {
                int hp = baseHp + (i % 3) * (waveNumber + 4);
                double speed = baseSpeed + (i % 4) * 1.8;
                int reward = baseReward + (i % 2);
                waveQueue.add(new EnemySpec(hp, speed, reward, EnemyType.NORMAL));
            }

            if (waveNumber % 10 == 0) {
                int hp = baseHp * 24;
                double speed = Math.max(45, baseSpeed * 0.9);
                int reward = baseReward * 11;
                waveQueue.add(new EnemySpec(hp, speed, reward, EnemyType.KING_BOSS));
            } else if (waveNumber % 5 == 0) {
                int hp = baseHp * 9;
                double speed = Math.max(50, baseSpeed * 0.95);
                int reward = baseReward * 6;
                waveQueue.add(new EnemySpec(hp, speed, reward, EnemyType.MINI_BOSS));
            }
        }

        void update(double dtMs) {
            if (gameOver) {
                return;
            }

            double dtSec = dtMs / 1000.0;

            if (waveInProgress) {
                handleSpawning(dtMs);
            }

            for (Enemy enemy : enemies) {
                enemy.update(dtSec, pathTiles, TILE_SIZE);
            }

            Iterator<Enemy> goalIt = enemies.iterator();
            while (goalIt.hasNext()) {
                Enemy enemy = goalIt.next();
                if (enemy.hasReachedGoal()) {
                    goalIt.remove();
                    lives--;
                    if (lives <= 0) {
                        lives = 0;
                        gameOver = true;
                    }
                }
            }

            for (Tower tower : towers) {
                tower.update(dtMs, enemies, effects, TILE_SIZE);
            }

            Iterator<Enemy> deadIt = enemies.iterator();
            while (deadIt.hasNext()) {
                Enemy enemy = deadIt.next();
                if (enemy.isDead()) {
                    deadIt.remove();
                    gold += enemy.getRewardGold();
                    kills++;
                }
            }

            Iterator<Effect> effectIt = effects.iterator();
            while (effectIt.hasNext()) {
                Effect effect = effectIt.next();
                effect.update(dtMs);
                if (!effect.isAlive()) {
                    effectIt.remove();
                }
            }

            if (waveInProgress && spawnedThisWave >= enemiesToSpawn && enemies.isEmpty()) {
                waveInProgress = false;
            }
        }

        private void handleSpawning(double dtMs) {
            spawnTimerMs += dtMs;
            while (spawnedThisWave < enemiesToSpawn && spawnTimerMs >= spawnIntervalMs) {
                spawnTimerMs -= spawnIntervalMs;
                EnemySpec spec = waveQueue.get(spawnedThisWave);
                enemies.add(new Enemy(pathTiles, TILE_SIZE, spec.hp, spec.speed, spec.reward, spec.type));
                spawnedThisWave++;
            }
        }

        boolean canPlaceTower(int tileX, int tileY) {
            if (!isInsideBoard(tileX, tileY) || pathMask[tileX][tileY]) {
                return false;
            }
            return getTowerAt(tileX, tileY) == null;
        }

        boolean placeTower(int tileX, int tileY) {
            if (!canPlaceTower(tileX, tileY) || gold < TOWER_COST) {
                return false;
            }
            gold -= TOWER_COST;
            towers.add(new Tower(tileX, tileY, TOWER_COST, TOWER_BASE_RANGE_PX, TOWER_BASE_COOLDOWN_MS, TOWER_BASE_DAMAGE));
            return true;
        }

        Tower getTowerAt(int tileX, int tileY) {
            for (Tower tower : towers) {
                Point p = tower.getTile();
                if (p.x == tileX && p.y == tileY) {
                    return tower;
                }
            }
            return null;
        }

        int getUpgradeCostAt(int tileX, int tileY) {
            Tower tower = getTowerAt(tileX, tileY);
            return tower == null ? 0 : tower.getUpgradeCost();
        }

        boolean upgradeTowerAt(int tileX, int tileY) {
            Tower tower = getTowerAt(tileX, tileY);
            if (tower == null) {
                return false;
            }
            int cost = tower.getUpgradeCost();
            if (gold < cost) {
                return false;
            }
            gold -= cost;
            tower.upgrade();
            return true;
        }

        boolean isInsideBoard(int tileX, int tileY) {
            return tileX >= 0 && tileX < BOARD_COLS && tileY >= 0 && tileY < BOARD_ROWS;
        }

        List<Point> getPathTiles() {
            return pathTiles;
        }

        Point getSpawnTile() {
            return pathTiles.get(0);
        }

        Point getGoalTile() {
            return pathTiles.get(pathTiles.size() - 1);
        }

        List<Enemy> getEnemies() {
            return Collections.unmodifiableList(enemies);
        }

        List<Tower> getTowers() {
            return Collections.unmodifiableList(towers);
        }

        List<Effect> getEffects() {
            return Collections.unmodifiableList(effects);
        }

        int getGold() {
            return gold;
        }

        int getLives() {
            return lives;
        }

        int getWave() {
            return wave;
        }

        int getKills() {
            return kills;
        }

        boolean isGameOver() {
            return gameOver;
        }

        boolean isWaveInProgress() {
            return waveInProgress;
        }

        int getEnemiesToSpawn() {
            return enemiesToSpawn;
        }

        int getSpawnedThisWave() {
            return spawnedThisWave;
        }
    }

    static class Path {
        private Path() {
        }

        static List<Point> createDefaultPath() {
            List<Point> path = new ArrayList<>();
            addLine(path, 0, 2, 7, 2);
            addLine(path, 7, 3, 7, 8);
            addLine(path, 8, 8, 14, 8);
            addLine(path, 14, 7, 14, 4);
            addLine(path, 15, 4, 15, 4);
            return Collections.unmodifiableList(path);
        }

        private static void addLine(List<Point> path, int x1, int y1, int x2, int y2) {
            int dx = Integer.compare(x2, x1);
            int dy = Integer.compare(y2, y1);
            int x = x1;
            int y = y1;

            if (path.isEmpty() || path.get(path.size() - 1).x != x || path.get(path.size() - 1).y != y) {
                path.add(new Point(x, y));
            }

            while (x != x2 || y != y2) {
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

    enum EnemyType {
        NORMAL,
        MINI_BOSS,
        KING_BOSS
    }

    static class EnemySpec {
        final int hp;
        final double speed;
        final int reward;
        final EnemyType type;

        EnemySpec(int hp, double speed, int reward, EnemyType type) {
            this.hp = Math.max(1, hp);
            this.speed = Math.max(1.0, speed);
            this.reward = Math.max(1, reward);
            this.type = type == null ? EnemyType.NORMAL : type;
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

        Enemy(List<Point> pathTiles, int tileSize, int hp, double speedPixelsPerSec, int rewardGold, EnemyType type) {
            Point spawn = pathTiles.get(0);
            this.x = Path.tileCenterX(spawn.x, tileSize);
            this.y = Path.tileCenterY(spawn.y, tileSize);
            this.pathIndex = 1;
            this.maxHp = Math.max(1, hp);
            this.hp = this.maxHp;
            this.speedPixelsPerSec = Math.max(1.0, speedPixelsPerSec);
            this.rewardGold = Math.max(1, rewardGold);
            this.type = type == null ? EnemyType.NORMAL : type;
            this.progress = 0.0;
        }

        void update(double dtSec, List<Point> pathTiles, int tileSize) {
            if (reachedGoal || hp <= 0 || pathIndex >= pathTiles.size()) {
                return;
            }

            double move = speedPixelsPerSec * dtSec;
            while (move > 0 && !reachedGoal && pathIndex < pathTiles.size()) {
                Point t = pathTiles.get(pathIndex);
                double tx = Path.tileCenterX(t.x, tileSize);
                double ty = Path.tileCenterY(t.y, tileSize);
                double dx = tx - x;
                double dy = ty - y;
                double dist = Math.hypot(dx, dy);

                if (dist < 0.0001) {
                    x = tx;
                    y = ty;
                    pathIndex++;
                    if (pathIndex >= pathTiles.size()) {
                        reachedGoal = true;
                    }
                    continue;
                }

                if (move >= dist) {
                    x = tx;
                    y = ty;
                    move -= dist;
                    pathIndex++;
                    if (pathIndex >= pathTiles.size()) {
                        reachedGoal = true;
                    }
                } else {
                    double ratio = move / dist;
                    x += dx * ratio;
                    y += dy * ratio;
                    move = 0;
                }
            }

            double nodeProgress = Math.min(pathIndex, pathTiles.size() - 1);
            if (pathIndex < pathTiles.size()) {
                Point t = pathTiles.get(pathIndex);
                double tx = Path.tileCenterX(t.x, tileSize);
                double ty = Path.tileCenterY(t.y, tileSize);
                double remaining = Math.hypot(tx - x, ty - y);
                progress = nodeProgress - Math.min(1.0, remaining / tileSize);
            } else {
                progress = pathTiles.size();
            }
        }

        void takeDamage(int damage) {
            if (damage > 0 && hp > 0) {
                hp = Math.max(0, hp - damage);
            }
        }

        boolean isDead() {
            return hp <= 0;
        }

        boolean hasReachedGoal() {
            return reachedGoal;
        }

        boolean isBoss() {
            return type != EnemyType.NORMAL;
        }

        int getDrawSize() {
            if (type == EnemyType.KING_BOSS) {
                return GameState.TILE_SIZE - 4;
            }
            if (type == EnemyType.MINI_BOSS) {
                return GameState.TILE_SIZE - 9;
            }
            return GameState.TILE_SIZE - 14;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        int getHp() {
            return hp;
        }

        int getMaxHp() {
            return maxHp;
        }

        int getRewardGold() {
            return rewardGold;
        }

        double getProgress() {
            return progress;
        }

        EnemyType getType() {
            return type;
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
            this.cooldownMs = 0;
        }

        void update(double dtMs, List<Enemy> enemies, List<Effect> effects, int tileSize) {
            cooldownMs -= dtMs;
            if (cooldownMs > 0) {
                return;
            }

            double cx = Path.tileCenterX(tileX, tileSize);
            double cy = Path.tileCenterY(tileY, tileSize);
            Enemy best = null;
            double bestProgress = -1;

            for (Enemy enemy : enemies) {
                if (enemy.isDead() || enemy.hasReachedGoal()) {
                    continue;
                }
                double dist = Math.hypot(enemy.getX() - cx, enemy.getY() - cy);
                if (dist <= rangePx && enemy.getProgress() > bestProgress) {
                    bestProgress = enemy.getProgress();
                    best = enemy;
                }
            }

            if (best != null) {
                best.takeDamage(damage);
                effects.add(new Effect(cx, cy, best.getX(), best.getY(), 110.0));
                cooldownMs = fireCooldownMs;
            }
        }

        void upgrade() {
            level++;
            damage = (int) Math.round(damage * 1.35 + 4);
            rangePx = Math.min(220.0, rangePx + 12.0);
            fireCooldownMs = Math.max(180.0, fireCooldownMs * 0.92);
        }

        int getUpgradeCost() {
            return baseCost + level * 35;
        }

        String getFireRateLabel() {
            double shotsPerSec = 1000.0 / Math.max(1.0, fireCooldownMs);
            return String.format("%.2f/s", shotsPerSec);
        }

        Point getTile() {
            return new Point(tileX, tileY);
        }

        int getLevel() {
            return level;
        }

        int getDamage() {
            return damage;
        }

        double getRangePx() {
            return rangePx;
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
            this.ttlMs = Math.max(0, ttlMs);
        }

        void update(double dtMs) {
            ttlMs -= dtMs;
        }

        boolean isAlive() {
            return ttlMs > 0;
        }

        double getAlpha() {
            return Math.min(1.0, Math.max(0.0, ttlMs / 110.0));
        }

        double getX1() {
            return x1;
        }

        double getY1() {
            return y1;
        }

        double getX2() {
            return x2;
        }

        double getY2() {
            return y2;
        }
    }
}
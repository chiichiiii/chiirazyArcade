import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GameState {
    public static final int BOARD_COLS = 16;
    public static final int BOARD_ROWS = 12;
    public static final int TILE_SIZE = 40;

    public static final int UI_WIDTH = 220;
    public static final int SCREEN_WIDTH = BOARD_COLS * TILE_SIZE + UI_WIDTH;
    public static final int SCREEN_HEIGHT = BOARD_ROWS * TILE_SIZE;

    public static final int INITIAL_GOLD = 100;
    public static final int INITIAL_LIVES = 10;

    public static final int TOWER_COST = 50;
    public static final int TOWER_DAMAGE = 20;
    public static final double TOWER_RANGE_PX = 120.0;
    public static final double TOWER_COOLDOWN_MS = 550.0;

    private static final int BASE_ENEMIES_PER_WAVE = 6;
    private static final int ENEMIES_INCREASE_PER_WAVE = 2;
    private static final int BASE_SPAWN_INTERVAL_MS = 850;
    private static final int SPAWN_INTERVAL_REDUCE_PER_WAVE = 40;
    private static final int MIN_SPAWN_INTERVAL_MS = 250;

    private final List<Point> pathTiles;
    private final boolean[][] pathMask;

    private final List<Enemy> enemies;
    private final List<Tower> towers;
    private final List<Effect> effects;

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

    public GameState() {
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

        reset();
    }

    public void reset() {
        enemies.clear();
        towers.clear();
        effects.clear();

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

    public void startNextWave() {
        if (gameOver || waveInProgress) {
            return;
        }

        wave++;
        waveInProgress = true;
        enemiesToSpawn = BASE_ENEMIES_PER_WAVE + wave * ENEMIES_INCREASE_PER_WAVE;
        spawnedThisWave = 0;
        spawnIntervalMs = Math.max(MIN_SPAWN_INTERVAL_MS,
                BASE_SPAWN_INTERVAL_MS - wave * SPAWN_INTERVAL_REDUCE_PER_WAVE);
        spawnTimerMs = 0;
    }

    public void update(double dtMs) {
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

        Iterator<Enemy> enemyGoalIt = enemies.iterator();
        while (enemyGoalIt.hasNext()) {
            Enemy enemy = enemyGoalIt.next();
            if (enemy.hasReachedGoal()) {
                enemyGoalIt.remove();
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
            enemies.add(createEnemyForWave(wave));
            spawnedThisWave++;
        }
    }

    private Enemy createEnemyForWave(int waveNumber) {
        int hp = 45 + waveNumber * 14;
        double speed = 65.0 + waveNumber * 4.0;
        int reward = 12 + waveNumber * 2;
        return new Enemy(pathTiles, TILE_SIZE, hp, speed, reward);
    }

    public boolean canPlaceTower(int tileX, int tileY) {
        if (!isInsideBoard(tileX, tileY)) {
            return false;
        }
        if (pathMask[tileX][tileY]) {
            return false;
        }
        for (Tower tower : towers) {
            Point t = tower.getTile();
            if (t.x == tileX && t.y == tileY) {
                return false;
            }
        }
        return true;
    }

    public boolean placeTower(int tileX, int tileY) {
        if (!canPlaceTower(tileX, tileY)) {
            return false;
        }
        if (gold < TOWER_COST) {
            return false;
        }

        gold -= TOWER_COST;
        towers.add(new Tower(tileX, tileY, TOWER_COST, TOWER_RANGE_PX, TOWER_COOLDOWN_MS, TOWER_DAMAGE));
        return true;
    }

    public boolean isInsideBoard(int tileX, int tileY) {
        return tileX >= 0 && tileX < BOARD_COLS && tileY >= 0 && tileY < BOARD_ROWS;
    }

    public List<Point> getPathTiles() {
        return pathTiles;
    }

    public Point getSpawnTile() {
        return pathTiles.get(0);
    }

    public Point getGoalTile() {
        return pathTiles.get(pathTiles.size() - 1);
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public List<Tower> getTowers() {
        return Collections.unmodifiableList(towers);
    }

    public List<Effect> getEffects() {
        return Collections.unmodifiableList(effects);
    }

    public int getGold() {
        return gold;
    }

    public int getLives() {
        return lives;
    }

    public int getWave() {
        return wave;
    }

    public int getKills() {
        return kills;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public int getEnemiesToSpawn() {
        return enemiesToSpawn;
    }

    public int getSpawnedThisWave() {
        return spawnedThisWave;
    }
}
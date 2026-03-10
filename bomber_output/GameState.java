import java.awt.Color;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameState {
    public static final int MAP_W = 15;
    public static final int MAP_H = 13;
    public static final int TILE_SIZE = 40;
    public static final int HUD_W = 260;

    public static final double BASE_SPEED = 4.0;
    private static final double BOMB_FUSE = 2.0;
    private static final double EXPLOSION_TIME = 0.35;
    private static final double POWERUP_DROP_CHANCE = 0.25;
    private static final int WIN_TARGET = 3;

    public enum Phase {
        START,
        PLAYING,
        PAUSED,
        ROUND_OVER,
        GAME_OVER
    }

    public final Random random = new Random();

    public final TileMap map = new TileMap(MAP_W, MAP_H);
    public final List<Bomb> bombs = new ArrayList<>();
    public final List<Explosion> explosions = new ArrayList<>();
    public final List<PowerUp> powerUps = new ArrayList<>();

    public final HumanPlayer p1 = new HumanPlayer("P1", new Color(72, 132, 255), 1, 1);
    public final HumanPlayer p2 = new HumanPlayer("P2", new Color(255, 80, 80), MAP_W - 2, MAP_H - 2);
    public final BotPlayer bot = new BotPlayer("BOT", new Color(70, 197, 90), MAP_W - 2, 1);

    public Phase phase = Phase.START;
    public boolean aiEnabled = true;
    public int round = 1;
    public Player roundWinner;

    private int nextBombId = 1;

    public void startNewMatch() {
        p1.wins = 0;
        p2.wins = 0;
        bot.wins = 0;
        round = 1;
        startRound();
    }

    public void restartRoundKeepScore() {
        startRound();
    }

    public void startRound() {
        bombs.clear();
        explosions.clear();
        powerUps.clear();
        nextBombId = 1;
        roundWinner = null;

        map.generate(random, new Point(p1.spawnTx, p1.spawnTy), new Point(p2.spawnTx, p2.spawnTy), new Point(bot.spawnTx, bot.spawnTy));

        p1.resetForRound();
        p2.resetForRound();
        bot.resetForRound();
        if (!aiEnabled) {
            bot.alive = false;
        }

        phase = Phase.PLAYING;
    }

    public void togglePause() {
        if (phase == Phase.PLAYING) {
            phase = Phase.PAUSED;
        } else if (phase == Phase.PAUSED) {
            phase = Phase.PLAYING;
        }
    }

    public void toggleAI() {
        aiEnabled = !aiEnabled;
        if (phase == Phase.PLAYING || phase == Phase.PAUSED || phase == Phase.ROUND_OVER) {
            if (aiEnabled) {
                if (!bot.alive && bot.lives > 0) {
                    bot.alive = true;
                    bot.x = bot.spawnTx + 0.5;
                    bot.y = bot.spawnTy + 0.5;
                    bot.invincibleTime = 0.8;
                }
            } else {
                bot.alive = false;
            }
            if (phase == Phase.PLAYING) {
                checkRoundEnd();
            }
        }
    }

    public void onEnterPressed() {
        if (phase == Phase.START) {
            startNewMatch();
            return;
        }

        if (phase == Phase.ROUND_OVER) {
            if (p1.wins >= WIN_TARGET || p2.wins >= WIN_TARGET || bot.wins >= WIN_TARGET) {
                phase = Phase.GAME_OVER;
            } else {
                round++;
                startRound();
            }
            return;
        }

        if (phase == Phase.GAME_OVER) {
            startNewMatch();
        }
    }

    public void onRestartPressed() {
        startNewMatch();
    }

    public void update(
            double dt,
            boolean p1Up,
            boolean p1Down,
            boolean p1Left,
            boolean p1Right,
            boolean p2Up,
            boolean p2Down,
            boolean p2Left,
            boolean p2Right,
            boolean p1Bomb,
            boolean p2Bomb
    ) {
        if (phase != Phase.PLAYING) {
            return;
        }

        p1.updateInvincibility(dt);
        p2.updateInvincibility(dt);
        bot.updateInvincibility(dt);

        p1.updateBombPassThrough(this);
        p2.updateBombPassThrough(this);
        bot.updateBombPassThrough(this);

        double p1dx = (p1Right ? 1 : 0) - (p1Left ? 1 : 0);
        double p1dy = (p1Down ? 1 : 0) - (p1Up ? 1 : 0);
        p1.tryMove(p1dx, p1dy, dt, this);

        double p2dx = (p2Right ? 1 : 0) - (p2Left ? 1 : 0);
        double p2dy = (p2Down ? 1 : 0) - (p2Up ? 1 : 0);
        p2.tryMove(p2dx, p2dy, dt, this);

        if (aiEnabled) {
            bot.updateAI(dt, this);
        }

        if (p1Bomb) {
            placeBomb(p1, p1.tileX(), p1.tileY());
        }
        if (p2Bomb) {
            placeBomb(p2, p2.tileX(), p2.tileY());
        }

        updateBombs(dt);
        updateExplosions(dt);
        handlePowerUpPickup(p1);
        handlePowerUpPickup(p2);
        if (aiEnabled) {
            handlePowerUpPickup(bot);
        }
        applyExplosionDamage();
        checkRoundEnd();
    }

    private void updateBombs(double dt) {
        List<Bomb> toExplode = new ArrayList<>();
        for (Bomb b : bombs) {
            b.fuse -= dt;
            if (b.fuse <= 0) {
                toExplode.add(b);
            }
        }

        for (Bomb b : toExplode) {
            if (bombs.contains(b)) {
                detonateBomb(b, new HashSet<>());
            }
        }
    }

    private void updateExplosions(double dt) {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion e = it.next();
            e.timeLeft -= dt;
            if (e.timeLeft <= 0) {
                it.remove();
            }
        }
    }

    private void applyExplosionDamage() {
        for (Player p : allPlayers()) {
            if (!p.alive || p.isInvincible()) {
                continue;
            }
            int tx = p.tileX();
            int ty = p.tileY();
            for (Explosion e : explosions) {
                if (e.contains(tx, ty)) {
                    p.hit();
                    break;
                }
            }
        }
    }

    private void handlePowerUpPickup(Player player) {
        if (!player.alive) {
            return;
        }
        int tx = player.tileX();
        int ty = player.tileY();
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            if (p.tx == tx && p.ty == ty) {
                player.applyPowerUp(p.type);
                map.set(tx, ty, TileType.EMPTY);
                it.remove();
                break;
            }
        }
    }

    private void checkRoundEnd() {
        List<Player> alive = new ArrayList<>();
        if (p1.alive) {
            alive.add(p1);
        }
        if (p2.alive) {
            alive.add(p2);
        }
        if (aiEnabled && bot.alive) {
            alive.add(bot);
        }

        if (alive.size() <= 1) {
            roundWinner = alive.isEmpty() ? null : alive.get(0);
            if (roundWinner != null) {
                roundWinner.wins++;
            }
            phase = (p1.wins >= WIN_TARGET || p2.wins >= WIN_TARGET || bot.wins >= WIN_TARGET)
                    ? Phase.GAME_OVER
                    : Phase.ROUND_OVER;
        }
    }

    public boolean placeBomb(Player owner, int tx, int ty) {
        if (phase != Phase.PLAYING || !owner.canPlaceBomb()) {
            return false;
        }
        if (!map.inBounds(tx, ty) || !map.isWalkableTile(tx, ty)) {
            return false;
        }
        if (bombAt(tx, ty) != null) {
            return false;
        }

        Bomb bomb = new Bomb(nextBombId++, tx, ty, owner, BOMB_FUSE);
        bombs.add(bomb);
        owner.onBombPlaced(bomb.id);
        return true;
    }

    public Bomb bombAt(int tx, int ty) {
        for (Bomb b : bombs) {
            if (b.tx == tx && b.ty == ty) {
                return b;
            }
        }
        return null;
    }

    public Bomb findBombById(int id) {
        for (Bomb b : bombs) {
            if (b.id == id) {
                return b;
            }
        }
        return null;
    }

    private void detonateBomb(Bomb bomb, Set<Integer> chainVisited) {
        if (!chainVisited.add(bomb.id)) {
            return;
        }
        if (!bombs.remove(bomb)) {
            return;
        }
        bomb.owner.onBombRemoved();

        Explosion explosion = new Explosion(EXPLOSION_TIME);
        List<Point> cells = calcExplosionCells(bomb.tx, bomb.ty, bomb.owner.bombRange);
        explosion.cells.addAll(cells);
        explosions.add(explosion);

        for (Point c : cells) {
            TileType t = map.get(c.x, c.y);
            if (t == TileType.BREAKABLE) {
                map.set(c.x, c.y, TileType.EMPTY);
                if (random.nextDouble() < POWERUP_DROP_CHANCE) {
                    spawnPowerUp(c.x, c.y);
                }
            } else if (t == TileType.POWERUP) {
                removePowerUpAt(c.x, c.y);
                map.set(c.x, c.y, TileType.EMPTY);
            }

            Bomb other = bombAt(c.x, c.y);
            if (other != null) {
                detonateBomb(other, chainVisited);
            }
        }
    }

    private void spawnPowerUp(int tx, int ty) {
        removePowerUpAt(tx, ty);
        PowerUpType type;
        double r = random.nextDouble();
        if (r < 0.34) {
            type = PowerUpType.BOMB_CAPACITY;
        } else if (r < 0.67) {
            type = PowerUpType.BOMB_RANGE;
        } else {
            type = PowerUpType.SPEED;
        }
        map.set(tx, ty, TileType.POWERUP);
        powerUps.add(new PowerUp(tx, ty, type));
    }

    private void removePowerUpAt(int tx, int ty) {
        powerUps.removeIf(p -> p.tx == tx && p.ty == ty);
    }

    private List<Point> calcExplosionCells(int ox, int oy, int range) {
        List<Point> cells = new ArrayList<>();
        cells.add(new Point(ox, oy));

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int d = 0; d < 4; d++) {
            for (int i = 1; i <= range; i++) {
                int nx = ox + dx[d] * i;
                int ny = oy + dy[d] * i;
                TileType t = map.get(nx, ny);
                if (t == TileType.SOLID) {
                    break;
                }
                cells.add(new Point(nx, ny));
                if (t == TileType.BREAKABLE) {
                    break;
                }
            }
        }

        return cells;
    }

    public Set<Point> getDangerCells() {
        Set<Point> danger = new HashSet<>();
        for (Explosion e : explosions) {
            danger.addAll(e.cells);
        }
        for (Bomb b : bombs) {
            danger.addAll(calcExplosionCells(b.tx, b.ty, b.owner.bombRange));
        }
        return danger;
    }

    public boolean isBlockedForPlayer(Player player, double nx, double ny) {
        double half = 0.31;
        double[] xs = {nx - half, nx + half};
        double[] ys = {ny - half, ny + half};

        for (double x : xs) {
            for (double y : ys) {
                int tx = (int) Math.floor(x);
                int ty = (int) Math.floor(y);
                if (!map.inBounds(tx, ty)) {
                    return true;
                }
                TileType t = map.get(tx, ty);
                if (t == TileType.SOLID || t == TileType.BREAKABLE) {
                    return true;
                }

                Bomb b = bombAt(tx, ty);
                if (b != null && !player.passableBombIds.contains(b.id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPassableForPath(Player player, int x, int y) {
        if (!map.inBounds(x, y)) {
            return false;
        }
        TileType t = map.get(x, y);
        if (t == TileType.SOLID || t == TileType.BREAKABLE) {
            return false;
        }
        Bomb b = bombAt(x, y);
        if (b == null) {
            return true;
        }
        return player.passableBombIds.contains(b.id);
    }

    public boolean isSafeStandingTile(int x, int y) {
        if (!map.inBounds(x, y)) {
            return false;
        }
        TileType t = map.get(x, y);
        return t == TileType.EMPTY || t == TileType.POWERUP;
    }

    public boolean hasAdjacentBreakable(int tx, int ty) {
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        for (int i = 0; i < 4; i++) {
            if (map.get(tx + dx[i], ty + dy[i]) == TileType.BREAKABLE) {
                return true;
            }
        }
        return false;
    }

    public Player findNearestEnemy(Player from) {
        Player nearest = null;
        int best = Integer.MAX_VALUE;
        for (Player p : allPlayers()) {
            if (p == from || !p.alive) {
                continue;
            }
            if (p == bot && !aiEnabled) {
                continue;
            }
            int d = Math.abs(from.tileX() - p.tileX()) + Math.abs(from.tileY() - p.tileY());
            if (d < best) {
                best = d;
                nearest = p;
            }
        }
        return nearest;
    }

    public boolean isLineThreatClear(int sx, int sy, int ex, int ey) {
        if (sx != ex && sy != ey) {
            return false;
        }

        int dx = Integer.compare(ex, sx);
        int dy = Integer.compare(ey, sy);
        int x = sx + dx;
        int y = sy + dy;

        while (x != ex || y != ey) {
            TileType t = map.get(x, y);
            if (t == TileType.SOLID || t == TileType.BREAKABLE) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    public List<Player> allPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(bot);
        return players;
    }

    public String winnerText() {
        if (roundWinner == null) {
            return "Draw";
        }
        return "Winner: " + roundWinner.name;
    }

    public String championText() {
        Player champ = p1;
        if (p2.wins > champ.wins) {
            champ = p2;
        }
        if (bot.wins > champ.wins) {
            champ = bot;
        }
        return "Champion: " + champ.name;
    }
}


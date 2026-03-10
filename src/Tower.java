import java.awt.Point;
import java.util.List;

public class Tower {
    private final int tileX;
    private final int tileY;
    private final int cost;
    private final double rangePx;
    private final double fireCooldownMs;
    private final int damage;

    private double cooldownMs;

    public Tower(int tileX, int tileY, int cost, double rangePx, double fireCooldownMs, int damage) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.cost = cost;
        this.rangePx = rangePx;
        this.fireCooldownMs = fireCooldownMs;
        this.damage = damage;
        this.cooldownMs = 0;
    }

    public void update(double dtMs, List<Enemy> enemies, List<Effect> effects, int tileSize) {
        cooldownMs -= dtMs;
        if (cooldownMs > 0) {
            return;
        }

        double cx = Path.tileCenterX(tileX, tileSize);
        double cy = Path.tileCenterY(tileY, tileSize);

        Enemy best = null;
        double bestProgress = -1.0;

        for (Enemy enemy : enemies) {
            if (enemy == null || enemy.isDead() || enemy.hasReachedGoal()) {
                continue;
            }
            double dx = enemy.getX() - cx;
            double dy = enemy.getY() - cy;
            double dist = Math.hypot(dx, dy);
            if (dist <= rangePx) {
                if (enemy.getProgress() > bestProgress) {
                    bestProgress = enemy.getProgress();
                    best = enemy;
                }
            }
        }

        if (best != null) {
            best.takeDamage(damage);
            effects.add(new Effect(cx, cy, best.getX(), best.getY(), 100.0));
            cooldownMs = fireCooldownMs;
        }
    }

    public Point getTile() {
        return new Point(tileX, tileY);
    }

    public int getCost() {
        return cost;
    }

    public double getRangePx() {
        return rangePx;
    }
}
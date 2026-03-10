import java.awt.Point;
import java.util.List;

public class Enemy {
    private double x;
    private double y;
    private int pathIndex;

    private final int maxHp;
    private int hp;
    private final double speedPixelsPerSec;
    private final int rewardGold;

    private boolean reachedGoal;
    private double progress;

    public Enemy(List<Point> pathTiles, int tileSize, int hp, double speedPixelsPerSec, int rewardGold) {
        if (pathTiles == null || pathTiles.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty.");
        }
        Point spawn = pathTiles.get(0);
        this.x = Path.tileCenterX(spawn.x, tileSize);
        this.y = Path.tileCenterY(spawn.y, tileSize);
        this.pathIndex = 1;

        this.maxHp = Math.max(1, hp);
        this.hp = this.maxHp;
        this.speedPixelsPerSec = Math.max(1.0, speedPixelsPerSec);
        this.rewardGold = Math.max(0, rewardGold);
        this.progress = 0.0;
    }

    public void update(double dtSec, List<Point> pathTiles, int tileSize) {
        if (reachedGoal || hp <= 0 || pathIndex >= pathTiles.size()) {
            return;
        }

        double move = speedPixelsPerSec * dtSec;

        while (move > 0 && !reachedGoal && pathIndex < pathTiles.size()) {
            Point targetTile = pathTiles.get(pathIndex);
            double tx = Path.tileCenterX(targetTile.x, tileSize);
            double ty = Path.tileCenterY(targetTile.y, tileSize);
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
            Point targetTile = pathTiles.get(pathIndex);
            double tx = Path.tileCenterX(targetTile.x, tileSize);
            double ty = Path.tileCenterY(targetTile.y, tileSize);
            double remaining = Math.hypot(tx - x, ty - y);
            double seg = tileSize <= 0 ? 1.0 : tileSize;
            progress = nodeProgress - Math.min(1.0, remaining / seg);
        } else {
            progress = pathTiles.size();
        }
    }

    public void takeDamage(int damage) {
        if (damage <= 0 || hp <= 0) {
            return;
        }
        hp = Math.max(0, hp - damage);
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean hasReachedGoal() {
        return reachedGoal;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getRewardGold() {
        return rewardGold;
    }

    public double getProgress() {
        return progress;
    }
}
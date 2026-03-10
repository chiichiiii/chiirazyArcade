import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public abstract class Player {
    public final String name;
    public final Color color;
    public final int spawnTx;
    public final int spawnTy;

    public double x;
    public double y;
    public double speed;
    public int bombCapacity;
    public int bombRange;
    public int lives;
    public int wins;
    public boolean alive;
    public double invincibleTime;
    public int activeBombs;
    public final Set<Integer> passableBombIds = new HashSet<>();

    protected Player(String name, Color color, int spawnTx, int spawnTy) {
        this.name = name;
        this.color = color;
        this.spawnTx = spawnTx;
        this.spawnTy = spawnTy;
        resetForRound();
        this.wins = 0;
    }

    public void resetForRound() {
        this.x = spawnTx + 0.5;
        this.y = spawnTy + 0.5;
        this.speed = GameState.BASE_SPEED;
        this.bombCapacity = 1;
        this.bombRange = 2;
        this.lives = 3;
        this.alive = true;
        this.invincibleTime = 0.0;
        this.activeBombs = 0;
        this.passableBombIds.clear();
    }

    public void updateInvincibility(double dt) {
        if (invincibleTime > 0) {
            invincibleTime -= dt;
            if (invincibleTime < 0) {
                invincibleTime = 0;
            }
        }
    }

    public boolean isInvincible() {
        return invincibleTime > 0;
    }

    public int tileX() {
        return (int) Math.floor(x);
    }

    public int tileY() {
        return (int) Math.floor(y);
    }

    public void tryMove(double dirX, double dirY, double dt, GameState state) {
        if (!alive) {
            return;
        }

        double len = Math.hypot(dirX, dirY);
        if (len > 0.0001) {
            dirX /= len;
            dirY /= len;
        }

        double dist = speed * dt;
        moveAxis(dirX * dist, true, state);
        moveAxis(dirY * dist, false, state);
    }

    private void moveAxis(double amount, boolean xAxis, GameState state) {
        if (Math.abs(amount) < 1e-9) {
            return;
        }
        double nx = x;
        double ny = y;
        if (xAxis) {
            nx += amount;
        } else {
            ny += amount;
        }
        if (!state.isBlockedForPlayer(this, nx, ny)) {
            x = nx;
            y = ny;
        }
    }

    public boolean canPlaceBomb() {
        return alive && activeBombs < bombCapacity;
    }

    public void onBombPlaced(int bombId) {
        activeBombs++;
        passableBombIds.add(bombId);
    }

    public void onBombRemoved() {
        if (activeBombs > 0) {
            activeBombs--;
        }
    }

    public void updateBombPassThrough(GameState state) {
        passableBombIds.removeIf(id -> {
            Bomb b = state.findBombById(id);
            if (b == null) {
                return true;
            }
            return !(tileX() == b.tx && tileY() == b.ty);
        });
    }

    public void applyPowerUp(PowerUpType type) {
        switch (type) {
            case BOMB_CAPACITY -> bombCapacity = Math.min(5, bombCapacity + 1);
            case BOMB_RANGE -> bombRange = Math.min(8, bombRange + 1);
            case SPEED -> speed = Math.min(7.0, speed + 0.55);
        }
    }

    public void hit() {
        if (!alive || isInvincible()) {
            return;
        }
        lives--;
        if (lives <= 0) {
            alive = false;
            return;
        }
        invincibleTime = 1.2;
        x = spawnTx + 0.5;
        y = spawnTy + 0.5;
    }

    public boolean visibleForRender(long millis) {
        if (!isInvincible()) {
            return true;
        }
        return (millis / 90) % 2 == 0;
    }
}


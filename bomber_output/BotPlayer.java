import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BotPlayer extends Player {
    private double thinkCooldown = 0.0;
    private double bombCooldown = 0.0;
    private final List<Point> path = new ArrayList<>();

    public BotPlayer(String name, Color color, int spawnTx, int spawnTy) {
        super(name, color, spawnTx, spawnTy);
    }

    public void updateAI(double dt, GameState state) {
        if (!alive) {
            return;
        }

        thinkCooldown -= dt;
        bombCooldown -= dt;

        Point me = new Point(tileX(), tileY());
        Set<Point> danger = state.getDangerCells();

        if (danger.contains(me)) {
            refreshEscapePath(state, me, danger, true);
            moveByPath(dt, state);
            return;
        }

        if (bombCooldown <= 0 && canPlaceBomb() && shouldPlantBomb(state)) {
            if (state.placeBomb(this, tileX(), tileY())) {
                bombCooldown = 0.7;
                thinkCooldown = 0.15;
                refreshEscapePath(state, me, danger, false);
            }
        }

        if (thinkCooldown <= 0 || path.size() <= 1) {
            refreshRoamPath(state, me, danger);
            thinkCooldown = 0.25;
        }

        moveByPath(dt, state);
    }

    private void refreshEscapePath(GameState state, Point me, Set<Point> danger, boolean strictSafe) {
        List<Point> newPath = GridUtil.bfsPathToPredicate(
                me,
                p -> !danger.contains(p) && state.isSafeStandingTile(p.x, p.y),
                (x, y) -> state.isPassableForPath(this, x, y)
        );

        if (newPath.isEmpty() && !strictSafe) {
            newPath = GridUtil.bfsPathToPredicate(
                    me,
                    p -> state.isSafeStandingTile(p.x, p.y),
                    (x, y) -> state.isPassableForPath(this, x, y)
            );
        }

        path.clear();
        path.addAll(newPath);
    }

    private void refreshRoamPath(GameState state, Point me, Set<Point> danger) {
        Player target = state.findNearestEnemy(this);
        List<Point> newPath = List.of();

        if (target != null) {
            Point tp = new Point(target.tileX(), target.tileY());
            newPath = GridUtil.bfsPath(
                    me,
                    tp,
                    (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y))
            );
        }

        if (newPath.isEmpty()) {
            newPath = GridUtil.bfsPathToPredicate(
                    me,
                    p -> state.random.nextDouble() < 0.08 && state.isSafeStandingTile(p.x, p.y),
                    (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y))
            );
        }

        if (newPath.isEmpty()) {
            newPath = GridUtil.bfsPathToPredicate(
                    me,
                    p -> state.isSafeStandingTile(p.x, p.y),
                    (x, y) -> state.isPassableForPath(this, x, y)
            );
        }

        path.clear();
        path.addAll(newPath);
    }

    private void moveByPath(double dt, GameState state) {
        if (path.size() <= 1) {
            return;
        }

        Point next = path.get(1);
        double targetX = next.x + 0.5;
        double targetY = next.y + 0.5;
        double dx = targetX - x;
        double dy = targetY - y;

        if (Math.hypot(dx, dy) < 0.05) {
            path.remove(0);
            return;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            tryMove(Math.signum(dx), 0, dt, state);
        } else {
            tryMove(0, Math.signum(dy), dt, state);
        }
    }

    private boolean shouldPlantBomb(GameState state) {
        Player nearest = state.findNearestEnemy(this);
        if (nearest == null) {
            return false;
        }

        int tx = tileX();
        int ty = tileY();
        int ex = nearest.tileX();
        int ey = nearest.tileY();

        if (tx == ex) {
            int d = Math.abs(ty - ey);
            if (d <= bombRange && state.isLineThreatClear(tx, ty, ex, ey)) {
                return true;
            }
        }
        if (ty == ey) {
            int d = Math.abs(tx - ex);
            if (d <= bombRange && state.isLineThreatClear(tx, ty, ex, ey)) {
                return true;
            }
        }

        return state.hasAdjacentBreakable(tx, ty);
    }
}


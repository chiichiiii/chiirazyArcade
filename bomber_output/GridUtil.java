import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GridUtil {
    private static final int[] DX = {1, -1, 0, 0};
    private static final int[] DY = {0, 0, 1, -1};

    public interface CellPassable {
        boolean test(int x, int y);
    }

    public static List<Point> bfsPath(Point start, Point goal, CellPassable passable) {
        return bfsPathToPredicate(start, p -> p.equals(goal), passable);
    }

    public static List<Point> bfsPathToPredicate(Point start, Predicate<Point> goalPredicate, CellPassable passable) {
        ArrayDeque<Point> q = new ArrayDeque<>();
        Map<Point, Point> prev = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        Point startCopy = new Point(start);
        q.add(startCopy);
        visited.add(startCopy);

        Point found = null;
        while (!q.isEmpty()) {
            Point cur = q.poll();
            if (goalPredicate.test(cur)) {
                found = cur;
                break;
            }
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + DX[i];
                int ny = cur.y + DY[i];
                Point next = new Point(nx, ny);
                if (visited.contains(next)) {
                    continue;
                }
                if (!passable.test(nx, ny)) {
                    continue;
                }
                visited.add(next);
                prev.put(next, cur);
                q.add(next);
            }
        }

        if (found == null) {
            return List.of();
        }

        List<Point> path = new ArrayList<>();
        Point cur = found;
        while (cur != null) {
            path.add(0, cur);
            cur = prev.get(cur);
        }
        return path;
    }
}


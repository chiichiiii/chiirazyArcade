import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private Path() {
    }

    public static List<Point> createDefaultPath() {
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

    public static int tileCenterX(int tileX, int tileSize) {
        return tileX * tileSize + tileSize / 2;
    }

    public static int tileCenterY(int tileY, int tileSize) {
        return tileY * tileSize + tileSize / 2;
    }
}
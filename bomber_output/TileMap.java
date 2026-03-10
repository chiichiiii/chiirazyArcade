import java.awt.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileMap {
    public final int width;
    public final int height;
    private final TileType[][] tiles;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TileType[height][width];
    }

    public void generate(Random random, Point... safeSpawns) {
        Set<Point> safe = new HashSet<>();
        for (Point spawn : safeSpawns) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (Math.abs(x - spawn.x) + Math.abs(y - spawn.y) <= 2) {
                        safe.add(new Point(x, y));
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    tiles[y][x] = TileType.SOLID;
                    continue;
                }
                if (x % 2 == 0 && y % 2 == 0) {
                    tiles[y][x] = TileType.SOLID;
                    continue;
                }

                Point p = new Point(x, y);
                if (safe.contains(p)) {
                    tiles[y][x] = TileType.EMPTY;
                } else {
                    tiles[y][x] = random.nextDouble() < 0.62 ? TileType.BREAKABLE : TileType.EMPTY;
                }
            }
        }

        for (Point spawn : safeSpawns) {
            if (inBounds(spawn.x, spawn.y)) {
                tiles[spawn.y][spawn.x] = TileType.EMPTY;
            }
        }
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public TileType get(int x, int y) {
        if (!inBounds(x, y)) {
            return TileType.SOLID;
        }
        return tiles[y][x];
    }

    public void set(int x, int y, TileType type) {
        if (inBounds(x, y)) {
            tiles[y][x] = type;
        }
    }

    public boolean isWalkableTile(int x, int y) {
        TileType t = get(x, y);
        return t == TileType.EMPTY || t == TileType.POWERUP;
    }
}


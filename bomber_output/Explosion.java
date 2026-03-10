import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Explosion {
    public final Set<Point> cells = new HashSet<>();
    public double timeLeft;

    public Explosion(double duration) {
        this.timeLeft = duration;
    }

    public boolean contains(int tx, int ty) {
        return cells.contains(new Point(tx, ty));
    }
}


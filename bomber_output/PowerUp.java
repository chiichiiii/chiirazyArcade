import java.awt.Color;

public class PowerUp {
    public final int tx;
    public final int ty;
    public final PowerUpType type;

    public PowerUp(int tx, int ty, PowerUpType type) {
        this.tx = tx;
        this.ty = ty;
        this.type = type;
    }

    public Color color() {
        return switch (type) {
            case BOMB_CAPACITY -> new Color(83, 163, 247);
            case BOMB_RANGE -> new Color(255, 166, 64);
            case SPEED -> new Color(87, 206, 101);
        };
    }

    public String shortName() {
        return switch (type) {
            case BOMB_CAPACITY -> "B+";
            case BOMB_RANGE -> "R+";
            case SPEED -> "S+";
        };
    }
}


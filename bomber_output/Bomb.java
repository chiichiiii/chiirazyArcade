public class Bomb {
    public final int id;
    public final int tx;
    public final int ty;
    public final Player owner;
    public double fuse;

    public Bomb(int id, int tx, int ty, Player owner, double fuse) {
        this.id = id;
        this.tx = tx;
        this.ty = ty;
        this.owner = owner;
        this.fuse = fuse;
    }
}


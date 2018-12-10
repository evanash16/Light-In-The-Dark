package pathfinding;

public class Node {

    public int i, j;
    boolean traversable = true;
    public int gridWidth;

    public Node (int i, int j, int gridWidth) {
        this.i = i;
        this.j = j;
        this.gridWidth = gridWidth;
    }

    public Node(int i, int j, int gridWidth, boolean traversable) {
        this(i, j, gridWidth);
        this.traversable = traversable;
    }

    public int getI() {
        return this.i;
    }
    public int getJ() {
        return this.j;
    }

    public int getX() {
        return this.i * gridWidth;
    }
    public int getY() {
        return this.j * gridWidth;
    }

    public boolean isTraversable() {
        return this.traversable;
    }

    public boolean equals(Object other) {
        return other != null && other.getClass() == Node.class &&
                this.i == ((Node) other).i && this.j == ((Node) other).j;
    }
}

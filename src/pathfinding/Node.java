package pathfinding;

public class Node {

    public int i, j;
    public int gridWidth;

    public Node (int i, int j, int gridWidth) {
        this.i = i;
        this.j = j;
        this.gridWidth = gridWidth;
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

    public boolean equals(Object other) {
        return other.getClass() == Node.class &&
                this.i == ((Node) other).i && this.j == ((Node) other).j;
    }
}

package ilantsperber.snake;

public class Coord {
    private int x;
    private int y;

    public Coord(int x, int y) {
        setCoord(x, y);
    }

    public int x() {
        return x;
    }

    public void x(int newX) {
        x = newX;
    }

    public int y() {
        return y;
    }

    public void y(int newY) {
        y = newY;
    }

    public void setCoord(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public Coord copy() {
        return new Coord(x, y);
    }

    public void setCoord(Coord copyFrom) {
        setCoord(copyFrom.x, copyFrom.y);
    }

    public <T> T at2dGrid(T[][] grid) {
        return grid[x][y];
    }

    public boolean equals(Coord otherCoord) {
        return otherCoord.x == x && otherCoord.y == y;
    }

    public String toString() {
        return x + ", " + y;
    }
}

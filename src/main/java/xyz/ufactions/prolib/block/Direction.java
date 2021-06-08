package xyz.ufactions.prolib.block;

public enum Direction {
    LEFT(1), RIGHT(2), DOWN(3), UP(4);

    private final int index;

    Direction(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
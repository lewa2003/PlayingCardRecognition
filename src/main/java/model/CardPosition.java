package model;

public enum CardPosition {
    LEFT_TOP(new int[]{2, 4}),
    LEFT_BOTTOM(new int[]{2, 82}),
    RIGHT_TOP(new int[]{51, 4});

    private final int[] coordinates;

    CardPosition(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getCoordinates() {
        return coordinates;
    }
}

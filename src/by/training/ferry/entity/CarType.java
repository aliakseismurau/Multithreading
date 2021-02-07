package by.training.ferry.entity;

public enum CarType {
    PASSENGER_CAR(3, 20),
    TRUCK(10, 50);

    private final int weight; //tonnes
    private final int square; // square metres

    CarType(int weight, int square) {
        this.weight = weight;
        this.square = square;
    }

    public int getWeight() {
        return weight;
    }

    public int getSquare() {
        return square;
    }
}

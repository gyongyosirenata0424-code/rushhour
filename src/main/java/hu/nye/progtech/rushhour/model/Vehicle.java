package hu.nye.progtech.rushhour.model;

import java.util.Objects;

public final class Vehicle {
    private final char id;
    private final int startRow;
    private final int startCol;
    private final int length;
    private final Orientation orientation;

    public Vehicle(char id, int startRow, int startCol, int length, Orientation orientation) {
        this.id = id;
        this.startRow = startRow;
        this.startCol = startCol;
        this.length = length;
        this.orientation = orientation;
    }

    public char getId() { return id; }
    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }
    public int getLength() { return length; }
    public Orientation getOrientation() { return orientation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id &&
                startRow == vehicle.startRow &&
                startCol == vehicle.startCol &&
                length == vehicle.length &&
                orientation == vehicle.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startRow, startCol, length, orientation);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", pozíció=(" + startRow + "," + startCol + ")" +
                ", hossz=" + length +
                ", irány=" + orientation +
                '}';
    }
}

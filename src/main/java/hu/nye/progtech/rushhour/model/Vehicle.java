package hu.nye.progtech.rushhour.model;

import java.util.Objects;

/**
 * Egy játéktáblán lévő járművet reprezentáló, megváltoztathatatlan (Immutable) Value Object.
 * Builder tervezési mintával is példányosítható.
 */
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

    public static Builder builder() {
        return new Builder();
    }

    public char getId() {
        return id;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && startRow == vehicle.startRow && startCol == vehicle.startCol
                && length == vehicle.length && orientation == vehicle.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startRow, startCol, length, orientation);
    }

    @Override
    public String toString() {
        return "Vehicle{" + "id=" + id + ", startRow=" + startRow + ", startCol=" + startCol
                + ", length=" + length + ", orientation=" + orientation + '}';
    }

    /**
     * Builder tervezési minta a Vehicle objektumok átlátható felépítéséhez.
     */
    public static class Builder {
        private char id;
        private int startRow;
        private int startCol;
        private int length;
        private Orientation orientation;

        public Builder id(char id) {
            this.id = id;
            return this;
        }

        public Builder startRow(int startRow) {
            this.startRow = startRow;
            return this;
        }

        public Builder startCol(int startCol) {
            this.startCol = startCol;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(id, startRow, startCol, length, orientation);
        }
    }
}

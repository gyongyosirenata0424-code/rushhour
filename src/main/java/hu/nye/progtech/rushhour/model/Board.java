package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Rush Hour játéktábla aktuális állapotát reprezentáló, megváltoztathatatlan (Immutable) osztály.
 */
public final class Board {
    private final int rows;
    private final int cols;
    private final List<Vehicle> vehicles;
    private final int humanExitRow;
    private final int aiExitRow;

    public Board(int rows, int cols, List<Vehicle> vehicles, int humanExitRow, int aiExitRow) {
        this.rows = rows;
        this.cols = cols;
        this.vehicles = Collections.unmodifiableList(new ArrayList<>(vehicles));
        this.humanExitRow = humanExitRow;
        this.aiExitRow = aiExitRow;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int getHumanExitRow() {
        return humanExitRow;
    }

    public int getAiExitRow() {
        return aiExitRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Board board = (Board) o;
        return rows == board.rows
                && cols == board.cols
                && humanExitRow == board.humanExitRow
                && aiExitRow == board.aiExitRow
                && Objects.equals(vehicles, board.vehicles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, cols, vehicles, humanExitRow, aiExitRow);
    }

    @Override
    public String toString() {
        return "Board{" + "rows=" + rows + ", cols=" + cols
                + ", vehicles=" + vehicles
                + ", humanExitRow=" + humanExitRow
                + ", aiExitRow=" + aiExitRow + '}';
    }
}

package hu.nye.progtech.rushhour.validator;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * A lépések szabályosságát ellenőrző validátor osztály (Single Responsibility elv).
 */
public class MoveValidator {

    /**
     * Ellenőrzi a lépés szabályosságát, és visszaadja a célpozíciót [újSor, újOszlop] formában.
     */
    public int[] validateMove(Board board, char vehicleId, int steps) throws InvalidMoveException {
        if (steps == 0) {
            throw new InvalidMoveException("A lépésszám nem lehet 0!");
        }

        Vehicle target = findVehicle(board, vehicleId);
        boolean[][] occupiedByOthers = buildOccupiedGrid(board, vehicleId);

        int direction = Integer.signum(steps);
        int totalSteps = Math.abs(steps);
        int currentRow = target.getStartRow();
        int currentCol = target.getStartCol();

        for (int stepIndex = 1; stepIndex <= totalSteps; stepIndex++) {
            int nextRow = currentRow + (target.getOrientation() == Orientation.VERTICAL ? direction : 0);
            int nextCol = currentCol + (target.getOrientation() == Orientation.HORIZONTAL ? direction : 0);

            if (isSlidingOutThroughOwnExit(board, target, nextRow, nextCol)) {
                return new int[]{nextRow, nextCol};
            }

            if (isOutOfBounds(board, target, nextRow, nextCol)) {
                throw new InvalidMoveException("A(z) " + vehicleId + " jármű nem hagyhatja el a pályát itt!");
            }

            if (hasCollision(occupiedByOthers, target, nextRow, nextCol)) {
                throw new InvalidMoveException("Ütközés vagy átugrás! A(z) " + vehicleId + " útja el van állva.");
            }

            currentRow = nextRow;
            currentCol = nextCol;
        }

        return new int[]{currentRow, currentCol};
    }

    /**
     * Megkeresi a járművet azonosító alapján, vagy kivételt dob, ha nem létezik.
     */
    public Vehicle findVehicle(Board board, char vehicleId) throws InvalidMoveException {
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == vehicleId) {
                return v;
            }
        }
        throw new InvalidMoveException("Nem található '" + vehicleId + "' azonosítójú jármű!");
    }

    private boolean isSlidingOutThroughOwnExit(Board board, Vehicle target, int nextRow, int nextCol) {
        if (target.getOrientation() != Orientation.HORIZONTAL || nextCol <= target.getStartCol()) {
            return false;
        }
        if (target.getId() == 'A' && nextRow == board.getHumanExitRow()) {
            return (nextCol + target.getLength()) > board.getCols();
        }
        if (target.getId() == 'Z' && nextRow == board.getAiExitRow()) {
            return (nextCol + target.getLength()) > board.getCols();
        }
        return false;
    }

    private boolean[][] buildOccupiedGrid(Board board, char ignoredVehicleId) {
        boolean[][] occupied = new boolean[board.getRows()][board.getCols()];
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == ignoredVehicleId) {
                continue;
            }
            for (int i = 0; i < v.getLength(); i++) {
                int r = v.getStartRow() + (v.getOrientation() == Orientation.VERTICAL ? i : 0);
                int c = v.getStartCol() + (v.getOrientation() == Orientation.HORIZONTAL ? i : 0);
                if (r >= 0 && r < board.getRows() && c >= 0 && c < board.getCols()) {
                    occupied[r][c] = true;
                }
            }
        }
        return occupied;
    }

    private boolean isOutOfBounds(Board board, Vehicle target, int row, int col) {
        if (row < 0 || col < 0) {
            return true;
        }
        if (target.getOrientation() == Orientation.HORIZONTAL) {
            return col + target.getLength() > board.getCols();
        } else {
            return row + target.getLength() > board.getRows();
        }
    }

    private boolean hasCollision(boolean[][] occupied, Vehicle target, int row, int col) {
        for (int i = 0; i < target.getLength(); i++) {
            int r = row + (target.getOrientation() == Orientation.VERTICAL ? i : 0);
            int c = col + (target.getOrientation() == Orientation.HORIZONTAL ? i : 0);
            if (occupied[r][c]) {
                return true;
            }
        }
        return false;
    }
}


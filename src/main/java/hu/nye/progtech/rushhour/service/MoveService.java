package hu.nye.progtech.rushhour.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * A járművek szabályos mozgatásáért, az ütközés- és átugrás-ellenőrzésért felelős szolgáltatás.
 */
public class MoveService {

    /**
     * Funkcionális programozással (Stream API) kigyűjti a táblán végrehajtható összes érvényes 1 egységnyi lépést.
     */
    public List<String> getAvailableMoves(Board board) {
        return board.getVehicles().stream()
                .flatMap(vehicle -> IntStream.of(-1, 1)
                        .filter(step -> isMoveValid(board, vehicle.getId(), step))
                        .mapToObj(step -> formatMoveDescription(vehicle, step)))
                .collect(Collectors.toList());
    }

    /**
     * Visszaadja, hogy az adott lépés szabályosan végrehajtható-e.
     */
    public boolean isMoveValid(Board board, char vehicleId, int steps) {
        try {
            moveVehicle(board, vehicleId, steps);
            return true;
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    private String formatMoveDescription(Vehicle vehicle, int step) {
        String direction;
        if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
            direction = (step > 0) ? "jobbra (+1)" : "balra (-1)";
        } else {
            direction = (step > 0) ? "lefelé (+1)" : "felfelé (-1)";
        }
        return vehicle.getId() + " (" + direction + ")";
    }

    /**
     * Elmozdít egy járművet lépésenként ellenőrizve, hogy ne ugorhasson át más járművet,
     * és csak a céljármű csúszhasson ki a saját kijáratán.
     */
    public Board moveVehicle(Board currentBoard, char vehicleId, int steps) throws InvalidMoveException {
        if (steps == 0) {
            throw new InvalidMoveException("A lépésszám nem lehet 0!");
        }

        Vehicle target = findVehicle(currentBoard, vehicleId);
        boolean[][] occupiedByOthers = buildOccupiedGrid(currentBoard, vehicleId);

        int direction = Integer.signum(steps);
        int totalSteps = Math.abs(steps);
        int currentRow = target.getStartRow();
        int currentCol = target.getStartCol();

        for (int stepIndex = 1; stepIndex <= totalSteps; stepIndex++) {
            int nextRow = currentRow + (target.getOrientation() == Orientation.VERTICAL ? direction : 0);
            int nextCol = currentCol + (target.getOrientation() == Orientation.HORIZONTAL ? direction : 0);

            if (isSlidingOutThroughOwnExit(currentBoard, target, nextRow, nextCol)) {
                currentRow = nextRow;
                currentCol = nextCol;
                break;
            }

            if (isOutOfBounds(currentBoard, target, nextRow, nextCol)) {
                throw new InvalidMoveException("A(z) " + vehicleId + " jármű nem hagyhatja el a pályát itt!");
            }

            if (hasCollision(occupiedByOthers, target, nextRow, nextCol)) {
                throw new InvalidMoveException("Ütközés vagy átugrás! A(z) " + vehicleId + " útja el van állva.");
            }

            currentRow = nextRow;
            currentCol = nextCol;
        }

        List<Vehicle> updatedVehicles = new ArrayList<>();
        for (Vehicle v : currentBoard.getVehicles()) {
            if (v.getId() == vehicleId) {
                updatedVehicles.add(new Vehicle(v.getId(), currentRow, currentCol, v.getLength(), v.getOrientation()));
            } else {
                updatedVehicles.add(v);
            }
        }

        return new Board(currentBoard.getRows(), currentBoard.getCols(), updatedVehicles,
                currentBoard.getHumanExitRow(), currentBoard.getAiExitRow());
    }

    /**
     * Ellenőrzi, hogy egy céljármű ('A' vagy 'Z') épp a saját kijáratán csúszik-e ki a tábláról.
     */
    public boolean hasVehicleExited(Board board, char vehicleId) {
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == vehicleId) {
                int exitRow = (vehicleId == 'A') ? board.getHumanExitRow() : board.getAiExitRow();
                return v.getStartRow() == exitRow && (v.getStartCol() + v.getLength()) > board.getCols();
            }
        }
        return false;
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

    private Vehicle findVehicle(Board board, char vehicleId) throws InvalidMoveException {
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == vehicleId) {
                return v;
            }
        }
        throw new InvalidMoveException("Nem található '" + vehicleId + "' azonosítójú jármű!");
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

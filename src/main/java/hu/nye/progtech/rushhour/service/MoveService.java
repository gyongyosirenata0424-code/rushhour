package hu.nye.progtech.rushhour.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.validator.MoveValidator;

/**
 * A járművek mozgatásáért és az új táblaállapot előállításáért felelős szolgáltatás.
 */
public class MoveService {

    private final MoveValidator moveValidator;

    public MoveService() {
        this(new MoveValidator());
    }

    public MoveService(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

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
            moveValidator.validateMove(board, vehicleId, steps);
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
     * Validáltatja a lépést a MoveValidatorral, majd visszaadja a frissített táblát.
     */
    public Board moveVehicle(Board currentBoard, char vehicleId, int steps) throws InvalidMoveException {
        int[] targetPos = moveValidator.validateMove(currentBoard, vehicleId, steps);
        int newRow = targetPos[0];
        int newCol = targetPos[1];

        List<Vehicle> updatedVehicles = new ArrayList<>();
        for (Vehicle v : currentBoard.getVehicles()) {
            if (v.getId() == vehicleId) {
                updatedVehicles.add(new Vehicle(v.getId(), newRow, newCol, v.getLength(), v.getOrientation()));
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
}

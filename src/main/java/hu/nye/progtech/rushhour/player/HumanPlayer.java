package hu.nye.progtech.rushhour.player;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.MoveService;

/**
 * Az emberi játékos stratégiája: a szöveges parancsot irányra és lépésszámra fordítja.
 */
public class HumanPlayer implements Player {
    private final String name;
    private final char targetVehicleId;
    private final MoveService moveService;
    private String pendingCommand;

    public HumanPlayer(String name, char targetVehicleId, MoveService moveService) {
        this.name = name;
        this.targetVehicleId = targetVehicleId;
        this.moveService = moveService;
    }

    public void setPendingCommand(String pendingCommand) {
        this.pendingCommand = pendingCommand;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getTargetVehicleId() {
        return targetVehicleId;
    }

    @Override
    public Board takeTurn(Board currentBoard) throws InvalidMoveException {
        if (pendingCommand == null || pendingCommand.isBlank()) {
            throw new InvalidMoveException("Üres parancs! Példa: 'A 1' vagy 'A RIGHT 1'.");
        }

        String[] parts = pendingCommand.trim().split("\\s+");
        char vehicleId = parts[0].toUpperCase().charAt(0);
        int steps;

        try {
            if (parts.length == 2) {
                steps = Integer.parseInt(parts[1]);
            } else if (parts.length == 3) {
                Direction dir = Direction.valueOf(parts[1].toUpperCase());
                int amount = Integer.parseInt(parts[2]);
                steps = convertDirectionToSteps(currentBoard, vehicleId, dir, amount);
            } else {
                throw new InvalidMoveException("Hibás paraméterszám! Használd pl.: 'A 1' vagy 'A RIGHT 1'.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidMoveException("A lépésszámnak egész számnak kell lennie!");
        } catch (IllegalArgumentException e) {
            throw new InvalidMoveException("Ismeretlen irány! Használd: UP, DOWN, LEFT vagy RIGHT.");
        }

        return moveService.moveVehicle(currentBoard, vehicleId, steps);
    }

    private int convertDirectionToSteps(Board board, char vehicleId, Direction dir, int amount)
            throws InvalidMoveException {
        Vehicle v = board.getVehicles().stream()
                .filter(veh -> veh.getId() == vehicleId)
                .findFirst()
                .orElseThrow(() -> new InvalidMoveException("Nem létezik '" + vehicleId + "' azonosítójú jármű!"));

        if (v.getOrientation() == Orientation.HORIZONTAL) {
            if (dir == Direction.RIGHT) {
                return amount;
            }
            if (dir == Direction.LEFT) {
                return -amount;
            }
            throw new InvalidMoveException("A(z) " + vehicleId + " vízszintes jármű, csak LEFT vagy RIGHT irányba mozoghat!");
        } else {
            if (dir == Direction.DOWN) {
                return amount;
            }
            if (dir == Direction.UP) {
                return -amount;
            }
            throw new InvalidMoveException("A(z) " + vehicleId + " függőleges jármű, csak UP vagy DOWN irányba mozoghat!");
        }
    }
}


package hu.nye.progtech.rushhour.service;

import java.util.List;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveServiceTest {

    private MoveService moveService;

    @BeforeEach
    void setUp() {
        moveService = new MoveService();
    }

    @Test
    void testMoveVehicleShouldSlideHorizontalCarWhenPathIsClear() throws InvalidMoveException {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When
        Board result = moveService.moveVehicle(board, 'A', 2);

        // Then
        assertEquals(2, result.getVehicles().get(0).getStartCol());
    }

    @Test
    void testMoveVehicleShouldRejectJumpingOverBlockingVehicle() {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle blockerX = new Vehicle('X', 1, 2, 3, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(carA, blockerX), 2, 3);

        // When & Then
        assertThrows(InvalidMoveException.class, () -> moveService.moveVehicle(board, 'A', 3));
    }

    @Test
    void testMoveVehicleShouldAllowTargetCarToSlideOutThroughOwnExit() throws InvalidMoveException {
        // Given
        Vehicle carA = new Vehicle('A', 2, 4, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When
        Board result = moveService.moveVehicle(board, 'A', 1);

        // Then
        assertTrue(moveService.hasVehicleExited(result, 'A'));
    }

    @Test
    void testMoveVehicleShouldRejectSlidingOutOnWrongRow() {
        // Given
        Vehicle carA = new Vehicle('A', 1, 4, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When & Then
        assertThrows(InvalidMoveException.class, () -> moveService.moveVehicle(board, 'A', 1));
    }

    @Test
    void testMoveVehicleShouldRejectZeroStepsAndUnknownVehicle() {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When & Then
        assertThrows(InvalidMoveException.class, () -> moveService.moveVehicle(board, 'A', 0));
        assertThrows(InvalidMoveException.class, () -> moveService.moveVehicle(board, 'Q', 1));
    }

    @Test
    void testGetAvailableMovesShouldReturnOnlyValidDirections() {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle carV = new Vehicle('V', 0, 5, 2, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(carA, carV), 2, 3);

        // When
        List<String> moves = moveService.getAvailableMoves(board);

        // Then
        assertTrue(moves.contains("A (jobbra (+1))"));
        assertTrue(moves.contains("V (lefelé (+1))"));
        assertFalse(moves.contains("A (balra (-1))"));
    }
}

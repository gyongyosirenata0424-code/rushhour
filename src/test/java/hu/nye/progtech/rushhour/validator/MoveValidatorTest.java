package hu.nye.progtech.rushhour.validator;

import java.util.List;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveValidatorTest {

    private MoveValidator moveValidator;

    @BeforeEach
    void setUp() {
        moveValidator = new MoveValidator();
    }

    @Test
    void testValidateMoveShouldReturnTargetCoordinatesForValidMove() throws InvalidMoveException {
        // Given
        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When
        int[] targetPos = moveValidator.validateMove(board, 'A', 2);

        // Then
        assertArrayEquals(new int[]{2, 3}, targetPos);
    }

    @Test
    void testValidateMoveShouldThrowExceptionWhenStepsIsZero() {
        // Given
        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When & Then
        assertThrows(InvalidMoveException.class, () -> moveValidator.validateMove(board, 'A', 0));
    }

    @Test
    void testValidateMoveShouldThrowExceptionWhenJumpingOverVehicle() {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle blocker = new Vehicle('B', 1, 2, 3, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(carA, blocker), 2, 3);

        // When & Then
        assertThrows(InvalidMoveException.class, () -> moveValidator.validateMove(board, 'A', 3));
    }

    @Test
    void testFindVehicleShouldReturnVehicleOrThrowWhenMissing() throws InvalidMoveException {
        // Given
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        // When & Then
        assertEquals(carA, moveValidator.findVehicle(board, 'A'));
        assertThrows(InvalidMoveException.class, () -> moveValidator.findVehicle(board, 'X'));
    }
}


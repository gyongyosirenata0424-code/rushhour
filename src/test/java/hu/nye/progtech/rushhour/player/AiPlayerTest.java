package hu.nye.progtech.rushhour.player;

import java.util.List;
import java.util.Random;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.MoveService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiPlayerTest {

    @Test
    void testTakeTurnShouldAdvanceTargetCarWhenPathIsClear() throws InvalidMoveException {
        // Given
        MoveService mockMoveService = Mockito.mock(MoveService.class);
        Vehicle carZ = new Vehicle('Z', 3, 0, 2, Orientation.HORIZONTAL);
        Board initialBoard = new Board(6, 6, List.of(carZ), 2, 3);
        Board expectedBoard = new Board(6, 6,
                List.of(new Vehicle('Z', 3, 1, 2, Orientation.HORIZONTAL)), 2, 3);

        when(mockMoveService.isMoveValid(initialBoard, 'Z', 1)).thenReturn(true);
        when(mockMoveService.moveVehicle(initialBoard, 'Z', 1)).thenReturn(expectedBoard);

        AiPlayer aiPlayer = new AiPlayer("AI", 'Z', 'A', mockMoveService, new Random(42));

        // When
        Board result = aiPlayer.takeTurn(initialBoard);

        // Then
        assertEquals(expectedBoard, result);
        verify(mockMoveService).moveVehicle(initialBoard, 'Z', 1);
    }

    @Test
    void testTakeTurnShouldMoveOtherVehicleWhenTargetIsBlocked() {
        // Given
        MoveService realMoveService = new MoveService();
        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle carZ = new Vehicle('Z', 3, 0, 2, Orientation.HORIZONTAL);
        Vehicle blocker = new Vehicle('B', 2, 2, 2, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(carA, carZ, blocker), 2, 3);

        AiPlayer aiPlayer = new AiPlayer("AI", 'Z', 'A', realMoveService, new Random(1));

        // When
        Board result = aiPlayer.takeTurn(board);

        // Then
        assertEquals("AI", aiPlayer.getName());
        assertEquals('Z', aiPlayer.getTargetVehicleId());
        assertEquals(0, result.getVehicles().get(0).getStartCol());
    }

    @Test
    void testTakeTurnShouldReturnSameBoardWhenNoVehicleCanMove() {
        // Given
        MoveService mockMoveService = Mockito.mock(MoveService.class);
        Vehicle carZ = new Vehicle('Z', 3, 0, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carZ), 2, 3);

        AiPlayer aiPlayer = new AiPlayer("AI", 'Z', 'A', mockMoveService, new Random(1));

        // When
        Board result = aiPlayer.takeTurn(board);

        // Then
        assertEquals(board, result);
    }
}

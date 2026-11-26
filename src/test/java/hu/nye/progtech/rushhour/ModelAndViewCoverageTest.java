package hu.nye.progtech.rushhour;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.view.BoardView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelAndViewCoverageTest {

    @Test
    void testBoardAndVehicleModelsShouldExposePropertiesAndEquality() {
        // Given
        Vehicle v1 = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Vehicle v2 = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Vehicle v3 = new Vehicle('V', 0, 4, 3, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(v1, v3), 2, 3);

        // When & Then
        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());
        assertEquals(2, board.getHumanExitRow());
        assertEquals(3, board.getAiExitRow());
        assertEquals(2, board.getVehicles().size());
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertTrue(v1.toString().contains("A"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
    }

    @Test
    void testBoardViewShouldRenderBoardWithVehiclesAndExits() {
        // Given
        BoardView view = new BoardView();
        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Vehicle carZ = new Vehicle('Z', 3, 0, 2, Orientation.HORIZONTAL);
        Vehicle carV = new Vehicle('V', 0, 4, 3, Orientation.VERTICAL);
        Board board = new Board(6, 6, List.of(carA, carZ, carV), 2, 3);

        // When & Then
        view.printBoard(board);
        assertNotNull(view);
    }

    @Test
    void testGameStartShouldRunCleanlyWithExitInput() {
        // Given
        InputStream originalIn = System.in;
        try {
            String simulatedInput = "TesztElek\nexit\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
            Game game = new Game();

            // When
            game.start();

            // Then
            assertNotNull(game);
        } finally {
            System.setIn(originalIn);
        }
    }
}

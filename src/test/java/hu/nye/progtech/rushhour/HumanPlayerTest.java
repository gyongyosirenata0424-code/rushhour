package hu.nye.progtech.rushhour;

import java.util.List;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.player.HumanPlayer;
import hu.nye.progtech.rushhour.service.MoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HumanPlayerTest {

    private HumanPlayer humanPlayer;
    private Board board;

    @BeforeEach
    void setUp() {
        humanPlayer = new HumanPlayer("Reni", 'A', new MoveService());
        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Vehicle carV = new Vehicle('V', 1, 5, 2, Orientation.VERTICAL);
        board = new Board(6, 6, List.of(carA, carV), 2, 3);
    }

    @Test
    void testTakeTurnShouldMoveCarWithNumericCommand() throws InvalidMoveException {
        humanPlayer.setPendingCommand("A 1");
        Board result = humanPlayer.takeTurn(board);
        assertEquals(2, result.getVehicles().get(0).getStartCol());
    }

    @Test
    void testTakeTurnShouldMoveCarWithDirectionCommand() throws InvalidMoveException {
        humanPlayer.setPendingCommand("A LEFT 1");
        Board leftResult = humanPlayer.takeTurn(board);
        assertEquals(0, leftResult.getVehicles().get(0).getStartCol());

        humanPlayer.setPendingCommand("V UP 1");
        Board upResult = humanPlayer.takeTurn(board);
        assertEquals(0, upResult.getVehicles().get(1).getStartRow());

        humanPlayer.setPendingCommand("V DOWN 1");
        Board downResult = humanPlayer.takeTurn(board);
        assertEquals(2, downResult.getVehicles().get(1).getStartRow());
    }

    @Test
    void testTakeTurnShouldThrowExceptionForInvalidInputs() {
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("   ");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("A");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("A abc");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("A DIAGONAL 1");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("A UP 1");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("V LEFT 1");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));

        humanPlayer.setPendingCommand("Z RIGHT 1");
        assertThrows(InvalidMoveException.class, () -> humanPlayer.takeTurn(board));
    }
}


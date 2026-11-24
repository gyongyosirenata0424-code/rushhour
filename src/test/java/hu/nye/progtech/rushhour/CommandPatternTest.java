package hu.nye.progtech.rushhour;

import java.nio.file.Path;
import java.util.List;

import hu.nye.progtech.rushhour.command.Command;
import hu.nye.progtech.rushhour.command.ExitCommand;
import hu.nye.progtech.rushhour.command.HistoryCommand;
import hu.nye.progtech.rushhour.command.SaveCommand;
import hu.nye.progtech.rushhour.command.ScoresCommand;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.GameStateService;
import hu.nye.progtech.rushhour.service.HighScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class CommandPatternTest {

    @Test
    void testExitCommandShouldReturnFalseToStopGameLoop() {
        // Given
        Command exitCommand = new ExitCommand();

        // When
        boolean shouldContinue = exitCommand.execute();

        // Then
        assertFalse(shouldContinue);
    }

    @Test
    void testScoresCommandShouldCallHighScoreServiceAndReturnTrue() {
        // Given
        HighScoreService mockHighScoreService = Mockito.mock(HighScoreService.class);
        Command scoresCommand = new ScoresCommand(mockHighScoreService);

        // When
        boolean shouldContinue = scoresCommand.execute();

        // Then
        assertTrue(shouldContinue);
        verify(mockHighScoreService).printHighScores();
    }

    @Test
    void testHistoryCommandShouldReturnTrue() {
        // Given
        Command historyCommand = new HistoryCommand();

        // When
        boolean shouldContinue = historyCommand.execute();

        // Then
        assertTrue(shouldContinue);
    }

    @Test
    void testSaveCommandShouldSaveCurrentStateAndReturnTrue(@TempDir Path tempDir) {
        // Given
        String savePath = tempDir.resolve("cmd_save.txt").toString();
        GameStateService stateService = new GameStateService(savePath);
        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);
        Command saveCommand = new SaveCommand(stateService, () -> board, () -> 4);

        // When
        boolean shouldContinue = saveCommand.execute();

        // Then
        assertTrue(shouldContinue);
        GameStateService.LoadedState loaded = stateService.loadGame();
        assertNotNull(loaded);
        assertEquals(4, loaded.moveCount());
    }
}


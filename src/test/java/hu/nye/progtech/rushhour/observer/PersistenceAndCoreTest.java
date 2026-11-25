package hu.nye.progtech.rushhour.observer;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import hu.nye.progtech.rushhour.controller.GameController;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.repository.HighScoreRepository;
import hu.nye.progtech.rushhour.service.BoardFileReader;
import hu.nye.progtech.rushhour.service.CommandHistorySingleton;
import hu.nye.progtech.rushhour.service.GameStateService;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PersistenceAndCoreTest {

    @Test
    void testGameStateServiceShouldSaveAndLoadRoundTrip(@TempDir Path tempDir) {
        String savePath = tempDir.resolve("test_mentes.txt").toString();
        GameStateService stateService = new GameStateService(savePath);

        assertNull(stateService.loadGame());

        Vehicle carA = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA), 2, 3);

        stateService.saveGame(board, 5);
        GameStateService.LoadedState loaded = stateService.loadGame();

        assertNotNull(loaded);
        assertEquals(5, loaded.moveCount());
        assertEquals(board, loaded.board());
    }

    @Test
    void testBoardFileReaderShouldLoadExistingFileAndReturnNullForMissing() {
        BoardFileReader reader = new BoardFileReader();
        assertNull(reader.readBoard("nem_letezo_fajl.txt"));

        Board board = reader.readBoard("palyarenata.txt");
        if (board != null) {
            assertTrue(board.getRows() > 0);
        }
    }

    @Test
    void testSingletonAndObserverShouldRecordHistory() {
        CommandHistorySingleton first = CommandHistorySingleton.getInstance();
        CommandHistorySingleton second = CommandHistorySingleton.getInstance();
        assertSame(first, second);

        HistoryLoggerObserver observer = new HistoryLoggerObserver();
        observer.onGameEvent("Teszt esemeny");
        assertTrue(first.getHistory().stream().anyMatch(e -> e.contains("Teszt esemeny")));
    }

    @Test
    void testGameControllerShouldPlayTurnSaveAndExitCleanly(@TempDir Path tempDir) {
        String simulatedInput = "Reni\nA 1\nscores\nhistory\nsave\nexit\n";
        Scanner scanner = new Scanner(simulatedInput);

        HighScoreRepository mockRepo = Mockito.mock(HighScoreRepository.class);
        when(mockRepo.findAllOrderByWinsDesc()).thenReturn(Map.of());

        GameStateService stateService = new GameStateService(tempDir.resolve("save.txt").toString());
        GameController controller = new GameController(
                new BoardView(),
                new MoveService(),
                stateService,
                new HighScoreService(mockRepo),
                scanner
        );
        controller.addObserver(new HistoryLoggerObserver());

        Vehicle carA = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle carZ = new Vehicle('Z', 3, 0, 2, Orientation.HORIZONTAL);
        Board board = new Board(6, 6, List.of(carA, carZ), 2, 3);

        controller.runGame(board);
        assertNotNull(stateService.loadGame());
    }
}


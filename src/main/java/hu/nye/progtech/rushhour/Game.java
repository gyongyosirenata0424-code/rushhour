package hu.nye.progtech.rushhour;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import hu.nye.progtech.rushhour.controller.GameController;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.observer.HistoryLoggerObserver;
import hu.nye.progtech.rushhour.repository.HighScoreRepository;
import hu.nye.progtech.rushhour.repository.JdbcHighScoreRepository;
import hu.nye.progtech.rushhour.service.BoardFileReader;
import hu.nye.progtech.rushhour.service.GameStateService;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Rush Hour alkalmazás életciklusát és komponenseinek összekötését végző osztály.
 */
public class Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private final GameController controller;
    private final Board initialBoard;

    public Game() {
        BoardFileReader fileReader = new BoardFileReader();
        MoveService moveService = new MoveService();
        GameStateService gameStateService = new GameStateService();
        HighScoreRepository highScoreRepository = new JdbcHighScoreRepository();
        HighScoreService highScoreService = new HighScoreService(highScoreRepository);
        BoardView boardView = new BoardView();
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        this.controller = new GameController(boardView, moveService, gameStateService, highScoreService, scanner);
        this.controller.addObserver(new HistoryLoggerObserver());

        Board loadedBoard = fileReader.readBoard("palyarenata.txt");
        if (loadedBoard == null) {
            LOGGER.info("Tartalék pálya inicializálása.");
            loadedBoard = createDefaultBoard();
        }
        this.initialBoard = loadedBoard;
    }

    /**
     * Elindítja a játék fő ciklusát.
     */
    public void start() {
        LOGGER.info("Rush Hour alkalmazás indítása (Game.start)...");
        controller.runGame(initialBoard);
    }

    private Board createDefaultBoard() {
        List<Vehicle> vehicles = List.of(
                Vehicle.builder().id('A').startRow(2).startCol(0).length(2).orientation(Orientation.HORIZONTAL).build(),
                Vehicle.builder().id('Z').startRow(3).startCol(0).length(2).orientation(Orientation.HORIZONTAL).build(),
                Vehicle.builder().id('X').startRow(0).startCol(2).length(3).orientation(Orientation.VERTICAL).build(),
                Vehicle.builder().id('B').startRow(0).startCol(3).length(2).orientation(Orientation.HORIZONTAL).build(),
                Vehicle.builder().id('C').startRow(1).startCol(4).length(2).orientation(Orientation.VERTICAL).build(),
                Vehicle.builder().id('D').startRow(4).startCol(2).length(3).orientation(Orientation.HORIZONTAL).build()
        );
        return new Board(6, 6, vehicles, 2, 3);
    }
}


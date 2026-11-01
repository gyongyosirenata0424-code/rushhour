package hu.nye.progtech.rushhour;

import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.controller.GameController;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.BoardFileReader;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.GameStateService; // ÚJ
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        BoardView boardView = new BoardView();
        BoardFileReader fileReader = new BoardFileReader();
        MoveService moveService = new MoveService();
        HighScoreService highScoreService = new HighScoreService();
        GameStateService gameStateService = new GameStateService(); // ÚJ

        // Átadjuk az összes szükséges service-t a Controllernek
        GameController gameController = new GameController(boardView, moveService, highScoreService, gameStateService);

        Board board = fileReader.readBoard("palyarenata.txt");

        if (board == null) {
            LOGGER.warn("A pályafájl nem tölthető be! Átváltás az alapértelmezett (default) pályára...");
            List<Vehicle> defaultVehicles = new ArrayList<>();
            defaultVehicles.add(new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('B', 3, 4, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('X', 0, 0, 3, Orientation.VERTICAL));
            board = new Board(6, 6, defaultVehicles, 2, 3);
        }

        gameController.runGame(board);
    }
}

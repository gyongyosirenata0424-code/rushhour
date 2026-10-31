package hu.nye.progtech.rushhour;

import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.controller.GameController;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.BoardFileReader;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játék indításáért felelős főosztály.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * A program belépési pontja.
     */
    public static void main(String[] args) {
        BoardView boardView = new BoardView();
        BoardFileReader fileReader = new BoardFileReader();
        MoveService moveService = new MoveService();

        // 1. LÉPÉS: Létrehozzuk a Vezérlőt, és átadjuk neki a View-t és a MoveService-t
        GameController gameController = new GameController(boardView, moveService);

        // Pálya beolvasása a külső fájlból
        Board board = fileReader.readBoard("palyarenata.txt");

        // Vészhelyzeti tartalék (fallback), ha a fájl nem található
        if (board == null) {
            LOGGER.warn("A pályafájl nem tölthető be! Átváltás az alapértelmezett (default) pályára...");
            List<Vehicle> defaultVehicles = new ArrayList<>();
            defaultVehicles.add(new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('B', 3, 4, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('X', 0, 0, 3, Orientation.VERTICAL));
            board = new Board(6, 6, defaultVehicles, 2, 3);
        }

        // 2. LÉPÉS: Átadjuk az irányítást a Vezérlőnek, ami elindítja a játékot
        gameController.runGame(board);
    }
}

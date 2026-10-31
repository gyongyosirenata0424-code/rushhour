package hu.nye.progtech.rushhour;

import java.util.ArrayList;
import java.util.List;

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
        MoveService moveService = new MoveService(); // 1. LÉPÉS: Létrehozzuk a mozgató szolgáltatást

        // Pálya beolvasása a fájlból (vagy default pálya, ha nincs meg a fájl)
        Board board = fileReader.readBoard("palyarenata.txt");

        if (board == null) {
            LOGGER.warn("A pályafájl nem tölthető be! Átváltás az alapértelmezett (default) pályára...");
            List<Vehicle> defaultVehicles = new ArrayList<>();
            defaultVehicles.add(new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('B', 3, 4, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('X', 0, 0, 3, Orientation.VERTICAL));
            board = new Board(6, 6, defaultVehicles, 2, 3);
        }

        // 2. LÉPÉS: Kirajzoljuk a KIINDULÁSI pályát
        System.out.println("=== KEDZŐ ÁLLAPOT ===");
        boardView.printBoard(board);

        // 3. LÉPÉS: Teszünk egy próbalépést!
        // Toljuk el az 'A' autót JOBBRA 1 mezővel (mivel jobbra tolunk, a steps = 1)
        LOGGER.info("Próbalépés végrehajtása: 'A' autó jobbra 1 mezővel.");
        board = moveService.moveVehicle(board, 'A', 1);

        // 4. LÉPÉS: Kirajzoljuk a pályát a LÉPÉS UTÁN is
        System.out.println("=== LÉPÉS UTÁNI ÁLLAPOT ===");
        boardView.printBoard(board);
    }
}

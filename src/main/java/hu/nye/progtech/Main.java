package hu.nye.progtech.rushhour;

import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.BoardFileReader;
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

        // 1. KÍSÉRLET: Megpróbáljuk beolvasni a pályát a külső txt fájlból
        // Itt pontosan azt a nevet adjuk meg, amit a resources mappában létrehoztunk!
        Board board = fileReader.readBoard("palyarenata.txt");

        // 2. LÉPÉS: Ellenőrizzük, hogy sikerült-e a beolvasás
        if (board != null) {
            LOGGER.info("A játék sikeresen elindult a beolvasott fájl alapján.");
        } else {
            // Ha a fájl nem található vagy hibás (null-t kaptunk), felépítjük a beégetett default pályát
            LOGGER.warn("A pályafájl nem tölthető be! Átváltás az alapértelmezett (default) pályára...");

            List<Vehicle> defaultVehicles = new ArrayList<>();
            defaultVehicles.add(new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('B', 3, 4, 2, Orientation.HORIZONTAL));
            defaultVehicles.add(new Vehicle('X', 0, 0, 3, Orientation.VERTICAL));

            // Létrehozzuk a default táblát (6x6-os méretben)
            board = new Board(6, 6, defaultVehicles, 2, 3);
        }

        // 3. LÉPÉS: A kiválasztott tábla kirajzolása a parancssorra
        boardView.printBoard(board);
    }
}

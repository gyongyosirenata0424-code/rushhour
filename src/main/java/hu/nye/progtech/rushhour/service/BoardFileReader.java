package hu.nye.progtech.rushhour.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A konfigurációs fájlból történő pályabetöltésért felelős osztály.
 */
public class BoardFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardFileReader.class);

    public Board readBoard(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            LOGGER.warn("A konfigurációs pályafájl nem található: {}", fileName);
            return null;
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            if (!scanner.hasNextInt()) {
                LOGGER.warn("Érvénytelen pályafájl formátum: {}", fileName);
                return null;
            }
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int humanExitRow = scanner.nextInt();
            int aiExitRow = scanner.nextInt();

            List<Vehicle> vehicles = new ArrayList<>();
            while (scanner.hasNext()) {
                char id = scanner.next().charAt(0);
                int startRow = scanner.nextInt();
                int startCol = scanner.nextInt();
                int length = scanner.nextInt();
                Orientation orientation = Orientation.valueOf(scanner.next().toUpperCase());

                vehicles.add(new Vehicle(id, startRow, startCol, length, orientation));
            }

            LOGGER.info("Pálya sikeresen betöltve a fájlból: {}", fileName);
            return new Board(rows, cols, vehicles, humanExitRow, aiExitRow);
        } catch (Exception e) {
            LOGGER.error("Hiba a pályafájl beolvasása közben: {}", e.getMessage());
            return null;
        }
    }
}

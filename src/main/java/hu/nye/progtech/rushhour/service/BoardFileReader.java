package hu.nye.progtech.rushhour.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Szöveges fájlból játéktábla beolvasásáért felelős osztály.
 */
public class BoardFileReader {
    // A tanár által kötelezően kért LOGOLÁS beállítása
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardFileReader.class);

    /**
     * Beolvassa a pályát a megadott nevű erőforrásfájlból.
     * @param fileName A fájl neve a resources mappában (pl. "palyalaszlo.txt")
     * @return A felépített Board objektum, vagy null, ha a fájl nem található/hibás.
     */
    public Board readBoard(String fileName) {
        LOGGER.info("Pályafájl beolvasásának megkísérlése: {}", fileName);

        // Megpróbáljuk megnyitni a fájlt a resources mappából
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            LOGGER.warn("A keresett pályafájl nem található: {}", fileName);
            return null; // Ha nincs meg a fájl, nullt ad vissza (jelezve, hogy a default pályát kell használni)
        }

        // A BufferedReader segítségével tudunk hatékonyan, soronként olvasni a fájlból
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 1. SOR BEOLVASÁSA: Pálya mérete (N M)
            String firstLine = reader.readLine();
            if (firstLine == null) return null;
            String[] sizeParts = firstLine.split(" ");
            int rows = Integer.parseInt(sizeParts[0]);
            int cols = Integer.parseInt(sizeParts[1]);

            // 2. SOR BEOLVASÁSA: Kijáratok sora (HumanExit AIExit)
            String secondLine = reader.readLine();
            if (secondLine == null) return null;
            String[] exitParts = secondLine.split(" ");
            int humanExitRow = Integer.parseInt(exitParts[0]);
            int aiExitRow = Integer.parseInt(exitParts[1]);

            // A TÖBBI SOR BEOLVASÁSA: Járművek listája
            List<Vehicle> vehicles = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                // Ha üres sort találunk, ugorjuk át
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(" ");
                char id = parts[0].charAt(0);
                int startRow = Integer.parseInt(parts[1]);
                int startCol = Integer.parseInt(parts[2]);
                int length = Integer.parseInt(parts[3]);
                Orientation orientation = Orientation.valueOf(parts[4]);

                // Létrehozzuk a járművet a beolvasott adatokból és betesszük a listába
                vehicles.add(new Vehicle(id, startRow, startCol, length, orientation));
            }

            LOGGER.info("A pálya sikeresen beolvasva. Járművek száma: {}", vehicles.size());
            // Visszaadjuk a kész, felépített játéktáblát
            return new Board(rows, cols, vehicles, humanExitRow, aiExitRow);

        } catch (Exception e) {
            // Ha bármilyen hiba történik olvasás közben (pl. elgépelt szám a fájlban), lelogoljuk
            LOGGER.error("Hiba történt a pályafájl feldolgozása során!", e);
            return null;
        }
    }
}


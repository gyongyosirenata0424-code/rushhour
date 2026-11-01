package hu.nye.progtech.rushhour.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játék aktuális állásának mentéséért és betöltéséért felelős szolgáltatás.
 */
public class GameStateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateService.class);

    // A fájl neve, ahová az aktuális játékállást mentjük
    private static final String SAVE_FILE = "mentes.txt";

    /**
     * Elmenti az aktuális játéktáblát és a lépésszámot egy fájlba.
     */
    public void saveGame(Board board, int moveCount) {
        LOGGER.info("Játékállás mentése megkezdődött. Lépésszám: {}", moveCount);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // 1. sor: Pálya mérete és a kijáratok sora, valamint az aktuális lépésszám
            writer.write(board.getRows() + " " + board.getCols() + " " + board.getHumanExitRow() + " " + board.getExitRow() + " " + moveCount);
            writer.newLine();

            // A többi sor: A járművek adatai
            for (Vehicle v : board.getVehicles()) {
                writer.write(v.getId() + " " + v.getStartRow() + " " + v.getStartCol() + " " + v.getLength() + " " + v.getOrientation());
                writer.newLine();
            }
            LOGGER.info("Játékállás sikeresen elmentve a(z) {} fájlba.", SAVE_FILE);
        } catch (Exception e) {
            LOGGER.error("Hiba történt a játék mentése közben!", e);
        }
    }

    /**
     * Beolvassa az elmentett játékállást, ha létezik a fájl.
     * Mivel a tábla mellett a lépésszámot is vissza kell adnunk, egy kis segédtömböt vagy objektumot használhatunk,
     * de a legegyszerűbb, ha egy egyedi eredmény-objektumot adunk vissza.
     */
    public LoadedState loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            LOGGER.info("Nincs elmentett játékállás.");
            return null;
        }

        LOGGER.info("Mentett játékállás betöltése...");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // 1. sor beolvasása: rows cols humanExit aiExit moveCount
            String firstLine = reader.readLine();
            if (firstLine == null) return null;
            String[] parts = firstLine.split(" ");

            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);
            int humanExit = Integer.parseInt(parts[2]);
            int aiExit = Integer.parseInt(parts[3]);
            int moveCount = Integer.parseInt(parts[4]);

            // Járművek beolvasása
            List<Vehicle> vehicles = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] vParts = line.split(" ");
                char id = vParts[0].charAt(0);
                int startRow = Integer.parseInt(vParts[1]);
                int startCol = Integer.parseInt(vParts[2]);
                int length = Integer.parseInt(vParts[3]);
                Orientation orientation = Orientation.valueOf(vParts[4]);

                vehicles.add(new Vehicle(id, startRow, startCol, length, orientation));
            }

            Board board = new Board(rows, cols, vehicles, humanExit, aiExit);
            return new LoadedState(board, moveCount);

        } catch (Exception e) {
            LOGGER.error("Hiba történt a játék betöltése közben!", e);
            return null;
        }
    }

    /**
     * Segédosztály, ami összefogja a betöltött táblát és a hozzá tartozó lépésszámot.
     */
    public static class LoadedState {
        private final Board board;
        private final int moveCount;

        public LoadedState(Board board, int moveCount) {
            this.board = board;
            this.moveCount = moveCount;
        }

        public Board getBoard() { return board; }
        public int getMoveCount() { return moveCount; }
    }
}


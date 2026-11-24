package hu.nye.progtech.rushhour.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játékállás fájlba történő mentéséért és visszatöltéséért felelős szolgáltatás.
 */
public class GameStateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateService.class);
    private final String saveFilePath;

    public GameStateService() {
        this("mentes.txt");
    }

    public GameStateService(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    /**
     * A betöltött játékállást tároló megváltoztathatatlan Java record (equals/hashCode/toString beépítve).
     */
    public record LoadedState(Board board, int moveCount) {
    }

    public void saveGame(Board board, int moveCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath))) {
            writer.write(moveCount + "\n");
            writer.write(board.getRows() + " " + board.getCols() + " "
                    + board.getHumanExitRow() + " " + board.getAiExitRow() + "\n");
            for (Vehicle v : board.getVehicles()) {
                writer.write(v.getId() + " " + v.getStartRow() + " " + v.getStartCol() + " "
                        + v.getLength() + " " + v.getOrientation() + "\n");
            }
            LOGGER.info("Játékállás sikeresen mentve: {}", saveFilePath);
        } catch (IOException e) {
            LOGGER.error("Hiba a játékállás mentésekor: {}", e.getMessage());
        }
    }

    public LoadedState loadGame() {
        File file = new File(saveFilePath);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String moveLine = reader.readLine();
            if (moveLine == null) {
                return null;
            }
            int moveCount = Integer.parseInt(moveLine.trim());

            String boardLine = reader.readLine();
            if (boardLine == null) {
                return null;
            }
            String[] dims = boardLine.trim().split("\\s+");
            int rows = Integer.parseInt(dims[0]);
            int cols = Integer.parseInt(dims[1]);
            int humanExitRow = Integer.parseInt(dims[2]);
            int aiExitRow = Integer.parseInt(dims[3]);

            List<Vehicle> vehicles = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.trim().split("\\s+");
                char id = parts[0].charAt(0);
                int r = Integer.parseInt(parts[1]);
                int c = Integer.parseInt(parts[2]);
                int len = Integer.parseInt(parts[3]);
                Orientation orientation = Orientation.valueOf(parts[4]);
                vehicles.add(new Vehicle(id, r, c, len, orientation));
            }

            Board loadedBoard = new Board(rows, cols, vehicles, humanExitRow, aiExitRow);
            LOGGER.info("Mentett játékállás betöltve ({} lépés).", moveCount);
            return new LoadedState(loadedBoard, moveCount);
        } catch (Exception e) {
            LOGGER.error("Hiba a mentett játékállás betöltésekor: {}", e.getMessage());
            return null;
        }
    }
}

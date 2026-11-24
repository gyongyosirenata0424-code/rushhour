package hu.nye.progtech.rushhour.view;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * A játéktábla konzolos megjelenítéséért felelős osztály (a..f oszlopok, 1..N sorok).
 */
public class BoardView {

    public void printBoard(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();
        char[][] grid = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = '.';
            }
        }

        for (Vehicle v : board.getVehicles()) {
            for (int i = 0; i < v.getLength(); i++) {
                int r = v.getStartRow() + (v.getOrientation() == Orientation.VERTICAL ? i : 0);
                int c = v.getStartCol() + (v.getOrientation() == Orientation.HORIZONTAL ? i : 0);
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    grid[r][c] = v.getId();
                }
            }
        }

        System.out.print("\n    ");
        for (int c = 0; c < cols; c++) {
            char colLabel = (char) ('a' + c);
            System.out.print(colLabel + " ");
        }
        System.out.println();

        System.out.print("  +-");
        for (int c = 0; c < cols; c++) {
            System.out.print("--");
        }
        System.out.println("+");

        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d| ", r + 1);
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c] + " ");
            }

            if (r == board.getHumanExitRow()) {
                System.out.print("  ==> KIJÁRAT: TE ('A')");
            } else if (r == board.getAiExitRow()) {
                System.out.print("  ==> KIJÁRAT: AI ('Z')");
            } else {
                System.out.print("|");
            }
            System.out.println();
        }

        System.out.print("  +-");
        for (int c = 0; c < cols; c++) {
            System.out.print("--");
        }
        System.out.println("+\n");
    }
}

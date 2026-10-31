package hu.nye.progtech.rushhour.view;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * A játéktábla parancssoros (CLI) kirajzolásáért felelős osztály.
 * Ez valósítja meg az MVC minta View (Nézet) részét.
 */
public class BoardView {

    /**
     * Kirajzolja a kapott táblát és a rajta lévő járműveket a konzolra.
     * @param board A játéktábla objektum, amit meg akarunk jeleníteni.
     */
    public void printBoard(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();

        // 1. LÉPÉS: Létrehozunk a memóriában egy üres,NxM-es karakterrácsot (kétdimenziós tömböt)
        char[][] grid = new char[rows][cols];

        // 2. LÉPÉS: Feltöltjük a rácsot alapértelmezetten pontokkal ('.'), ami az üres mezőket jelzi
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }

        // 3. LÉPÉS: Ráhelyezzük az autókat a rácsra a koordinátáik és hosszuk alapján
        for (Vehicle vehicle : board.getVehicles()) {
            int r = vehicle.getStartRow();
            int c = vehicle.getStartCol();

            for (int i = 0; i < vehicle.getLength(); i++) {
                // Ellenőrizzük, hogy az autó sarka nem lóg-e le véletlenül a pályáról
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    grid[r][c] = vehicle.getId(); // Beírjuk az autó betűjelét (pl. 'A' vagy 'X')
                }

                // Ha az autó vízszintes, a következő darabkája eggyel jobbra (oszlop+) van
                if (vehicle.getOrientation() == hu.nye.progtech.rushhour.model.Orientation.HORIZONTAL) {
                    c++;
                } else {
                    // Ha függőleges, akkor eggyel lejjebb (sor+) van
                    r++;
                }
            }
        }

        // 4. LÉPÉS: Ténylegesen kirajzoljuk a rácsot a képernyőre, szép kerettel együtt
        System.out.println("--- RUSH HOUR PÁLYA ---");
        for (int i = 0; i < rows; i++) {
            // Sor eleji keret
            System.out.print("| ");
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            // Sor végi keret, plusz jelezzük, ha az adott sorban kijárat van
            System.out.print("|");
            if (i == board.getHumanExitRow()) {
                System.out.print(" -> HUMÁN KIJÁRAT ('A')");
            }
            if (i == board.getExitRow()) {
                System.out.print(" <- GÉPI KIJÁRAT ('B')");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }
}

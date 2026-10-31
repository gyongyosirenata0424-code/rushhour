package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Board {
    // 1. TULAJDONSÁGOK (VÁLTOZÓK)
    private final int rows;                      // A tábla sorainak száma (N)
    private final int cols;                      // A tábla oszlopainak száma (M)
    private final List<Vehicle> vehicles;        // A táblán lévő járművek listája

    // A leírás szerint a két játékos céljárművének ki kell jutnia a saját kijáratán.
    // Tároljuk el a kijárati pontok sorait (feltételezve, hogy a humán a jobb szélen, a gép a bal szélen távozik).
    private final int humanExitRow;              // A humán játékos ('A' autó) kijárati sora
    private final int aiExitRow;                 // A gépi játékos ('B' autó) kijárati sora

    // 2. KONSTRUKTOR (A tábla gyára)
    public Board(int rows, int cols, List<Vehicle> vehicles, int humanExitRow, int aiExitRow) {
        this.rows = rows;
        this.cols = cols;
        // Védekező másolás (Defensive copy): azért csinálunk új listát, hogy kívülről se lehessen módosítani a tábla tartalmát
        this.vehicles = java.util.Collections.unmodifiableList(new ArrayList<>(vehicles));
        this.humanExitRow = humanExitRow;
        this.aiExitRow = aiExitRow;
    }

    // 3. GETTEREK (Adatok biztonságos leolvasása)
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public int getHumanExitRow() { return humanExitRow; }
    public int getExitRow() { return aiExitRow; }

    // --- KÖTELEZŐ OBJECT METÓDUSOK ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return rows == board.rows &&
                cols == board.cols &&
                humanExitRow == board.humanExitRow &&
                aiExitRow == board.aiExitRow &&
                Objects.equals(vehicles, board.vehicles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, cols, vehicles, humanExitRow, aiExitRow);
    }

    @Override
    public String toString() {
        return "Board{" +
                "méret=" + rows + "x" + cols +
                ", járművek száma=" + vehicles.size() +
                ", humánKijáratSor=" + humanExitRow +
                ", aiKijáratSor=" + aiExitRow +
                '}';
    }
}

package hu.nye.progtech.rushhour;

import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.view.BoardView;

/**
 * A játék indításáért felelős főosztály.
 */
public class Main {

    /**
     * A program belépési pontja.
     */
    public static void main(String[] args) {
        // 1. LÉPÉS: Gyártunk néhány tesztjárművet a Vehicle osztályunkkal
        // Paraméterek: ID, kezdőSor, kezdőOszlop, hossz, irány
        Vehicle humanCar = new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL); // Humán játékos autója
        Vehicle aiCar = new Vehicle('B', 3, 4, 2, Orientation.HORIZONTAL);    // Gépi játékos autója
        Vehicle truckX = new Vehicle('X', 0, 0, 3, Orientation.VERTICAL);     // Egy függőleges teherautó akadály

        // 2. LÉPÉS: Betesszük a járműveket egy listába
        List<Vehicle> testVehicles = new ArrayList<>();
        testVehicles.add(humanCar);
        testVehicles.add(aiCar);
        testVehicles.add(truckX);

        // 3. LÉPÉS: Létrehozunk egy 6x6-os játéktáblát a listával és a kijáratok soraival
        // A humán kijárat a 2. sor végén lesz, az AI kijárat a 3. sor elején
        Board board = new Board(6, 6, testVehicles, 2, 3);

        // 4. LÉPÉS: Meghívjuk a View-t, hogy rajzolja ki ezt a táblát a konzolra
        BoardView boardView = new BoardView();
        boardView.printBoard(board);
    }
}


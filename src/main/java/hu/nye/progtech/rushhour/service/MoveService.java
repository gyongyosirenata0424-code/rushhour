package hu.nye.progtech.rushhour.service;

import java.util.ArrayList;
import java.util.List;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A járművek mozgásáért és a lépési szabályok ellenőrzéséért felelős osztály.
 */
public class MoveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveService.class);

    /**
     * Megkísérel elmozdítani egy járművet a táblán.
     * Mivel a Board és a Vehicle osztályaink Immutable-ök, ez a metódus egy ÚJ Board objektumot ad vissza a friss pozíciókkal!
     *
     * @param board A jelenlegi játéktábla.
     * @param vehicleId A mozgatni kívánt jármű betűje (pl. 'A').
     * @param steps Hány mezőt lépjen (pozitív szám = jobbra/le, negatív szám = balra/fel).
     * @return Az új, frissített Board objektum, vagy a RÉGI board, ha a lépés szabálytalan volt.
     */
    public Board moveVehicle(Board board, char vehicleId, int steps) {
        // 1. Megkeressük az autót a listában a betűjele alapján
        Vehicle targetVehicle = null;
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == vehicleId) {
                targetVehicle = v;
                break;
            }
        }

        if (targetVehicle == null) {
            LOGGER.warn("Sikertelen lépés: Nem található '{}' betűjelű jármű a táblán!", vehicleId);
            return board;
        }

        // 2. Kiszámoljuk az új kezdőpozíciót az irány és a lépésszám alapján
        int newRow = targetVehicle.getStartRow();
        int newCol = targetVehicle.getStartCol();

        if (targetVehicle.getOrientation() == Orientation.HORIZONTAL) {
            newCol += steps; // Vízszintes autó esetén az oszlop változik (pl. +1 jobbra, -1 balra)
        } else {
            newRow += steps; // Függőleges autó esetén a sor változik (pl. +1 le, -1 fel)
        }

        // 3. SZABÁLYELLENŐRZÉS A: Nem lóg-e le a pályáról az autó?
        if (newRow < 0 || newCol < 0) {
            LOGGER.warn("Szabálytalan lépés: A(z) '{}' jármű lemenne a pályáról a bal/felső szélen!", vehicleId);
            return board;
        }

        if (targetVehicle.getOrientation() == Orientation.HORIZONTAL) {
            if (newCol + targetVehicle.getLength() > board.getCols()) {
                LOGGER.warn("Szabálytalan lépés: A(z) '{}' jármű lemenne a pályáról a jobb szélen!", vehicleId);
                return board;
            }
        } else {
            if (newRow + targetVehicle.getLength() > board.getRows()) {
                LOGGER.warn("Szabálytalan lépés: A(z) '{}' jármű lemenne a pályáról az alsó szélen!", vehicleId);
                return board;
            }
        }

        // 4. Gyártunk egy ideiglenes új jármű objektumot a friss koordinátákkal
        Vehicle movedVehicle = new Vehicle(
                targetVehicle.getId(),
                newRow,
                newCol,
                targetVehicle.getLength(),
                targetVehicle.getOrientation()
        );

        // 5. SZABÁLYELLENŐRZÉS B: Nem ütközik-e egy másik autónak?
        // Ehhez létrehozunk egy új járműlistát, amiben a régit kicseréljük az új elmozdítottra
        List<Vehicle> updatedVehicles = new ArrayList<>();
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == vehicleId) {
                updatedVehicles.add(movedVehicle);
            } else {
                updatedVehicles.add(v);
            }
        }

        // Leellenőrizzük az ütközést egy segédfüggvénnyel (amit lentebb megírunk)
        if (hasCollision(updatedVehicles, board.getRows(), board.getCols())) {
            LOGGER.warn("Szabálytalan lépés: A(z) '{}' jármű ütközne egy másik járművel!", vehicleId);
            return board; // Ha ütközés van, visszadobjuk a régi, változatlan táblát
        }

        LOGGER.info("Sikeres lépés: '{}' jármű elmozdítva ennyi mezővel: {}", vehicleId, steps);

        // 6. Ha minden szabályos volt, visszatérünk egy VADIÚJ Board objektummal, ami már az új listát használja
        return new Board(board.getRows(), board.getCols(), updatedVehicles, board.getHumanExitRow(), board.getExitRow());
    }

    /**
     * Segédfüggvény, ami ellenőrzi, hogy a listában lévő autók átfedik-e (ütköznek-e) egymást.
     */
    private boolean hasCollision(List<Vehicle> vehicles, int rows, int cols) {
        // Gyártunk egy üres rácsot a háttérben
        boolean[][] occupied = new boolean[rows][cols];

        for (Vehicle v : vehicles) {
            int r = v.getStartRow();
            int c = v.getStartCol();

            for (int i = 0; i < v.getLength(); i++) {
                // Ha ez a hely már foglalt egy másik autó által, akkor ütközést találtunk!
                if (occupied[r][c]) {
                    return true;
                }
                occupied[r][c] = true;

                // Léptetjük a koordinátát az autó iránya szerint a vizsgálathoz
                if (v.getOrientation() == Orientation.HORIZONTAL) {
                    c++;
                } else {
                    r++;
                }
            }
        }
        return false; // Nincs ütközés
    }
}


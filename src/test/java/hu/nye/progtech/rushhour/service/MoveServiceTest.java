package hu.nye.progtech.rushhour.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A MoveService osztály működését ellenőrző JUnit 5 tesztosztály.
 */
public class MoveServiceTest {

    private MoveService underTest;
    private Board testBoard;

    /**
     * Ez a metódus minden egyes teszteset futtatása előtt automatikusan lefut.
     * Alaphelyzetbe állítja a MoveService-t és a teszttáblát.
     */
    @BeforeEach
    public void setUp() {
        underTest = new MoveService();

        // Gyártunk egy tiszta tesztpályát: egy vízszintes 'A' autót és egy függőleges 'X' akadályt
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle('A', 2, 1, 2, Orientation.HORIZONTAL)); // (2,1) és (2,2) helyen áll
        vehicles.add(new Vehicle('X', 0, 0, 3, Orientation.VERTICAL));   // (0,0), (1,0), (2,0) helyen áll

        testBoard = new Board(6, 6, vehicles, 2, 3);
    }

    /**
     * 1. TESZTESET: Ellenőrizzük, hogy a szabályos lépés sikeresen végbemegy-e.
     */
    @Test
    public void testMoveVehicleShouldMoveWhenStepIsRegular() {
        // GIVEN - Adott az 'A' autó a (2,1) pozíción, és el akarjuk tolni jobbra 1 mezővel
        char vehicleId = 'A';
        int steps = 1;

        // WHEN - Végrehajtjuk a mozgást
        Board result = underTest.moveVehicle(testBoard, vehicleId, steps);

        // THEN - Ellenőrizzük az eredményt (Assertion-ökkel)
        assertNotNull(result);

        // Megkeressük az elmozdított autót az új táblán
        Vehicle movedCar = result.getVehicles().stream()
                .filter(v -> v.getId() == 'A')
                .findFirst()
                .orElse(null);

        assertNotNull(movedCar);
        // A sorának változatlannak kell lennie (2)
        assertEquals(2, movedCar.getStartRow());
        // Az oszlopának 1-ről 2-re kellett változnia (mivel 1 + 1 = 2)
        assertEquals(2, movedCar.getStartCol());
    }

    /**
     * 2. TESZTESET: Ellenőrizzük, hogy ha egy jármű ütközne, a rendszer megakadályozza-e a mozgást.
     */
    @Test
    public void testMoveVehicleShouldNotMoveWhenCollisionOccurs() {
        // GIVEN - Az 'A' autót balra akarjuk tolni 1 mezővel (-1), de ott áll az 'X' teherautó
        char vehicleId = 'A';
        int steps = -1;

        // WHEN - Megpróbáljuk elmozdítani
        Board result = underTest.moveVehicle(testBoard, vehicleId, steps);

        // THEN - Mivel szabálytalan a lépés, a visszakapott táblának meg kell egyeznie a régi táblával
        assertEquals(testBoard, result);
    }
}

package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VehicleBuilderTest {

    @Test
    void testVehicleBuilderCreatesCorrectVehicle() {
        Vehicle builtVehicle = Vehicle.builder()
                .id('B')
                .startRow(1)
                .startCol(3)
                .length(2)
                .orientation(Orientation.HORIZONTAL)
                .build();

        assertEquals('B', builtVehicle.getId());
        assertEquals(1, builtVehicle.getStartRow());
        assertEquals(3, builtVehicle.getStartCol());
        assertEquals(2, builtVehicle.getLength());
        assertEquals(Orientation.HORIZONTAL, builtVehicle.getOrientation());
    }

    @Test
    void testVehicleEqualityAndHashCode() {
        Vehicle v1 = Vehicle.builder().id('A').startRow(2).startCol(0).length(2).orientation(Orientation.HORIZONTAL).build();
        Vehicle v2 = new Vehicle('A', 2, 0, 2, Orientation.HORIZONTAL);
        Vehicle v3 = Vehicle.builder().id('X').startRow(0).startCol(2).length(3).orientation(Orientation.VERTICAL).build();

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
    }
}


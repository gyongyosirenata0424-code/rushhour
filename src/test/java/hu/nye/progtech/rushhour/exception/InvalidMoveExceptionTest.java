package hu.nye.progtech.rushhour.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidMoveExceptionTest {

    @Test
    void testExceptionMessageIsPreserved() {
        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> {
            throw new InvalidMoveException("Szabálytalan lépés: ütközés!");
        });

        assertEquals("Szabálytalan lépés: ütközés!", exception.getMessage());
    }
}


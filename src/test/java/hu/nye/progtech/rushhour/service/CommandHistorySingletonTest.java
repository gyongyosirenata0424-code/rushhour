package hu.nye.progtech.rushhour.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandHistorySingletonTest {

    @BeforeEach
    void setUp() {
        CommandHistorySingleton.getInstance().clearHistory();
    }

    @Test
    void testGetInstanceAlwaysReturnsSameObject() {
        CommandHistorySingleton firstInstance = CommandHistorySingleton.getInstance();
        CommandHistorySingleton secondInstance = CommandHistorySingleton.getInstance();

        assertSame(firstInstance, secondInstance);
    }

    @Test
    void testRecordAndClearHistory() {
        CommandHistorySingleton history = CommandHistorySingleton.getInstance();
        history.recordEvent("Teszt esemény 1");
        history.recordEvent("Teszt esemény 2");

        assertEquals(2, history.getHistory().size());
        assertTrue(history.getHistory().get(0).contains("Teszt esemény 1"));

        history.clearHistory();
        assertTrue(history.getHistory().isEmpty());
    }
}


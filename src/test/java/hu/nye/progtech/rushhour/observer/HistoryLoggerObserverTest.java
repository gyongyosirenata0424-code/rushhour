package hu.nye.progtech.rushhour.observer;

import hu.nye.progtech.rushhour.service.CommandHistorySingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryLoggerObserverTest {

    @BeforeEach
    void setUp() {
        CommandHistorySingleton.getInstance().clearHistory();
    }

    @Test
    void testOnGameEventLogsToSingleton() {
        GameObserver observer = new HistoryLoggerObserver();
        observer.onGameEvent("Játékos lépett: A RIGHT 1");

        List<String> loggedEvents = CommandHistorySingleton.getInstance().getHistory();
        assertEquals(1, loggedEvents.size());
        assertTrue(loggedEvents.get(0).contains("Játékos lépett: A RIGHT 1"));
    }
}


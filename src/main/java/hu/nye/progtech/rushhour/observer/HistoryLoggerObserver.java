package hu.nye.progtech.rushhour.observer;

import hu.nye.progtech.rushhour.service.CommandHistorySingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Konkrét Observer implementáció, amely a játék eseményeit a Singleton parancsnaplóba és a logfájlba rögzíti.
 */
public class HistoryLoggerObserver implements GameObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryLoggerObserver.class);

    @Override
    public void onGameEvent(String eventMessage) {
        CommandHistorySingleton.getInstance().recordEvent(eventMessage);
        LOGGER.info("Játék esemény rögzítve: {}", eventMessage);
    }
}


package hu.nye.progtech.rushhour.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton tervezési mintát megvalósító központi parancs- és eseménynaplózó.
 * Garantálja, hogy az alkalmazás futása során csak egyetlen naplózó példány létezzen.
 */
public final class CommandHistorySingleton {
    private static CommandHistorySingleton instance;
    private final List<String> commandLog;
    private final DateTimeFormatter timeFormatter;

    private CommandHistorySingleton() {
        this.commandLog = new ArrayList<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    public static synchronized CommandHistorySingleton getInstance() {
        if (instance == null) {
            instance = new CommandHistorySingleton();
        }
        return instance;
    }

    public void recordEvent(String eventDescription) {
        String timestamp = LocalTime.now().format(timeFormatter);
        commandLog.add("[" + timestamp + "] " + eventDescription);
    }

    public List<String> getHistory() {
        return Collections.unmodifiableList(commandLog);
    }

    public void clearHistory() {
        commandLog.clear();
    }
}


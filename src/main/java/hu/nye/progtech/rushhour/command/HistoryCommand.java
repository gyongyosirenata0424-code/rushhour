package hu.nye.progtech.rushhour.command;

import hu.nye.progtech.rushhour.service.CommandHistorySingleton;

/**
 * A parancs- és eseménynapló kiírását végrehajtó parancs.
 */
public class HistoryCommand implements Command {

    @Override
    public boolean execute() {
        System.out.println("\n--- PARANCSNAPLÓ (Singleton) ---");
        CommandHistorySingleton.getInstance().getHistory().forEach(System.out::println);
        System.out.println("---------------------------------\n");
        return true;
    }
}


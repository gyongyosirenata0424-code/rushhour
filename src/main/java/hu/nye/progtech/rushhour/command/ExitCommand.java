package hu.nye.progtech.rushhour.command;

/**
 * A játékból történő kilépést végrehajtó parancs.
 */
public class ExitCommand implements Command {

    @Override
    public boolean execute() {
        System.out.println("Kilépés a játékból. Viszlát!");
        return false;
    }
}


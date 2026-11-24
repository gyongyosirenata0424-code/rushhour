package hu.nye.progtech.rushhour.command;

/**
 * A Command (Parancs) tervezési minta közös interfésze.
 * Visszatérési értéke jelzi, hogy a játékhurok folytatódjon-e (true) vagy lépjen ki (false).
 */
public interface Command {

    boolean execute();
}


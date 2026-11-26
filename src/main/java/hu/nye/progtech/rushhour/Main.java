package hu.nye.progtech.rushhour;

/**
 * Az alkalmazás belépési pontja.
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("slf4j.internal.verbosity", "WARN");
        Game game = new Game();
        game.start();
    }
}

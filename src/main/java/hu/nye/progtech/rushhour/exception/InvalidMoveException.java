package hu.nye.progtech.rushhour.exception;

/**
 * Akkor dobódik, ha a játékos szabálytalan lépést kísérel meg (pl. ütközés vagy pályaelhagyás).
 */
public class InvalidMoveException extends Exception {

    public InvalidMoveException(String message) {
        super(message);
    }
}


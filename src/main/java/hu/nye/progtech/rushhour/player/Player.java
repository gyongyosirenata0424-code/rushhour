package hu.nye.progtech.rushhour.player;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;

/**
 * Strategy tervezési minta interfésze a játékosok (emberi és AI) lépéseinek egységes kezelésére.
 */
public interface Player {

    String getName();

    char getTargetVehicleId();

    Board takeTurn(Board currentBoard) throws InvalidMoveException;
}


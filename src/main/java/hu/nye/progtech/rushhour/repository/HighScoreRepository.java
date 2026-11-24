package hu.nye.progtech.rushhour.repository;

import java.util.Map;

/**
 * Adatbázis-független interfész a játékosok győzelmeinek tárolásához (Repository minta).
 */
public interface HighScoreRepository {

    /**
     * Növeli az adott játékos győzelmeinek számát (vagy beszúrja 1 győzelemmel, ha még nem szerepel).
     */
    void recordWin(String playerName);

    /**
     * Visszaadja a játékosok nevét és győzelmeik számát győzelmek szerint csökkenő sorrendben.
     */
    Map<String, Integer> findAllOrderByWinsDesc();
}


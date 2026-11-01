package hu.nye.progtech.rushhour.model;

import java.util.Objects;

/**
 * Egy toplistás eredményt reprezentáló osztály.
 * Tartalmazza a játékos nevét és a sikeres lépéseinek számát.
 * Megvalósítja a Comparable interfészt, hogy a Java automatikusan sorba tudja rendezni lépésszám szerint.
 */
public final class HighScoreModel implements Comparable<HighScoreModel> {
    // 1. TULAJDONSÁGOK (Módosíthatatlan private final változók)
    private final String playerName;
    private final int moveCount;

    // 2. KONSTRUKTOR
    public HighScoreModel(String playerName, int moveCount) {
        this.playerName = playerName;
        this.moveCount = moveCount;
    }

    // 3. GETTEREK
    public String getPlayerName() { return playerName; }
    public int getMoveCount() { return moveCount; }

    // 4. SORBA RENDEZÉSI LOGIKA (A Comparable interfész miatt kötelező)
    @Override
    public int compareTo(HighScoreModel other) {
        // Növekvő sorrendbe rendezünk: aki kevesebb lépésből oldotta meg a játékot, az nyer!
        return Integer.compare(this.moveCount, other.moveCount);
    }

    // 5. KÖTELEZŐ METÓDUSOK AZ ÖSSZEHASONLÍTÁSHOZ ÉS KIÍRATÁSHOZ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HighScoreModel that = (HighScoreModel) o;
        return moveCount == that.moveCount && Objects.equals(playerName, that.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, moveCount);
    }

    @Override
    public String toString() {
        return playerName + " - " + moveCount + " lépés";
    }
}


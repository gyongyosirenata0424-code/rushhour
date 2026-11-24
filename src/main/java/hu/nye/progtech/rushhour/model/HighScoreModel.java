package hu.nye.progtech.rushhour.model;

import java.util.Objects;

/**
 * Egy toplistás eredményt reprezentáló osztály.
 * Tartalmazza a játékos nevét és a sikeres lépéseinek számát.
 * Megvalósítja a Comparable interfészt, hogy a Java automatikusan sorba tudja rendezni lépésszám szerint.
 */
public final class HighScoreModel implements Comparable<HighScoreModel> {
    private final String playerName;
    private final int moveCount;

    public HighScoreModel(String playerName, int moveCount) {
        this.playerName = playerName;
        this.moveCount = moveCount;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getMoveCount() {
        return moveCount;
    }

    @Override
    public int compareTo(HighScoreModel other) {
        return Integer.compare(this.moveCount, other.moveCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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


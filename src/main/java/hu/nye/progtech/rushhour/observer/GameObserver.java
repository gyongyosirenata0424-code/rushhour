package hu.nye.progtech.rushhour.observer;

/**
 * Observer tervezési minta interfésze a játékmenet eseményeinek megfigyeléséhez.
 */
public interface GameObserver {

    /**
     * Akkor hívódik meg, ha egy esemény (lépés, mentés, hiba) történik a játékban.
     *
     * @param eventMessage Az esemény szöveges leírása.
     */
    void onGameEvent(String eventMessage);
}


package hu.nye.progtech.rushhour.service;

import java.util.Map;

import hu.nye.progtech.rushhour.repository.HighScoreRepository;

/**
 * A győzelmek SQL adatbázisba történő mentését és a ranglista megjelenítését végző szolgáltatás.
 */
public class HighScoreService {
    private final HighScoreRepository repository;

    public HighScoreService(HighScoreRepository repository) {
        this.repository = repository;
    }

    /**
     * Rögzít egy győzelmet az adott játékos nevére az adatbázisban.
     */
    public void recordWin(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        repository.recordWin(playerName.trim());
    }

    /**
     * Lekérdezi a játékosok győzelmeit csökkenő sorrendben.
     */
    public Map<String, Integer> getHighScores() {
        return repository.findAllOrderByWinsDesc();
    }

    /**
     * Kiírja a konzolra a dicsőséglistát (Top 10).
     */
    public void printHighScores() {
        Map<String, Integer> scores = getHighScores();
        System.out.println("\n================ TOPLISTA (SQL DB) ================");
        if (scores.isEmpty()) {
            System.out.println("  Még nincs rögzített győzelem az adatbázisban.");
        } else {
            int rank = 1;
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                if (rank > 10) {
                    break;
                }
                System.out.printf("  %d. %-20s - %d győzelem%n", rank, entry.getKey(), entry.getValue());
                rank++;
            }
        }
        System.out.println("===================================================\n");
    }
}

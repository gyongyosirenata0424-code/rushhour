package hu.nye.progtech.rushhour.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.nye.progtech.rushhour.model.HighScoreModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A toplista mentéséért, betöltéséért és megjelenítéséért felelős szolgáltatás.
 */
public class HighScoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HighScoreService.class);

    // A fájl neve, ahová a helyi gépen menteni fogjuk a toplistát
    private static final String SCORE_FILE = "toplista.txt";

    /**
     * Elment egy új eredményt a toplistafájlba.
     */
    public void saveScore(String playerName, int moveCount) {
        LOGGER.info("Új eredmény mentése a toplistába: {} - {} lépés", playerName, moveCount);

        // A FileWriter true paramétere (append mód) gondoskodik róla, hogy a meglévő fájl végére fűzzük az új sort, ne töröljük a régit
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(playerName + "," + moveCount);
            writer.newLine(); // Új sorba lépünk a következő mentésnek
        } catch (Exception e) {
            LOGGER.error("Hiba történt az eredmény mentése közben!", e);
        }
    }

    /**
     * Beolvassa az összes eddigi eredményt a fájlból, sorba rendezi őket, és visszaadja a legjobbakat.
     */
    public List<HighScoreModel> loadHighScores() {
        List<HighScoreModel> scores = new ArrayList<>();
        File file = new File(SCORE_FILE);

        // Ha a fájl még nem létezik (mert senki sem játszott még), üres listát adunk vissza
        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Feldaraboljuk a sort a vessző mentén (Név,Lépésszám)
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int moves = Integer.parseInt(parts[1]);
                    scores.add(new HighScoreModel(name, moves));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Hiba történt a toplista betöltése közben!", e);
        }

        // Itt történik a csoda! A HighScoreModel-ben megírt compareTo metódus alapján automatikusan sorba rendezi a Java
        Collections.sort(scores);

        return scores;
    }

    /**
     * Szépen formázva kiírja a legjobb 10 eredményt a konzolra.
     */
    public void printTopTen() {
        List<HighScoreModel> scores = loadHighScores();

        System.out.println("\n======= 🏆 TOP 10 RANGLISTA 🏆 =======");
        if (scores.isEmpty()) {
            System.out.println("Még nincsenek elmentett eredmények. Légy te az első!");
        } else {
            // Maximum 10 elemet íratunk ki
            int limit = Math.min(10, scores.size());
            for (int i = 0; i < limit; i++) {
                System.out.println((i + 1) + ". hely: " + scores.get(i));
            }
        }
        System.out.println("=====================================\n");
    }
}


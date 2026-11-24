package hu.nye.progtech.rushhour.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC és beágyazott H2 SQL adatbázis alapú megvalósítás a győzelmek tárolásához.
 */
public class JdbcHighScoreRepository implements HighScoreRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHighScoreRepository.class);

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public JdbcHighScoreRepository() {
        this("jdbc:h2:file:./rushhour_db", "sa", "");
    }

    public JdbcHighScoreRepository(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        initDatabase();
    }

    private void initDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS high_scores ("
                + "player_name VARCHAR(100) PRIMARY KEY, "
                + "wins INT NOT NULL"
                + ")";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            LOGGER.error("Hiba a H2 adatbázis tábla létrehozásakor: {}", e.getMessage());
        }
    }

    @Override
    public void recordWin(String playerName) {
        String selectSql = "SELECT wins FROM high_scores WHERE player_name = ?";
        String updateSql = "UPDATE high_scores SET wins = wins + 1 WHERE player_name = ?";
        String insertSql = "INSERT INTO high_scores (player_name, wins) VALUES (?, 1)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, playerName);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, playerName);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, playerName);
                        insertStmt.executeUpdate();
                    }
                }
            }
            LOGGER.info("Győzelem rögzítve az SQL adatbázisban: {}", playerName);
        } catch (SQLException e) {
            LOGGER.error("Adatbázis hiba a győzelem mentésekor: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> findAllOrderByWinsDesc() {
        Map<String, Integer> results = new LinkedHashMap<>();
        String querySql = "SELECT player_name, wins FROM high_scores ORDER BY wins DESC, player_name ASC";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement stmt = conn.prepareStatement(querySql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.put(rs.getString("player_name"), rs.getInt("wins"));
            }
        } catch (SQLException e) {
            LOGGER.error("Adatbázis hiba a toplista lekérdezésekor: {}", e.getMessage());
        }
        return results;
    }
}

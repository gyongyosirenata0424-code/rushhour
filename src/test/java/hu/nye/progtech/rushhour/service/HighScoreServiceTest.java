package hu.nye.progtech.rushhour.service;

import java.nio.file.Path;
import java.util.Map;

import hu.nye.progtech.rushhour.repository.HighScoreRepository;
import hu.nye.progtech.rushhour.repository.JdbcHighScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HighScoreServiceTest {

    @Test
    void testRecordWinShouldCallRepositoryWithTrimmedName() {
        // Given
        HighScoreRepository mockRepo = Mockito.mock(HighScoreRepository.class);
        HighScoreService service = new HighScoreService(mockRepo);

        // When
        service.recordWin("  Reni  ");
        service.recordWin("   ");
        service.recordWin(null);

        // Then
        verify(mockRepo).recordWin("Reni");
        verify(mockRepo, never()).recordWin("");
    }

    @Test
    void testPrintHighScoresShouldQueryRepository() {
        // Given
        HighScoreRepository mockRepo = Mockito.mock(HighScoreRepository.class);
        when(mockRepo.findAllOrderByWinsDesc()).thenReturn(Map.of("Reni", 3, "AI", 1));
        HighScoreService service = new HighScoreService(mockRepo);

        // When
        Map<String, Integer> result = service.getHighScores();
        service.printHighScores();

        // Then
        assertEquals(3, result.get("Reni"));
        verify(mockRepo, Mockito.times(2)).findAllOrderByWinsDesc();
    }

    @Test
    void testJdbcHighScoreRepositoryShouldInsertAndIncrementWins(@TempDir Path tempDir) {
        // Given
        String dbUrl = "jdbc:h2:file:" + tempDir.resolve("test_db").toAbsolutePath();
        JdbcHighScoreRepository jdbcRepo = new JdbcHighScoreRepository(dbUrl, "sa", "");
        HighScoreService service = new HighScoreService(jdbcRepo);

        // When
        service.recordWin("Reni");
        service.recordWin("Reni");
        service.recordWin("AI");

        // Then
        Map<String, Integer> scores = service.getHighScores();
        assertEquals(2, scores.get("Reni"));
        assertEquals(1, scores.get("AI"));
    }
}


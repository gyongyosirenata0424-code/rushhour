package hu.nye.progtech.rushhour.command;

import hu.nye.progtech.rushhour.service.HighScoreService;

/**
 * A dicsőséglista (H2 SQL adatbázis) megjelenítését végrehajtó parancs.
 */
public class ScoresCommand implements Command {
    private final HighScoreService highScoreService;

    public ScoresCommand(HighScoreService highScoreService) {
        this.highScoreService = highScoreService;
    }

    @Override
    public boolean execute() {
        highScoreService.printHighScores();
        return true;
    }
}

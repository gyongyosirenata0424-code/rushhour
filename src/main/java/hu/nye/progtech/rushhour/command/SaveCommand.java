package hu.nye.progtech.rushhour.command;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.service.GameStateService;

/**
 * Az aktuális játékállás fájlba mentését végrehajtó parancs.
 */
public class SaveCommand implements Command {
    private final GameStateService gameStateService;
    private final Supplier<Board> boardSupplier;
    private final IntSupplier moveCountSupplier;

    public SaveCommand(GameStateService gameStateService,
                       Supplier<Board> boardSupplier,
                       IntSupplier moveCountSupplier) {
        this.gameStateService = gameStateService;
        this.boardSupplier = boardSupplier;
        this.moveCountSupplier = moveCountSupplier;
    }

    @Override
    public boolean execute() {
        int currentMoves = moveCountSupplier.getAsInt();
        gameStateService.saveGame(boardSupplier.get(), currentMoves);
        System.out.println("Játékállás sikeresen elmentve a(z) " + currentMoves + ". lépésnél!");
        return true;
    }
}


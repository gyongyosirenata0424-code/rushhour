package hu.nye.progtech.rushhour.controller;

import java.util.Scanner;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.service.GameStateService; // ÚJ IMPORT
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final BoardView boardView;
    private final MoveService moveService;
    private final HighScoreService highScoreService;
    private final GameStateService gameStateService; // ÚJ BEJEGYZÉS
    private final Scanner scanner;

    // Frissített konstruktor
    public GameController(BoardView boardView, MoveService moveService, HighScoreService highScoreService, GameStateService gameStateService) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.highScoreService = highScoreService;
        this.gameStateService = gameStateService;
        this.scanner = new Scanner(System.in);
    }

    public void runGame(Board initialBoard) {
        Board board = initialBoard;
        int moveCount = 0;

        // ÚJ: Indításkor ellenőrizzük, van-e mentett állás
        GameStateService.LoadedState savedState = gameStateService.loadGame();
        if (savedState != null) {
            System.out.print("Találtam egy elmentett játékállást! Szeretnéd betölteni? (igen/nem): ");
            String answer = scanner.nextLine().trim();
            if ("igen".equalsIgnoreCase(answer)) {
                board = savedState.getBoard();
                moveCount = savedState.getMoveCount();
                LOGGER.info("Mentett játékállás sikeresen betöltve. Lépésszám visszaállítva: {}", moveCount);
            }
        }

        System.out.println("Üdvözöllek a Rush Hour játékban!");
        System.out.println("Parancs formátuma: [AutóBetűjele] [Lépésszám] (Pl: A 1 vagy X -2)");
        System.out.println("Mentéshez írd be: save | Kilépéshez: exit");

        while (!isGameWon(board)) {
            boardView.printBoard(board);
            System.out.println("Eddigi lépések száma: " + moveCount);

            System.out.print("Kérlek, add meg a lépésed: ");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                LOGGER.info("A játékos kezdeményezte a kilépést.");
                System.out.println("Köszönöm a játékot! Viszlát!");
                return;
            }

            // ÚJ PARANCS: Mentés kezelése
            if ("save".equalsIgnoreCase(input)) {
                gameStateService.saveGame(board, moveCount);
                System.out.println("A játékállás sikeresen elmentve!");
                continue;
            }

            try {
                String[] parts = input.split(" ");
                if (parts.length != 2) {
                    System.out.println("Hibás formátum! Kérlek, két dolgot adj meg szóközzel elválasztva.");
                    continue;
                }

                char vehicleId = parts[0].toUpperCase().charAt(0);
                int steps = Integer.parseInt(parts[1]);

                Board previousBoard = board;
                board = moveService.moveVehicle(board, vehicleId, steps);

                if (!board.equals(previousBoard)) {
                    moveCount++;
                }

            } catch (Exception e) {
                System.out.println("Érvénytelen parancs! Győződj meg róla, hogy a második paraméter egy szám.");
            }
        }

        // Győzelem kezelése
        boardView.printBoard(board);
        LOGGER.info("A játék sikeresen véget ért, győzelemmel! Összes lépés: {}", moveCount);

        System.out.println("GRATULÁLOK! Sikeresen kijuttattad a céljárművet, megnyerted a játékot!");
        System.out.println("A játékot összesen " + moveCount + " sikeres lépésből oldottad meg.");

        System.out.print("Kérlek, add meg a neved a toplistához: ");
        String playerName = scanner.nextLine().trim();

        if (playerName.isEmpty()) {
            playerName = "Névtelen Játékos";
        }

        highScoreService.saveScore(playerName, moveCount);
        System.out.println("Köszönjük, " + playerName + "! Az eredményed elmentve.");
        highScoreService.printTopTen();
    }

    private boolean isGameWon(Board board) {
        for (Vehicle v : board.getVehicles()) {
            if (v.getId() == 'A') {
                int carFrontCol = v.getStartCol() + v.getLength();
                if (v.getStartRow() == board.getHumanExitRow() && carFrontCol >= board.getCols()) {
                    return true;
                }
            }
        }
        return false;
    }
}

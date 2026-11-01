package hu.nye.progtech.rushhour.controller;

import java.util.Scanner;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.service.GameStateService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A felhasználói interakciók kezeléséért és a játékmenet vezérléséért felelős osztály.
 */
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final BoardView boardView;
    private final MoveService moveService;
    private final HighScoreService highScoreService;
    private final GameStateService gameStateService;
    private final Scanner scanner;

    public GameController(BoardView boardView, MoveService moveService, HighScoreService highScoreService, GameStateService gameStateService) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.highScoreService = highScoreService;
        this.gameStateService = gameStateService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Elindítja és menedzseli a játék fő futási ciklusát.
     * @param initialBoard A játék indításához használt kezdeti táblaállapot.
     */
    public void runGame(Board initialBoard) {
        Board board = initialBoard;
        int moveCount = 0;

        GameStateService.LoadedState savedState = gameStateService.loadGame();
        if (savedState != null) {
            System.out.print("Mentett játékállás észlelve. Szeretné betölteni? (igen/nem): ");
            String answer = scanner.nextLine().trim();
            if ("igen".equalsIgnoreCase(answer)) {
                board = savedState.getBoard();
                moveCount = savedState.getMoveCount();
                LOGGER.info("Mentett játékállás sikeresen visszaállítva. Aktuális lépésszám: {}", moveCount);
            }
        }

        System.out.println("Üdvözöllek a Rush Hour játékban!");
        System.out.println("Parancs formátuma: [JárműBetűjele] [Lépésszám] (Pl: A 1 vagy X -2)");
        System.out.println("Rendszerparancsok: save (mentés) | exit (kilépés)");

        while (!isGameWon(board)) {
            boardView.printBoard(board);
            System.out.println("Megtett szabályos lépések száma: " + moveCount);
            System.out.print("Adja meg a következő lépést: ");

            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                LOGGER.info("Felhasználói kezdeményezésű kilépés.");
                System.out.println("Köszönöm a játékot! Viszlát!");
                return;
            }

            if ("save".equalsIgnoreCase(input)) {
                gameStateService.saveGame(board, moveCount);
                System.out.println("A játékállás sikeresen rögzítésre került.");
                continue;
            }

            try {
                String[] parts = input.split(" ");
                if (parts.length != 2) {
                    System.out.println("Érvénytelen szintaktika! Két paraméter megadása kötelező szóköz elválasztással.");
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
                System.out.println("Érvénytelen parancsformátum! A második paraméternek egész számnak kell lennie.");
            }
        }

        boardView.printBoard(board);
        LOGGER.info("A játékmenet sikeresen lezárult. Összesített lépésszám: {}", moveCount);

        System.out.println("GRATULÁLOK! A céljármű sikeresen elérte a kijáratot. Megnyerted a játékot!");
        System.out.println("A megoldáshoz szükséges szabályos lépések száma: " + moveCount);

        System.out.print("Kérlek, add meg a neved a ranglistához: ");
        String playerName = scanner.nextLine().trim();

        if (playerName.isEmpty()) {
            playerName = "Anonymous Player";
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

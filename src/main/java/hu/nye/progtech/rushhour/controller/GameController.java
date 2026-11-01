package hu.nye.progtech.rushhour.controller;

import java.util.Scanner;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játékmenet futtatásáért és a felhasználói bevitelek kezeléséért felelős Vezérlő (Controller) osztály.
 */
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final BoardView boardView;
    private final MoveService moveService;
    // ÚJ: Bevezetjük a toplista szolgáltatást a Vezérlőbe
    private final HighScoreService highScoreService;
    private final Scanner scanner;

    // Frissítjük a konstruktort, hogy a HighScoreService-t is megkapja
    public GameController(BoardView boardView, MoveService moveService, HighScoreService highScoreService) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.highScoreService = highScoreService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Elindítja a játék fő ciklusát.
     * @param initialBoard A kezdeti játéktábla, amivel a játék indul.
     */
    public void runGame(Board initialBoard) {
        Board board = initialBoard;
        int moveCount = 0;

        LOGGER.info("A játékmenet elindult a GameController segítségével.");

        System.out.println("Üdvözöllek a Rush Hour játékban!");
        System.out.println("Parancs formátuma: [AutóBetűjele] [Lépésszám] (Pl: A 1 vagy X -2)");
        System.out.println("A kilépéshez írd be: exit");

        // FŐ JÁTÉKCIKLUS (Addig fut, amíg nincs győzelem)
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

        // --- GYŐZELEM UTÁNI RÉSZ ---
        boardView.printBoard(board);
        LOGGER.info("A játék sikeresen véget ért, győzelemmel! Összes lépés: {}", moveCount);

        System.out.println("GRATULÁLOK! Sikeresen kijuttattad a céljárművet, megnyerted a játékot!");
        System.out.println("A játékot összesen " + moveCount + " sikeres lépésből oldottad meg.");

        // Bekérjük a játékos nevét a toplistához
        System.out.print("Kérlek, add meg a neved a toplistához: ");
        String playerName = scanner.nextLine().trim();

        // Ha a játékos nem adott meg nevet, kap egy alapértelmezett nevet
        if (playerName.isEmpty()) {
            playerName = "Névtelen Játékos";
        }

        // ÚJ: Ténylegesen elmentjük az eredményt a fájlba
        highScoreService.saveScore(playerName, moveCount);
        System.out.println("Köszönjük, " + playerName + "! Az eredményed elmentve.");

        // ÚJ: Azonnal kiíratjuk a friss TOP 10 ranglistát a képernyőre!
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

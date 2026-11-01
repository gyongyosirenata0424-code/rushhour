package hu.nye.progtech.rushhour.controller;

import java.util.Scanner;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
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
    private final Scanner scanner;

    public GameController(BoardView boardView, MoveService moveService) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Elindítja a játék fő ciklusát.
     * @param initialBoard A kezdeti játéktábla, amivel a játék indul.
     */
    public void runGame(Board initialBoard) {
        Board board = initialBoard;
        int moveCount = 0; // ÚJ: Ebben a változóban számoljuk a játékos sikeres lépéseit!

        LOGGER.info("A játékmenet elindult a GameController segítségével.");

        System.out.println("Üdvözöllek a Rush Hour játékban!");
        System.out.println("Parancs formátuma: [AutóBetűjele] [Lépésszám] (Pl: A 1 vagy X -2)");
        System.out.println("A kilépéshez írd be: exit");

        // FŐ JÁTÉKCIKLUS (Addig fut, amíg nincs győzelem)
        while (!isGameWon(board)) {
            // Kirajzoljuk az aktuális táblát
            boardView.printBoard(board);
            // ÚJ: Kiírjuk a játékosnak, hogy jelenleg hányadik lépésnél jár
            System.out.println("Eddigi lépések száma: " + moveCount);

            // Bekérjük a játékos lépését
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
                    System.out.println("Hibás formátum! Kérlek, két dolgot adj meg szóközzel elválaştva.");
                    continue;
                }

                char vehicleId = parts[0].toUpperCase().charAt(0);
                int steps = Integer.parseInt(parts[1]);

                // Eltároljuk a mozgás előtti táblát, hogy ellenőrizni tudjuk, változott-e valami
                Board previousBoard = board;

                // Megkíséreljük a mozgást
                board = moveService.moveVehicle(board, vehicleId, steps);

                // ÚJ ELLENŐRZÉS: Ha a visszakapott tábla NEM egyezik meg a régivel, a lépés SIKERES volt!
                if (!board.equals(previousBoard)) {
                    moveCount++; // Megnöveljük a lépésszámlálót eggyel
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

        // ÚJ: Bekérjük a játékos nevét a toplistához
        System.out.print("Kérlek, add meg a neved a toplistához: ");
        String playerName = scanner.nextLine().trim();

        System.out.println("Köszönjük, " + playerName + "! Az eredményed rögzítésre készen áll.");
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

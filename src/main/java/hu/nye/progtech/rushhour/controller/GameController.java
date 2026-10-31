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
    // A Scanner a Java gyári eszköze, amivel a billentyűzetről tudunk szöveget beolvasni
    private final Scanner scanner;

    public GameController(BoardView boardView, MoveService moveService) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.scanner = new Scanner(System.in); // System.in jelenti a szabványos bemenetet (billentyűzet)
    }

    /**
     * Elindítja a játék fő ciklusát.
     * @param initialBoard A kezdeti játéktábla, amivel a játék indul.
     */
    public void runGame(Board initialBoard) {
        Board board = initialBoard;
        LOGGER.info("A játékmenet elindult a GameController segítségével.");

        System.out.println("Üdvözöllek a Rush Hour játékban!");
        System.out.println("Parancs formátuma: [AutóBetűjele] [Lépésszám] (Pl: A 1 vagy X -2)");
        System.out.println("A kilépéshez írd be: exit");

        // FŐ JÁTÉKCIKLUS (Addig fut, amíg nincs győzelem)
        while (!isGameWon(board)) {
            // 1. Kirajzoljuk az aktuális táblát
            boardView.printBoard(board);

            // 2. Bekérjük a játékos lépését
            System.out.print("Kérlek, add meg a lépésed: ");
            String input = scanner.nextLine().trim();

            // Ha a játékos ki akar lépni
            if ("exit".equalsIgnoreCase(input)) {
                LOGGER.info("A játékos kezdeményezte a kilépést.");
                System.out.println("Köszönöm a játékot! Viszlát!");
                return;
            }

            // 3. Feldolgozzuk a beírt parancsot
            try {
                String[] parts = input.split(" ");
                if (parts.length != 2) {
                    System.out.println("Hibás formátum! Kérlek, két dolgot adj meg szóközzel elválasztva.");
                    continue;
                }

                char vehicleId = parts[0].toUpperCase().charAt(0);
                int steps = Integer.parseInt(parts[1]);

                // 4. Átadjuk a lépést a mozgató szolgáltatásnak
                board = moveService.moveVehicle(board, vehicleId, steps);

            } catch (Exception e) {
                System.out.println("Érvénytelen parancs! Győződj meg róla, hogy a második paraméter egy szám.");
            }
        }

        // Ha a ciklus véget ért, az azt jelenti, hogy az isGameWon igaz lett, vagyis GYŐZELEM!
        boardView.printBoard(board); // Kirajzoljuk az utolsó, győztes állapotot
        LOGGER.info("A játék sikeresen véget ért, győzelemmel!");
        System.out.println("GRATULÁLOK! Sikeresen kijuttattad a céljárművet, megnyerted a játékot!");
    }

    /**
     * Segédfüggvény, ami ellenőrzi, hogy teljesült-e a győzelmi feltétel.
     * A szabály szerint akkor van győzelem, ha a humán játékos ('A' autó) orra elérte a tábla jobb szélét.
     */
    private boolean isGameWon(Board board) {
        for (Vehicle v : board.getVehicles()) {
            // Csak a humán játékos 'A' jelű autóját vizsgáljuk
            if (v.getId() == 'A') {
                // Kiszámoljuk az autó jobb szélének (orrának) oszlopindexét
                // Kezdő oszlop + hosszúság (pl. ha az 1. oszlopban kezdődik és 2 hosszú, akkor az 1+2 = 3. oszlopig tart)
                int carFrontCol = v.getStartCol() + v.getLength();

                // Ha ez az érték eléri a tábla teljes oszlopszámát a kijárati sorban, akkor az autó kiment a pályáról!
                if (v.getStartRow() == board.getHumanExitRow() && carFrontCol >= board.getCols()) {
                    return true;
                }
            }
        }
        return false;
    }
}


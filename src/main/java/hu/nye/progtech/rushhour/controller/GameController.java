package hu.nye.progtech.rushhour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.observer.GameObserver;
import hu.nye.progtech.rushhour.player.AiPlayer;
import hu.nye.progtech.rushhour.player.HumanPlayer;
import hu.nye.progtech.rushhour.service.CommandHistorySingleton;
import hu.nye.progtech.rushhour.service.GameStateService;
import hu.nye.progtech.rushhour.service.HighScoreService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.view.BoardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játék fő vezérlője (Controller), amely összefogja a játékhurkot, a játékosokat és a szolgáltatásokat.
 */
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final BoardView boardView;
    private final MoveService moveService;
    private final GameStateService gameStateService;
    private final HighScoreService highScoreService;
    private final Scanner scanner;
    private final List<GameObserver> observers = new ArrayList<>();

    public GameController(BoardView boardView,
                          MoveService moveService,
                          GameStateService gameStateService,
                          HighScoreService highScoreService,
                          Scanner scanner) {
        this.boardView = boardView;
        this.moveService = moveService;
        this.gameStateService = gameStateService;
        this.highScoreService = highScoreService;
        this.scanner = scanner;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String event) {
        for (GameObserver observer : observers) {
            observer.onGameEvent(event);
        }
    }

    public void runGame(Board initialBoard) {
        System.out.print("Add meg a neved: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Jatekos";
        }

        HumanPlayer humanPlayer = new HumanPlayer(playerName, 'A', moveService);
        AiPlayer aiPlayer = new AiPlayer("Gep (AI)", 'Z', 'A', moveService, new java.util.Random());

        Board currentBoard = initialBoard;
        int moveCount = 0;

        GameStateService.LoadedState saved = gameStateService.loadGame();
        if (saved != null) {
            System.out.print("Van elmentett játékállás (" + saved.moveCount() + " lépés). Folytatod? (igen/nem): ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("igen")) {
                currentBoard = saved.board();
                moveCount = saved.moveCount();
                notifyObservers("Mentett játékállás betöltve (" + moveCount + " lépés).");
            }
        }

        notifyObservers("Játék elindult. Játékos: " + playerName);
        System.out.println("\nÜdvözlünk a kétjátékos Rush Hour játékban, " + playerName + "!");
        System.out.println("Cél: Csúsztasd ki az 'A' járművet a kijáraton, mielőtt az AI kijuttatja a 'Z'-t!");
        System.out.println("Parancsok: 'A 1' vagy 'A RIGHT 1', 'save', 'scores', 'history', 'exit'");

        while (true) {
            boardView.printBoard(currentBoard);

            List<String> possibleMoves = moveService.getAvailableMoves(currentBoard);
            System.out.println("Lehetséges lépéseid: "
                    + (possibleMoves.isEmpty() ? "Nincs elérhető lépés!" : String.join(", ", possibleMoves)));
            System.out.print("[" + (moveCount + 1) + ". kör] Lépésed: ");

            if (!scanner.hasNextLine()) {
                LOGGER.info("Bemeneti folyam vége (EOF), a játék tisztán kilép.");
                break;
            }
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                notifyObservers("Játékos kilépett a játékból.");
                System.out.println("Kilépés a játékból. Viszlát!");
                break;
            }
            if (input.equalsIgnoreCase("save")) {
                gameStateService.saveGame(currentBoard, moveCount);
                notifyObservers("Játékállás elmentve a " + moveCount + ". lépésnél.");
                System.out.println("Játékállás sikeresen elmentve!");
                continue;
            }
            if (input.equalsIgnoreCase("scores") || input.equalsIgnoreCase("toplista")) {
                highScoreService.printHighScores();
                continue;
            }
            if (input.equalsIgnoreCase("history")) {
                System.out.println("\n--- PARANCSNAPLÓ (Singleton) ---");
                CommandHistorySingleton.getInstance().getHistory().forEach(System.out::println);
                System.out.println("---------------------------------\n");
                continue;
            }

            try {
                humanPlayer.setPendingCommand(input);
                Board afterHumanMove = humanPlayer.takeTurn(currentBoard);
                currentBoard = afterHumanMove;
                moveCount++;
                notifyObservers(playerName + " lépett: " + input);

                if (moveService.hasVehicleExited(currentBoard, humanPlayer.getTargetVehicleId())) {
                    boardView.printBoard(currentBoard);
                    System.out.println("GRATULÁLOK " + playerName.toUpperCase() + "! NYERTÉL "
                            + moveCount + " lépésből!");
                    notifyObservers("GYŐZELEM! " + playerName + " nyert " + moveCount + " lépésből.");
                    highScoreService.recordWin(playerName);
                    highScoreService.printHighScores();
                    break;
                }

                currentBoard = aiPlayer.takeTurn(currentBoard);

                if (moveService.hasVehicleExited(currentBoard, aiPlayer.getTargetVehicleId())) {
                    boardView.printBoard(currentBoard);
                    System.out.println("SAJNOS VESZTETTÉL! A gép ('Z') hamarabb kijutott a pályáról!");
                    notifyObservers("VERESÉG! Az AI kijuttatta a 'Z' járművet.");
                    highScoreService.recordWin(aiPlayer.getName());
                    highScoreService.printHighScores();
                    break;
                }

            } catch (InvalidMoveException e) {
                LOGGER.warn("Szabálytalan lépési kísérlet: {}", e.getMessage());
                System.out.println("HIBA: " + e.getMessage());
            }
        }
    }
}

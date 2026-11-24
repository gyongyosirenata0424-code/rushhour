package hu.nye.progtech.rushhour.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;
import hu.nye.progtech.rushhour.service.MoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A gépi ellenfél (AI) stratégiája: véletlenszerű járművet választ,
 * kilistázza a legális lépéseit és végrehajt egyet.
 */
public class AiPlayer implements Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(AiPlayer.class);

    private final String name;
    private final char targetVehicleId;
    private final char opponentVehicleId;
    private final MoveService moveService;
    private final Random random;

    public AiPlayer(String name, char targetVehicleId, char opponentVehicleId,
                    MoveService moveService, Random random) {
        this.name = name;
        this.targetVehicleId = targetVehicleId;
        this.opponentVehicleId = opponentVehicleId;
        this.moveService = moveService;
        this.random = random;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getTargetVehicleId() {
        return targetVehicleId;
    }

    @Override
    public Board takeTurn(Board currentBoard) {
        if (moveService.isMoveValid(currentBoard, targetVehicleId, 1)) {
            try {
                Board movedTarget = moveService.moveVehicle(currentBoard, targetVehicleId, 1);
                LOGGER.info("AI ({}) előrelépett a(z) {} céljárművel.", name, targetVehicleId);
                System.out.println("\n>>> 🤖 AI LÉPÉSE: " + targetVehicleId + " jobbra (+1) <<<");
                return movedTarget;
            } catch (InvalidMoveException ignored) {
                // Nem fordul elő, mert az isMoveValid már ellenőrizte
            }
        }

        List<Vehicle> candidateVehicles = new ArrayList<>();
        for (Vehicle v : currentBoard.getVehicles()) {
            if (v.getId() != opponentVehicleId) {
                candidateVehicles.add(v);
            }
        }
        Collections.shuffle(candidateVehicles, random);

        for (Vehicle vehicle : candidateVehicles) {
            List<Integer> legalSteps = new ArrayList<>();
            for (int step = -currentBoard.getCols(); step <= currentBoard.getCols(); step++) {
                if (step != 0 && moveService.isMoveValid(currentBoard, vehicle.getId(), step)) {
                    legalSteps.add(step);
                }
            }

            if (!legalSteps.isEmpty()) {
                int chosenStep = legalSteps.get(random.nextInt(legalSteps.size()));
                try {
                    Board nextBoard = moveService.moveVehicle(currentBoard, vehicle.getId(), chosenStep);
                    String sign = chosenStep > 0 ? "+" + chosenStep : String.valueOf(chosenStep);
                    LOGGER.info("AI ({}) lépett: {} ({})", name, vehicle.getId(), sign);
                    System.out.println("\n>>> 🤖 AI LÉPÉSE: " + vehicle.getId() + " (" + sign + ") <<<");
                    return nextBoard;
                } catch (InvalidMoveException ignored) {
                    // Következő jelölt vizsgálata
                }
            }
        }

        LOGGER.info("AI ({}) nem talált végrehajtható lépést.", name);
        System.out.println("\n>>> 🤖 AI LÉPÉSE: Nem tudott lépni ebben a körben. <<<");
        return currentBoard;
    }
}

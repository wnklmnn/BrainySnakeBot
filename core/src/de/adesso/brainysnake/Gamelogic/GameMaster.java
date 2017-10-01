package de.adesso.brainysnake.Gamelogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.adesso.brainysnake.Config;
import de.adesso.brainysnake.Gamelogic.Level.GlobalGameState;
import de.adesso.brainysnake.Gamelogic.Level.Level;
import de.adesso.brainysnake.Gamelogic.Player.PlayerChoice;
import de.adesso.brainysnake.Gamelogic.Player.PlayerController;
import de.adesso.brainysnake.Gamelogic.Player.PlayerHandler;
import de.adesso.brainysnake.Gamelogic.Player.Snake;
import de.adesso.brainysnake.Gamelogic.UI.UIPlayerInformation;
import de.adesso.brainysnake.Gamelogic.Player.TestPlayer.KeyBoardPlayer;
import de.adesso.brainysnake.Gamelogic.UI.UiState;
import de.adesso.brainysnake.playercommon.*;
import de.adesso.brainysnake.playercommon.math.Point2D;
import de.adesso.brainysnake.sampleplayer.SamplePlayer;
import de.adesso.brainysnake.sampleplayer.BrainyMcBrainSnake;

import static de.adesso.brainysnake.playercommon.Orientation.*;
import static de.adesso.brainysnake.playercommon.RoundEvent.*;

public class GameMaster {

    private PlayerController playerController;

    private Level level;
    private boolean gameOver;

    public GameMaster(Level level) {
        // Create UI ?
        this.level = level;

        // Add agents to the game
        List<BrainySnakePlayer> brainySnakePlayers = new ArrayList<BrainySnakePlayer>();
        BrainySnakePlayer playerOne = new KeyBoardPlayer();
        BrainySnakePlayer playerTwo = new SamplePlayer() {

            @Override
            public String getPlayerName() {
                return "SamplePlayer Two";
            }
        };

        //brainySnakePlayers.add(playerOne);
        brainySnakePlayers.add(new BrainyMcBrainSnake());
        brainySnakePlayers.add(new BrainyMcBrainSnake());
        brainySnakePlayers.add(new BrainyMcBrainSnake());
        brainySnakePlayers.add(new BrainyMcBrainSnake());

        // Build UI Models for the agents
        Map<Orientation, Snake> brainySnakePlayersUiModel = new HashMap<Orientation, Snake>();
        brainySnakePlayersUiModel.put(UP, level.createStartingGameObject(UP, Config.INITIAL_PLAYER_LENGTH));
        brainySnakePlayersUiModel.put(DOWN, level.createStartingGameObject(DOWN, Config.INITIAL_PLAYER_LENGTH));
        brainySnakePlayersUiModel.put(RIGHT, level.createStartingGameObject(RIGHT, Config.INITIAL_PLAYER_LENGTH));
        brainySnakePlayersUiModel.put(LEFT, level.createStartingGameObject(LEFT, Config.INITIAL_PLAYER_LENGTH));

        // The PlayerController capsules agent actions an calculations
        // The Controller will randomly assign agents to GameObjects
        playerController = new PlayerController(brainySnakePlayers, brainySnakePlayersUiModel);
    }

    public void update(float delta) {
        gameLoop();
    }

    public void gameLoop() {

        GlobalGameState.countMoves++;
        UiState.getINSTANCE().setRoundsRemaining(GlobalGameState.movesRemaining());
        List<PlayerHandler> winner = getWinner();
        if (winner.size() > 0) {
            gameOver = true;
            return;
        }

        Map<PlayerHandler, PlayerChoice> playerStatus = this.playerController.getPlayerStatus();
        for (PlayerHandler playerHandler : playerStatus.keySet()) {
            PlayerChoice playerChoice = playerStatus.get(playerHandler);
            validateEvents(playerHandler, playerChoice);
        }
        playerStatus.clear();

        // check reaction to gameevents of agents
        List<PlayerHandler> deadPlayer = new ArrayList<PlayerHandler>();
        for (PlayerHandler playerHandler : playerController.getPlayerHandlerList()) {
            List<RoundEvent> roundEvents = playerHandler.getRoundEvents();
            int collectedPoints = 0;
            for (RoundEvent roundEvent : roundEvents) {
                switch (roundEvent) {
                    case DIEDED:
                        deadPlayer.add(playerHandler);
                        break;
                    case MOVED:
                        // move player as he planned
                        playerHandler.moveToNextPosition();
                        break;
                    case CONFUSED:
                        collectedPoints++;
                        break;
                    case COLLISION_WITH_LEVEL:
                        collectedPoints--;
                        break;
                    case BIT_HIMSELF:
                        collectedPoints--;
                        break;
                    case BIT_AGENT:
                        if (!playerHandler.isGhostMode()) {
                            collectedPoints++;
                        }
                        break;
                    case BIT_BY_PLAYER:
                        // agent was Hit by another agent
                        if (!playerHandler.isGhostMode()) {
                            collectedPoints--;
                        }
                        break;
                    case CONSUMED_POINT:
                        collectedPoints++;
                        break;
                }
            }

            // if no points where collected, just move the snake
            if (collectedPoints <= 0) {
                playerHandler.penalty();
                // if negative points where collected, add another penalty
                if (collectedPoints < -1) {
                    playerHandler.penalty();
                }
            }
        }

        // remove dead player from playerlist
        for (PlayerHandler dead : deadPlayer) {
            playerController.getPlayerHandlerList().remove(dead);
            UiState.getINSTANCE().rip(dead.getPlayerName());
        }

        // spread new points in level
        level.spreadPoints();

        for (PlayerHandler playerHandler : playerController.getPlayerHandlerList()) {
            // reset data an update view of player
            endRoundForPlayer(playerHandler);
        }

        // calculates the playerState and updates the playercontroller via call
        this.playerController.updatePlayerState(new GlobalGameState());
    }

    private void endRoundForPlayer(PlayerHandler playerHandler) {
        List<PlayerHandler> playerHandlerList = playerController.getPlayerHandlerList();
        List<Point2D> playerViewPositions = PlayerViewHelper.generatePlayerView(playerHandler.getCurrentOrientation(), playerHandler.getHeadPosition());
        List<Point2D> playerPositions = playerController.getPlayerPositions();
        List<Field> playerView = new ArrayList<Field>();
        for (Point2D point2D : playerViewPositions) {
            if (!level.levelContainsPosition(point2D)) {
                playerView.add(new Field(point2D, FieldType.NONE));
                continue;
            }

            if (level.checkCollision(point2D)) {
                playerView.add(new Field(point2D, FieldType.LEVEL));
            } else if (level.isPointOn(point2D)) {
                playerView.add(new Field(point2D, FieldType.POINT));
            } else if (playerPositions.contains(point2D)) {
                playerView.add(new Field(point2D, FieldType.PLAYER));
            } else {
                playerView.add(new Field(point2D, FieldType.EMPTY));
            }

        }

        playerHandler.updatePlayerView(new PlayerView(playerView, playerHandler.getCurrentOrientation()));
        playerHandler.endround();
        playerHandler.update();
        UiState.getINSTANCE().updatePlayerPoints(playerHandler.getPlayerName(), new UIPlayerInformation(playerHandler.getSnake().getHeadColor(), playerHandler.getSnake().countPoints()));
    }

    public List<PlayerHandler> getWinner() {
        List<PlayerHandler> winner = new ArrayList<PlayerHandler>();
        if (GlobalGameState.movesRemaining() <= 0) {

            int maxPoints =-1;
            for (PlayerHandler playerHandler : playerController.getPlayerHandlerList()) {
                maxPoints = Math.max(maxPoints, playerHandler.getSnake().countPoints());
            }

            for (PlayerHandler playerHandler : playerController.getPlayerHandlerList()) {
                if (playerHandler.getSnake().countPoints() == maxPoints) {
                    winner.add(playerHandler);
                }
            }
        } else if (playerController.getPlayerHandlerList().size() <= 1) {
            winner.add(playerController.getPlayerHandlerList().get(0));
        }

        return winner;
    }

    private void validateEvents(PlayerHandler playerHandler, PlayerChoice playerChoice) {
        List<RoundEvent> roundEvents = playerHandler.getRoundEvents();

        if (playerHandler.isDead() || playerHandler.getSnake().countPoints() <= 1) {
            roundEvents.add(DIEDED);
            playerHandler.kill();
            return;
        }

        if (!playerChoice.isHasChosen() || !playerHandler.isOrientationValid(playerChoice.getOrientation())) {
            roundEvents.add(CONFUSED);
            playerHandler.setConfused(true);
            return;
        }

        Point2D nextPosition = playerHandler.getNextPositionBy(playerChoice.getOrientation());
        if (level.checkCollision(nextPosition)) {
            roundEvents.add(COLLISION_WITH_LEVEL);
            playerHandler.setConfused(true);
            return;
        }

        playerHandler.setCurrentOrientation(playerChoice.getOrientation());
        roundEvents.add(MOVED);
        playerHandler.setConfused(false);

        // did the player bit any snake object
        for (PlayerHandler player : playerController.getPlayerHandlerList()) {
            if (!playerHandler.isGhostMode() && player.gotBitten(nextPosition)) {
                if (player.equals(playerHandler)) {
                    roundEvents.add(BIT_HIMSELF);
                } else {
                    roundEvents.add(BIT_AGENT);
                    player.getRoundEvents().add(BIT_BY_PLAYER);
                }
                playerHandler.setGhostMode();
            }
        }

        if (!playerHandler.isGhostMode() && level.tryConsumePoint(nextPosition)) {
            roundEvents.add(CONSUMED_POINT);
        }
    }

    public List<PlayerHandler> getPlayerHandler() {
        return playerController.getPlayerHandlerList();
    }

    public void shutdown() {
        this.playerController.shutdown();
    }

    public boolean isGameOver() {
        return gameOver;
    }
}

package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PlayerMenu extends GameMenu {
    private static PlayerMenu playerMenuInstance = null;

    public static PlayerMenu getPlayerMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        if (
                playerMenuInstance == null ||
                !playerMenuInstance.authToken.equals(authToken) ||
                playerMenuInstance.gameID != gameID ||
                !playerMenuInstance.teamColor.equals(teamColor)
        ) {
            playerMenuInstance = new PlayerMenu(
                    engine,
                    serverFacade,
                    webSocketFacade,
                    authToken,
                    gameID,
                    teamColor
            );
        }

        return playerMenuInstance;
    }

    PlayerMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade, webSocketFacade, authToken, gameID, teamColor);
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "draw" -> draw();
            case "exit" -> exit();
            default -> help();
        };
    }

    @Override
    void printPrompt() {
        System.out.printf(
                "\n%s[IN GAME] >>>%s ",
                RESET_TEXT_COLOR,
                SET_TEXT_COLOR_GREEN
        );
    }

    @Override
    State help() {
        notify(
        """
             
             USAGE:
             - draw - Redraw board
             - exit - Exit current game
             - help - Show available commands
             """
        );

        return State.IN_GAME;
    }

    @Override
    void deleteMenuInstance() {
        playerMenuInstance = null;
    }
}

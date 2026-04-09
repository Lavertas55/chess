package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class ObservingMenu extends GameMenu {
    private static ObservingMenu observingMenuInstance = null;

    public static ObservingMenu getObservingMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID
    ) {
        if (
                observingMenuInstance == null ||
                !observingMenuInstance.authToken.equals(authToken) ||
                observingMenuInstance.gameID != gameID
        ) {
            observingMenuInstance = new ObservingMenu(engine, serverFacade, webSocketFacade, authToken, gameID);
        }

        return observingMenuInstance;
    }

    private ObservingMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID
    ) {
        super(engine, serverFacade, webSocketFacade, authToken, gameID, ChessGame.TeamColor.WHITE);
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "draw" -> draw();
            case "highlight" -> highlightMoves(params);
            case "exit" -> exit();
            default -> help();
        };
    }

    @Override
    void printPrompt() {
        System.out.printf(
                "\n%s[OBSERVING] >>>%s ",
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
             - highlight <PIECE POSITION> - Highlight possible moves for a specific piece
             - exit - Exit current game
             - help - Show available commands
             """
        );

        return State.OBSERVING;
    }

    @Override
    void deleteMenuInstance() {
        observingMenuInstance = null;
    }
}

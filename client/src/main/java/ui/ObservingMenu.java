package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import exception.ResponseException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class ObservingMenu extends GameMenu {
    public ObservingMenu(UIEngine engine, ServerFacade serverFacade, String authToken, ChessGame game) {
        super(engine, serverFacade, authToken, game, ChessGame.TeamColor.WHITE);
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
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
             - exit - Exit current game
             - help - Show available commands
             """
        );

        return State.OBSERVING;
    }
}

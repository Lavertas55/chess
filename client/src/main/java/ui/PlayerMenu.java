package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import exception.ResponseException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PlayerMenu extends GameMenu {
    public PlayerMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            String authToken,
            ChessGame game,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade, authToken, game, teamColor);
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "exit" -> exit();
            case "quit" -> quit();
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
             - exit
             - quit
             - help
             """
        );

        return State.OBSERVING;
    }
}

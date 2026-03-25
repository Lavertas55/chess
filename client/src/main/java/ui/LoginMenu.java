package ui;

import client.ServerFacade;
import client.State;
import client.UIEngine;
import exception.ResponseException;
import response.LoginResponse;
import response.RegisterResponse;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class LoginMenu extends UIMenu {

    public LoginMenu(UIEngine engine, ServerFacade serverFacade) {
        super(engine, serverFacade);
    }

    @Override
    void printPrompt() {
        System.out.printf(
                "\n%s[SIGNED OUT] >>>%s ",
                RESET_TEXT_COLOR,
                SET_TEXT_COLOR_GREEN
        );
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "help" -> help();
            case "quit" -> quit();
            default -> help();
        };
    }

    private State register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterResponse response = serverFacade.register(username, password, email);
            engine.setAuthToken(response.authToken());

            notify(String.format("Successfully logged in as %s\n", username));
            return State.SIGNED_IN;
        }
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private State login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            LoginResponse response = serverFacade.login(username, password);
            engine.setAuthToken(response.authToken());

            notify(String.format("Successfully logged in as %s\n", username));
            return State.SIGNED_IN;
        }
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <USERNAME> <PASSWORD>");
    }

    @Override
    State help() {
        notify(
        """
             
             USAGE:
             - register <USERNAME> <PASSWORD> <EMAIL>
             - login <USERNAME> <PASSWORD>
             - help
             - quit
             """
        );

        return State.SIGNED_OUT;
    }
}

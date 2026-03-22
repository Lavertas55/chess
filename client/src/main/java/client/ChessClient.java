package client;

import exception.ResponseException;
import response.LoginResponse;
import response.RegisterResponse;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private String authToken = null;
    private State state = State.SIGNED_OUT;

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess ♕");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            }
            catch (ResponseException ex) {
                var msg = ex.getMessage();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private String eval(String input) throws ResponseException {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private void printPrompt() {
        System.out.printf(
                "\n%s[%s] >>>%s ",
                EscapeSequences.RESET_TEXT_COLOR,
                state,
                EscapeSequences.SET_TEXT_COLOR_GREEN
        );
    }

    private String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterResponse response = serverFacade.register(username, password, email);
            authToken = response.authToken();
            state = State.SIGNED_IN;

            return String.format("Successfully logged in as %s", username);
        }
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            throw new ResponseException(ResponseException.Code.FORBIDDEN, "You must logout first");
        }
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            LoginResponse response = serverFacade.login(username, password);
            authToken = response.authToken();
            state = State.SIGNED_IN;

            return String.format("Successfully logged in as %s", username);
        }
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <USERNAME> <PASSWORD>");
    }

    private String logout() throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            serverFacade.logout(authToken);
            authToken = null;
            state = State.SIGNED_OUT;

            return "Successfully logged out";
        }

        throw new ResponseException(ResponseException.Code.FORBIDDEN, "You must be logged in first");
    }



    private String help() {
        if (state.equals(State.SIGNED_OUT)) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - help
                    - quit""";
        }

        return """
                - create <NAME>
                - list
                - join <ID> [WHITE|BLACK]
                - observe <ID>
                - logout
                - help
                - quit""";
    }
}

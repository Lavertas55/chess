package client;

import chess.ChessGame;
import exception.ResponseException;
import response.GameResponse;
import response.ListGamesResponse;
import response.LoginResponse;
import response.RegisterResponse;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private String authToken = null;
    private State state = State.SIGNED_OUT;
    private final HashMap<Integer, Integer> games = new HashMap<>();

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess ♕");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("Quiting...")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);

                if (result.toLowerCase().contains("error")) {
                    alert(result);
                }
                else {
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
                }
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
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "quit" -> quit();
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
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Error: Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            throw new ResponseException(ResponseException.Code.FORBIDDEN, "Error: You must logout first");
        }
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            LoginResponse response = serverFacade.login(username, password);
            authToken = response.authToken();
            state = State.SIGNED_IN;

            return String.format("Successfully logged in as %s", username);
        }
        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Error: Expected: <USERNAME> <PASSWORD>");
    }

    private String logout() throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            serverFacade.logout(authToken);
            authToken = null;
            state = State.SIGNED_OUT;

            return "Successfully logged out";
        }

        throw new ResponseException(ResponseException.Code.FORBIDDEN, "Error: You must be logged in first");
    }

    private String create(String... params) throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            if (params.length == 1) {
                String name = params[0];
                serverFacade.createGame(name, authToken);

                return String.format("Successfully created game: %s", name);
            }

            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <NAME>");
        }

        throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Error: You must be logged in first");
    }

    private String list() throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            ListGamesResponse response = serverFacade.listGames(authToken);
            StringBuilder result = new StringBuilder("Games:");

            games.clear();
            int index = 1;
            for (GameResponse game : response.games()) {
                games.put(index, game.gameID());
                result.append(String.format(
                        "\n%d - Name: %s | White: %s | Black: %s",
                        index,
                        game.gameName(),
                        game.whiteUsername(),
                        game.blackUsername()
                ));

                index++;
            }

            return result.toString();
        }

        throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Error: You must be logged in first");
    }

    private String join(String... params) throws ResponseException {
        if (state.equals(State.SIGNED_IN)) {
            int mapGameID = Integer.parseInt(params[0]);
            int gameID;
            if (!games.containsKey(mapGameID)) {
                throw new ResponseException(
                        ResponseException.Code.NOT_FOUND,
                        String.format("Error: Game %d does not exist: Use list to see games", mapGameID)
                );
            }
            gameID = games.get(mapGameID);

            String teamColor = params[1];
            if (teamColor.equals("white")) {
                serverFacade.joinGame(authToken, ChessGame.TeamColor.WHITE, gameID);
            }
            else if (teamColor.equals("black")) {
                serverFacade.joinGame(authToken, ChessGame.TeamColor.BLACK, gameID);
            }
            else {
                throw new ResponseException(
                        ResponseException.Code.BAD_REQUEST,
                        "Error: Please select team WHITE or BLACK"
                );
            }

            return String.format("Successfully joined game: %d as %s", mapGameID, teamColor.toUpperCase());
        }

        throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Error: You must be logged in first");
    }

    private String quit() {
        if (state.equals(State.SIGNED_IN)) {
            try {
                serverFacade.logout(authToken);
            }
            catch (ResponseException ex) {
                alert("Error: Failed to logout");
                System.out.println();
            }
        }

        return "Quiting...";
    }

    private void alert(String msg) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + msg);
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

package client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import response.GameResponse;
import response.ListGamesResponse;
import response.LoginResponse;
import response.RegisterResponse;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private String authToken = null;
    private State state = State.SIGNED_OUT;

    private final HashMap<Integer, Integer> games = new HashMap<>();

    private static final int BOARD_SIZE_IN_CELLS = 10;
    private static final int CELL_SIZE = 3;
    private static final String EMPTY = " ";

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess ♕");
        System.out.println(SET_TEXT_COLOR_BLUE + help());

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
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
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
                case "observe" -> observe(params);
                case "quit" -> quit();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    private void printPrompt() {
        System.out.printf(
                "\n%s[%s] >>>%s ",
                RESET_TEXT_COLOR,
                state,
                SET_TEXT_COLOR_GREEN
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
        loginRequired();

        serverFacade.logout(authToken);
        authToken = null;
        state = State.SIGNED_OUT;

        return "Successfully logged out";
    }

    private String create(String... params) throws ResponseException {
        loginRequired();

        if (params.length == 1) {
            String name = params[0];
            serverFacade.createGame(name, authToken);

            return String.format("Successfully created game: %s", name);
        }

        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <NAME>");
    }

    private String list() throws ResponseException {
        loginRequired();

        ListGamesResponse response = serverFacade.listGames(authToken);
        StringBuilder result = new StringBuilder("Games:");

        games.clear();
        int index = 1;
        for (GameResponse game : response.games()) {
            games.put(index, game.gameID());
            result.append(String.format(
                    "\nID: %d | Name: %s | White: %s | Black: %s",
                    index,
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername()
            ));

            index++;
        }

        return result.toString();
    }

    private String join(String... params) throws ResponseException {
        loginRequired();

        if (params.length == 2) {
            int mapGameID;
            try {
                mapGameID = Integer.parseInt(params[0]);
            }
            catch (NumberFormatException ex) {
                throw new ResponseException(
                        ResponseException.Code.BAD_REQUEST,
                        "ID must be a valid game ID: Use list to see available games"
                );
            }

            int gameID;
            if (!games.containsKey(mapGameID)) {
                throw new ResponseException(
                        ResponseException.Code.NOT_FOUND,
                        String.format("Game %d does not exist: Use list to see available games", mapGameID)
                );
            }
            gameID = games.get(mapGameID);

            String teamColorString = params[1];
            ChessGame.TeamColor teamColor;

            if (teamColorString.equals("white")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (teamColorString.equals("black")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException(
                        ResponseException.Code.BAD_REQUEST,
                        "Please select team WHITE or BLACK"
                );
            }

            serverFacade.joinGame(authToken, teamColor, gameID);

            drawBoard(System.out, new ChessBoard(), teamColor);
            return String.format("\nSuccessfully joined game: %d as %s", mapGameID, teamColorString.toUpperCase());
        }

        throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "You must be logged in first");
    }

    private String observe(String... params) throws ResponseException {
        loginRequired();

        if (params.length == 1) {
            int mapGameID;
            try {
                mapGameID = Integer.parseInt(params[0]);
            }
            catch (NumberFormatException ex) {
                throw new ResponseException(
                        ResponseException.Code.BAD_REQUEST,
                        "ID must be a valid game ID: Use list to see available games"
                );
            }

            drawBoard(System.out, new ChessBoard(), ChessGame.TeamColor.WHITE);
            return String.format("\nSuccessfully observing game: %d", mapGameID);
        }

        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <ID>");
    }

    private void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor teamColor) {
        drawRankHeaders(out, teamColor);
        setBlack(out);
    }

    private void drawRankHeaders(PrintStream out, ChessGame.TeamColor teamColor) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);

        String[] ranks = { "a", "b", "c", "d", "e", "f", "g", "h" };
        int boardColumn;
        int step;

        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            boardColumn = 0;
            step = 1;
        }
        else {
            boardColumn = 9;
            step = -1;
        }

        while (boardColumn < BOARD_SIZE_IN_CELLS && boardColumn >= 0) {
            if (boardColumn > 0 && boardColumn < 9) {
                drawHeader(out, ranks[boardColumn - 1]);
            }
            else {
                out.print(EMPTY.repeat(CELL_SIZE));
            }

            boardColumn += step;
        }
    }

    private void drawHeader(PrintStream out, String header) {
        int prefixLength = CELL_SIZE / 2;
        int suffixLength = CELL_SIZE - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        out.print(header);
        out.print(EMPTY.repeat(suffixLength));
    }

    private void setBlack(PrintStream out) {
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
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

    private void loginRequired() throws ResponseException {
        if (state.equals(State.SIGNED_OUT)) {
            throw new ResponseException(
                    ResponseException.Code.UNAUTHORIZED,
                    "You must be logged in first"
            );
        }
    }

    private void alert(String msg) {
        System.out.print(SET_TEXT_COLOR_RED + msg);
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

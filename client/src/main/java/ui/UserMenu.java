package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import exception.ResponseException;
import response.GameResponse;
import response.ListGamesResponse;

import java.util.HashMap;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class UserMenu extends UIMenu {
    private final String authToken;
    private final HashMap<Integer, Integer> games;

    public UserMenu(UIEngine engine, ServerFacade serverFacade, String authToken, HashMap<Integer, Integer> games) {
        super(engine, serverFacade);
        this.authToken = authToken;
        this.games = games == null ? new HashMap<>() : games;
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> help();
            case "quit" -> quit();
            default -> null;
        };
    }

    @Override
    void printPrompt() {
        System.out.printf(
            "\n%s[SIGNED IN] >>>%s ",
            RESET_TEXT_COLOR,
            SET_TEXT_COLOR_GREEN
        );
    }

    private State create(String... params) throws ResponseException {
        if (params.length == 1) {
            String name = params[0];
            serverFacade.createGame(name, authToken);

            notify(String.format("Successfully created game: %s", name));
            return State.SIGNED_IN;
        }

        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <NAME>");
    }

    private State list() throws ResponseException {
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

        engine.setGames(games);

        notify(result + "\n");
        return State.SIGNED_IN;
    }

    private State join(String... params) throws ResponseException {
        if (params.length == 2) {
            int mapGameID;
            try {
                mapGameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException ex) {
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
            engine.setTeamColor(teamColor);

            notify(String.format("\nSuccessfully joined game: %d as %s\n", mapGameID, teamColorString.toUpperCase()));
            return State.IN_GAME;
        }

        throw new ResponseException(
                ResponseException.Code.BAD_REQUEST,
                "Expected: <ID> [WHITE|BLACK]"
        );
    }

    private State observe(String... params) throws ResponseException {
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

            if (!games.containsKey(mapGameID)) {
                throw new ResponseException(
                        ResponseException.Code.NOT_FOUND,
                        String.format("Game %d does not exist: Use list to see available games", mapGameID)
                );
            }

            notify(String.format("\nSuccessfully observing game: %d\n", mapGameID));
            return State.OBSERVING;
        }

        throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <ID>");
    }

    private State logout() throws ResponseException {
        serverFacade.logout(authToken);
        engine.setAuthToken(null);

        notify("Successfully logged out");
        return State.SIGNED_OUT;
    }

    @Override
    State help() {
        notify(
        """
             
             USAGE:
             - create <NAME>
             - list
             - join <ID> [WHITE|BLACK]
             - observe <ID>
             - logout
             - help
             - quit
             """
        );

        return State.SIGNED_IN;
    }
}

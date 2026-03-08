package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exception.DataException;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.GameResponse;
import response.ListGamesResponse;
import response.LoginResponse;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final AuthService authService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clearData)
                .post("/user", this::registerUser)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler);
        authService = new AuthService(new MySQLAuthDAO());
        userService = new UserService(new MySQLUserDAO());
        gameService = new GameService(new MySQLGameDAO());

        try {
            DatabaseManager.createDatabase();
        }
        catch (DataException e) {
            throw new RuntimeException(String.format("failed to start database: %s", e.getMessage()));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearData(Context ctx) throws ResponseException {
        authService.clearAuth();
        gameService.clearGames();
        userService.clearUsers();
    }

    private void registerUser(Context ctx) throws ResponseException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        int userID = userService.register(registerRequest);

        AuthData authData = authService.generateSession(userID);

        LoginResponse loginResponse = new LoginResponse(registerRequest.username(), authData.authToken());

        ctx.result(loginResponse.toJson());
    }

    private void login(Context ctx) throws ResponseException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);

        userService.verifyPassword(loginRequest.username(), loginRequest.password());
        int userID = userService.getUserID(loginRequest.username());

        AuthData authData = authService.generateSession(userID);

        LoginResponse loginResponse = new LoginResponse(loginRequest.username(), authData.authToken());

        ctx.result(loginResponse.toJson());
    }

    private void logout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");

        authService.closeSession(authToken);
    }

    private void listGames(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");

        authService.verifySession(authToken);

        Collection<GameData> games = gameService.listGames();
        ListGamesResponse listGamesResponse = convertGameData(games);

        String body = new Gson().toJson(
                Map.of("games", listGamesResponse.games())
        );

        ctx.result(body);
    }

    private ListGamesResponse convertGameData(Collection<GameData> games) throws ResponseException {
        Collection<GameResponse> convertedGames = new HashSet<>();

        for (GameData game : games) {
            String whiteUsername = game.whiteUserID() != null ? userService.getUsername(game.whiteUserID()) : null;
            String blackUsername = game.blackUserID() != null ? userService.getUsername(game.blackUserID()) : null;

            GameResponse convertedGame = new GameResponse(
                    game.gameID(),
                    whiteUsername,
                    blackUsername,
                    game.gameName(),
                    game.gameString()
            );

            convertedGames.add(convertedGame);
        }

        return new ListGamesResponse(convertedGames);
    }

    private void createGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");

        authService.verifySession(authToken);

        CreateGameRequest createGameRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        int gameID = gameService.createGame(createGameRequest.gameName());

        String body = new Gson().toJson(
                Map.of("gameID", gameID)
        );

        ctx.result(body);
    }

    private void joinGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");

        authService.verifySession(authToken);

        JoinGameRequest joinGameRequest = new Gson().fromJson(ctx.body(), JoinGameRequest.class);

        int userID = authService.getUserID(authToken);

        gameService.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), userID);
    }

    private void exceptionHandler(ResponseException e, Context ctx) {
        String body = new Gson().toJson(
                Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)
        );

        ctx.status(e.toHttpStatusCode());
        ctx.json(body);
    }
}

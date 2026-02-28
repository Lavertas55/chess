package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import service.AuthService;
import service.GameService;
import service.UserService;

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
        authService = new AuthService(new MemoryAuthDAO());
        userService = new UserService(new MemoryUserDAO());
        gameService = new GameService(new MemoryGameDAO());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearData(Context ctx) {
        authService.clearAuth();
        userService.clearUsers();
        gameService.clearGames();
    }

    private void registerUser(Context ctx) throws ResponseException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        String username = userService.register(userData);

        AuthData authData = authService.generateSession(username);

        ctx.result(authData.toJson());
    }

    private void login(Context ctx) throws ResponseException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);

        userService.login(loginRequest.username(), loginRequest.password());

        AuthData authData = authService.generateSession(loginRequest.username());

        ctx.result(authData.toJson());
    }

    private void logout(Context ctx) {
        throw new RuntimeException("not implemented");
    }

    private void listGames(Context ctx) {
        throw new RuntimeException("not implemented");
    }

    private void createGame(Context ctx) {
        throw new RuntimeException("not implemented");
    }

    private void joinGame(Context ctx) {
        throw new RuntimeException("not implemented");
    }

    private void exceptionHandler(ResponseException e, Context ctx) {
        String body = new Gson().toJson(
                Map.of("message", String.format("Error: %s", e.getMessage()), "success", false)
        );

        ctx.status(e.toHttpStatusCode());
        ctx.json(body);
    }
}

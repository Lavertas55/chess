package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import response.ListGamesResponse;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static GameService gameService;
    private static GameDAO gameDAO;
    private static GameData validGame;

    @BeforeAll
    static void init() {
        String whiteUsername = null;
        String blackUsername = null;
        String gameName = "pretty cool game";
        String gameString = new ChessGame().getBoard().toJson();

        validGame = new GameData(1, whiteUsername, blackUsername, gameName, gameString);
    }

    @BeforeEach
    void setUp() {
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO);
    }

    @Test
    void createGameValid() throws ResponseException, DataException {
        int gameID = gameService.createGame(validGame.gameName());

        assertEquals(validGame.gameID(), gameID);
        assertEquals(validGame, gameDAO.getGame(validGame.gameID()));
    }

    @Test
    void createGameNull() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> gameService.createGame(null)
        );

        assertEquals(ResponseException.Code.BAD_REQUEST, exception.getCode());
    }

    @Test
    void listGames() throws DataException {
        gameDAO.createGame(validGame.gameName());

        ListGamesResponse listGamesResponse = gameService.listGames();
        HashSet<GameData> validGameSet = new HashSet<>(Set.of(validGame));
        assertEquals(validGameSet, listGamesResponse.games());
    }

    @Test
    void joinGameValid() throws ResponseException, DataException {
        gameDAO.createGame(validGame.gameName());

        String newUsername = "new user";
        gameService.joinGame(validGame.gameID(), ChessGame.TeamColor.WHITE, newUsername);
        assertEquals(newUsername, gameDAO.getGame(validGame.gameID()).whiteUsername());
    }

    @ParameterizedTest
    @EnumSource(ChessGame.TeamColor.class)
    void joinGameAlreadyTaken(ChessGame.TeamColor color) throws DataException {
        gameDAO.createGame(validGame.gameName());

        gameDAO.updateGameUser(validGame.gameID(), color, "color taken");

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> gameService.joinGame(validGame.gameID(), color, "new user")
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }
}
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static GameService gameService;
    private static GameDAO gameDAO;
    private static GameData validGame;

    @BeforeAll
    static void init() {
        Integer whiteUserID = null;
        Integer blackUserID = null;
        String gameName = "pretty cool game";
        String gameString = new ChessGame().getBoard().toJson();

        validGame = new GameData(1, whiteUserID, blackUserID, gameName, gameString);
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
    void listGames() throws ResponseException, DataException {
        gameDAO.createGame(validGame.gameName());

        Collection<GameData> games = gameService.listGames();
        HashSet<GameData> validGameSet = new HashSet<>(Set.of(validGame));
        assertEquals(validGameSet, games);
    }

    @Test
    void joinGameValid() throws ResponseException, DataException {
        gameDAO.createGame(validGame.gameName());

        Integer newUserID = 1;
        gameService.joinGame(validGame.gameID(), ChessGame.TeamColor.WHITE, newUserID);
        assertEquals(newUserID, gameDAO.getGame(validGame.gameID()).whiteUserID());
    }

    @ParameterizedTest
    @EnumSource(ChessGame.TeamColor.class)
    void joinGameAlreadyTaken(ChessGame.TeamColor color) throws DataException {
        gameDAO.createGame(validGame.gameName());

        gameDAO.updateGameUser(validGame.gameID(), color, 1);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> gameService.joinGame(validGame.gameID(), color, 2)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }
}
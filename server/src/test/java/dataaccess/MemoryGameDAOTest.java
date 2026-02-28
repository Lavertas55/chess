package dataaccess;

import chess.ChessGame;
import dataaccess.exception.*;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryGameDAOTest {

    private static MemoryGameDAO memoryDAO;
    private static GameData validGame;

    @BeforeAll
    static void init() {
        memoryDAO = new MemoryGameDAO();

        String whiteUsername = null;
        String blackUsername = null;
        String gameName = "game 1";
        String gameString = new ChessGame().getBoard().toJson();

        validGame = new GameData(1, whiteUsername, blackUsername, gameName, gameString);
    }

    @BeforeEach
    void setup() {
        memoryDAO = new MemoryGameDAO();
    }

    @Test
    void testCreateGame() throws DataException {
        memoryDAO.createGame(validGame.gameName());

        assertThrows(BadDataException.class, () -> memoryDAO.createGame(null));
    }

    @Test
    void testGetGame() throws DataException {
        GameData gameData = memoryDAO.createGame(validGame.gameName());

        assertEquals(validGame, memoryDAO.getGame(gameData.gameID()));

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getGame(0));
    }

    @Test
    void testListGames() throws DataException {
        GameData gameData = memoryDAO.createGame(validGame.gameName());

        Collection<GameData> gameList = new HashSet<>(Set.of(gameData));
        assertEquals(gameList, memoryDAO.listGames());
    }

    @ParameterizedTest
    @EnumSource(ChessGame.TeamColor.class)
    void testUpdateUser(ChessGame.TeamColor teamColor) throws DataException {
        GameData gameData = memoryDAO.createGame(validGame.gameName());

        String newUsername = "new username";
        memoryDAO.updateGameUser(gameData.gameID(), teamColor, newUsername);

        GameData game = memoryDAO.getGame(gameData.gameID());

        switch (teamColor) {
            case WHITE -> assertEquals(newUsername, game.whiteUsername());
            case BLACK -> assertEquals(newUsername, game.blackUsername());
        }

        assertThrows(DataNotFoundException.class, () -> memoryDAO.updateGameUser(0, teamColor, newUsername));
    }

    @Test
    void testUpdateGameString() throws DataException {
        GameData gameData = memoryDAO.createGame(validGame.gameName());

        String newGameString = "new game string";
        memoryDAO.updateGameString(gameData.gameID(), newGameString);

        GameData game = memoryDAO.getGame(gameData.gameID());
        assertEquals(newGameString, game.gameString());

        assertThrows(DataNotFoundException.class, () -> memoryDAO.updateGameString(0, newGameString));
    }

    @Test
    void testClear() throws DataException {
        memoryDAO.createGame(validGame.gameName());

        memoryDAO.clear();

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getGame(validGame.gameID()));
    }
}
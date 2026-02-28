package dataaccess;

import dataaccess.exception.*;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        memoryDAO.createGame(validGame);

        assertEquals(validGame, memoryDAO.getGame(0));

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getGame(1));
    }

    @Test
    void testListGames() throws DataException {
        memoryDAO.createGame(validGame);

        Collection<GameData> gameList = new HashSet<>(Set.of(validGame));
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
        memoryDAO.createGame(validGame);

        String newGameString = "new game string";
        memoryDAO.updateGameString(0, newGameString);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newGameString, game.gameString());

        assertThrows(DataNotFoundException.class, () -> memoryDAO.updateGameString(1, newGameString));
    }

    @Test
    void testClear() throws DataException {
        memoryDAO.createGame(validGame);

        memoryDAO.clear();

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getGame(validGame.gameID()));
    }
}
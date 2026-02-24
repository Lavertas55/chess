package dataaccess;

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

        String whiteUsername = "white user 0";
        String blackUsername = "black user 0";
        String gameName = "game 0";
        String gameString = "Game Info";

        validGame = new GameData(0, whiteUsername, blackUsername, gameName, gameString);
    }

    @BeforeEach
    void setup() {
        memoryDAO.clear();
    }

    @Test
    void testCreateGame() throws DataAccessException {
        memoryDAO.createGame(validGame);

        assertThrows(DataAccessException.class, () -> memoryDAO.createGame(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.createGame(validGame));
    }

    @Test
    void testGetGame() throws DataAccessException {
        memoryDAO.createGame(validGame);

        assertEquals(validGame, memoryDAO.getGame(0));
        assertThrows(DataAccessException.class, () -> memoryDAO.getGame(1));
    }

    @Test
    void testListGames() throws DataAccessException {
        memoryDAO.createGame(validGame);

        Collection<GameData> gameList = new HashSet<>(Set.of(validGame));
        assertEquals(gameList, memoryDAO.listGames());
    }

    @Test
    void testUpdateWhiteUser() throws DataAccessException {
        memoryDAO.createGame(validGame);

        String newUsername = "new username";
        memoryDAO.updateGameWhiteUser(0, newUsername);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newUsername, game.whiteUsername());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameWhiteUser(1, newUsername));
    }

    @Test
    void testUpdateBlackUser() throws DataAccessException {
        memoryDAO.createGame(validGame);

        String newUsername = "new username";
        memoryDAO.updateGameBlackUser(0, newUsername);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newUsername, game.blackUsername());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameBlackUser(1, newUsername));
    }

    @Test
    void testUpdateGameString() throws DataAccessException {
        memoryDAO.createGame(validGame);

        String newGameString = "new game string";
        memoryDAO.updateGameString(0, newGameString);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newGameString, game.gameString());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameString(1, newGameString));
    }

    @Test
    void testClear() throws DataAccessException {
        memoryDAO.createGame(validGame);

        memoryDAO.clear();

        assertThrows(DataAccessException.class, () -> memoryDAO.getGame(validGame.gameID()));
    }
}
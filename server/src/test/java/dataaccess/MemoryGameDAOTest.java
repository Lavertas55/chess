package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryGameDAOTest {

    private static Map<Integer, GameData> gameStorage;
    private static MemoryGameDAO memoryDAO;
    private static GameData validGame;

    @BeforeAll
    static void init() {
        gameStorage = new HashMap<>();
        memoryDAO = new MemoryGameDAO(gameStorage);

        String whiteUsername = "white user 0";
        String blackUsername = "black user 0";
        String gameName = "game 0";
        String gameString = "Game Info";

        validGame = new GameData(0, whiteUsername, blackUsername, gameName, gameString);
    }

    @BeforeEach
    void setup() {
        gameStorage.clear();
        gameStorage.put(validGame.gameID(), validGame);
    }

    @Test
    void testCreateGame() throws DataAccessException {
        assertEquals(1, gameStorage.size());

        int gameID = 1;
        String whiteUsername = "White User";
        String blackUsername = "Black User";
        String gameName = "Test Game";
        String gameString = "Game Info";

        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, gameString);
        memoryDAO.createGame(newGame);

        assertEquals(2, gameStorage.size());
        assertEquals(newGame, gameStorage.get(gameID));

        assertThrows(DataAccessException.class, () -> memoryDAO.createGame(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.createGame(newGame));
    }

    @Test
    void testGetGame() throws DataAccessException {
        assertEquals(validGame, memoryDAO.getGame(0));
        assertThrows(DataAccessException.class, () -> memoryDAO.getGame(1));
    }

    @Test
    void testListGames() {
        Collection<GameData> gameList = new HashSet<>(Set.of(validGame));
        assertEquals(gameList, memoryDAO.listGames());
    }

    @Test
    void testUpdateWhiteUser() throws DataAccessException {
        String newUsername = "new username";
        memoryDAO.updateGameWhiteUser(0, newUsername);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newUsername, game.whiteUsername());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameWhiteUser(1, newUsername));
    }

    @Test
    void testUpdateBlackUser() throws DataAccessException {
        String newUsername = "new username";
        memoryDAO.updateGameBlackUser(0, newUsername);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newUsername, game.blackUsername());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameBlackUser(1, newUsername));
    }

    @Test
    void testUpdateGameString() throws DataAccessException {
        String newGameString = "new game string";
        memoryDAO.updateGameString(0, newGameString);

        GameData game = memoryDAO.getGame(0);
        assertEquals(newGameString, game.gameString());

        assertThrows(DataAccessException.class, () -> memoryDAO.updateGameString(1, newGameString));
    }

    @Test
    void testClear() {
        assertEquals(1, gameStorage.size());
        memoryDAO.clear();
        assertEquals(0, gameStorage.size());
    }
}
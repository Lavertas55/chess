package dataaccess;

import chess.ChessGame;
import dataaccess.exception.*;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private static GameData validGame;
    private static int userID;

    private static final String USERNAME = "user";
    private static final String PASSWORD = "1234";
    private static final String EMAIL = "user@gmail.com";

    private GameDAO getGameDAO(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO;
        if (dbClass.equals(MySQLGameDAO.class)) {
            DatabaseManager.createDatabase();
            addUsers();
            gameDAO = new MySQLGameDAO();
        }
        else {
            gameDAO = new MemoryGameDAO();
        }
        gameDAO.clear();
        return gameDAO;
    }

    private void addUsers() throws DataException {
        String statement = "INSERT IGNORE INTO user (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, USERNAME);
                preparedStatement.setString(2, PASSWORD);
                preparedStatement.setString(3, EMAIL);

                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    userID = resultSet.getInt(1);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("failed to add users to database: %s", ex.getMessage()));
        }
    }

    private void removeUsers() throws DataException {
        String[] statements = {
                "DELETE FROM user",
                "ALTER TABLE user AUTO_INCREMENT = 1"
        };

        try (var conn = DatabaseManager.getConnection()) {
            for (String statement: statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("failed to remove users from database: %s", ex.getMessage()));
        }
    }

    private void cleanUp(GameDAO gameDAO, Class<? extends GameDAO> dbClass) throws DataException {
        gameDAO.clear();

        if (dbClass == MySQLGameDAO.class) {
            removeUsers();
        }
    }

    @BeforeAll
    static void init() {
        String gameName = "game 1";
        String gameString = new ChessGame().getBoard().toJson();

        validGame = new GameData(1, null, null, gameName, gameString);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testCreateGameValid(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        assertDoesNotThrow(() -> gameDAO.createGame(validGame.gameName()));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testCreateGameNull(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        assertThrows(BadDataException.class, () -> gameDAO.createGame(null));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testGetGameValid(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);
        GameData gameData = gameDAO.createGame(validGame.gameName());

        assertEquals(validGame, gameDAO.getGame(gameData.gameID()));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testGetGameNonExistent(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> gameDAO.getGame(0));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testListGames(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        GameData gameData = gameDAO.createGame(validGame.gameName());
        Collection<GameData> gameList = new HashSet<>(Set.of(gameData));

        assertEquals(gameList, gameDAO.listGames());

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @MethodSource("dataaccess.GameDAOTest#getParameterizedStream")
    void testGetGameUserValid(Class<? extends GameDAO> dbClass, ChessGame.TeamColor teamColor) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        GameData gameData = gameDAO.createGame("test");

        assertNull(gameDAO.getGameUser(gameData.gameID(), teamColor));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @MethodSource("dataaccess.GameDAOTest#getParameterizedStream")
    void testGetGameUserNotFound(Class<? extends GameDAO> dbClass, ChessGame.TeamColor teamColor) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> gameDAO.getGameUser(0, teamColor));

        cleanUp(gameDAO, dbClass);
    }

    @Nested
    class TestUpdateUser {

        @ParameterizedTest
        @MethodSource("dataaccess.GameDAOTest#getParameterizedStream")
        void valid(Class<? extends GameDAO> dbClass, ChessGame.TeamColor teamColor) throws DataException {
            GameDAO gameDAO = getGameDAO(dbClass);

            GameData gameData = gameDAO.createGame(validGame.gameName());

            gameDAO.updateGameUser(gameData.gameID(), teamColor, userID);

            GameData game = gameDAO.getGame(gameData.gameID());

            switch (teamColor) {
                case WHITE -> assertEquals(userID, game.whiteUserID());
                case BLACK -> assertEquals(userID, game.blackUserID());
            }

            cleanUp(gameDAO, dbClass);
        }

        @ParameterizedTest
        @MethodSource("dataaccess.GameDAOTest#getParameterizedStream")
        void notFound(Class<? extends GameDAO> dbClass, ChessGame.TeamColor teamColor) throws DataException {
            GameDAO gameDAO = getGameDAO(dbClass);

            assertThrows(DataNotFoundException.class, () -> gameDAO.updateGameUser(0, teamColor, userID));

            cleanUp(gameDAO, dbClass);
        }
    }

    static Stream<Arguments> getParameterizedStream() {
        List<Class<? extends GameDAO>> classes = List.of(MemoryGameDAO.class, MySQLGameDAO.class);

        return Arrays.stream(ChessGame.TeamColor.values())
                .flatMap(teamColor ->
                    classes.stream().map(dbClass -> Arguments.of(dbClass, teamColor))
                );
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testUpdateGameStringValid(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        GameData gameData = gameDAO.createGame(validGame.gameName());

        String newGameString = "new game string";
        gameDAO.updateGameString(gameData.gameID(), newGameString);

        GameData game = gameDAO.getGame(gameData.gameID());
        assertEquals(newGameString, game.gameString());

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testUpdateGameStringNotFound(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> gameDAO.updateGameString(0, "test"));

        cleanUp(gameDAO, dbClass);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class, MySQLGameDAO.class})
    void testClear(Class<? extends GameDAO> dbClass) throws DataException {
        GameDAO gameDAO = getGameDAO(dbClass);

        gameDAO.createGame(validGame.gameName());

        assertDoesNotThrow(gameDAO::clear);

        cleanUp(gameDAO, dbClass);
    }
}
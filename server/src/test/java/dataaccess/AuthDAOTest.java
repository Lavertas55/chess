package dataaccess;

import dataaccess.exception.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {

    private static AuthData validAuth;

    private AuthDAO getAuthDAO(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO;
        if (dbClass.equals(MySQLAuthDAO.class)) {
            DatabaseManager.createDatabase();
            addUser();
            authDAO = new MySQLAuthDAO();
        }
        else {
            authDAO = new MemoryAuthDAO();
        }
        authDAO.clear();
        return authDAO;
    }

    private void addUser() throws DataException {
        String statement = "INSERT IGNORE INTO user (username, password, email) VALUES (\"bob\", \"1234\", \"bob@byu.edu\")";

        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("failed to add user to database: %s", ex.getMessage()));
        }
    }

    @BeforeAll
    static void init() {
        String authToken = "1234";
        int userID = 1;

        validAuth = new AuthData(userID, authToken);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertDoesNotThrow(() -> authDAO.createAuth(validAuth));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthNull(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(BadDataException.class, () -> authDAO.createAuth(null));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthExistingUser(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertDoesNotThrow(() -> authDAO.createAuth(validAuth));
        assertThrows(DataConflictException.class, () -> authDAO.createAuth(validAuth));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void getAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        authDAO.createAuth(validAuth);

        assertEquals(validAuth, authDAO.getAuth(validAuth.authToken()));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void getAuthNotFound(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth(null));
        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth("1111"));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void deleteAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        authDAO.createAuth(validAuth);

        assertDoesNotThrow(() -> authDAO.deleteAuth(validAuth.authToken()));
        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth(validAuth.authToken()));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void deleteAuthNotFound(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> authDAO.deleteAuth(null));
        assertThrows(DataNotFoundException.class, () -> authDAO.deleteAuth("1111"));

        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void clear(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        authDAO.createAuth(validAuth);

        assertDoesNotThrow(authDAO::clear);
        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth(validAuth.authToken()));
    }
}
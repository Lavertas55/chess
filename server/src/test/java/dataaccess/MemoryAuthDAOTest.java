package dataaccess;

import dataaccess.exception.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthDAOTest {

    private static AuthData validAuth;

    private AuthDAO getAuthDAO(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO;
        if (dbClass.equals(MySQLAuthDAO.class)) {
            authDAO = new MySQLAuthDAO();
        }
        else {
            authDAO = new MemoryAuthDAO();
        }
        authDAO.clear();
        return authDAO;
    }

    @BeforeAll
    static void init() {
        String authToken = "1234";
        String username = "test";

        validAuth = new AuthData(username, authToken);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertDoesNotThrow(() -> authDAO.createAuth(validAuth));

        assertThrows(DataConflictException.class, () -> authDAO.createAuth(validAuth));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthNull(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(BadDataException.class, () -> authDAO.createAuth(null));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void createAuthExistingUser(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertDoesNotThrow(() -> authDAO.createAuth(validAuth));
        assertThrows(DataConflictException.class, () -> authDAO.createAuth(validAuth));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void getAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        authDAO.createAuth(validAuth);

        assertEquals(validAuth, authDAO.getAuth(validAuth.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void getAuthNotFound(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth(null));
        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth("1111"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void deleteAuthValid(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        authDAO.createAuth(validAuth);

        assertDoesNotThrow(() -> authDAO.deleteAuth(validAuth.authToken()));
        assertThrows(DataNotFoundException.class, () -> authDAO.getAuth(validAuth.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class, MySQLAuthDAO.class})
    void deleteAuthNotFound(Class<? extends AuthDAO> dbClass) throws DataException {
        AuthDAO authDAO = getAuthDAO(dbClass);

        assertThrows(DataNotFoundException.class, () -> authDAO.deleteAuth(null));
        assertThrows(DataNotFoundException.class, () -> authDAO.deleteAuth("1111"));
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
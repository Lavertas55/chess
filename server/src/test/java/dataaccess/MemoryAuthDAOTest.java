package dataaccess;

import dataaccess.exception.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthDAOTest {

    private static MemoryAuthDAO memoryDAO;
    private static AuthData validAuth;

    @BeforeAll
    static void init() {
        memoryDAO = new MemoryAuthDAO();

        String authToken = "1234";
        String username = "test";

        validAuth = new AuthData(authToken, username);
    }

    @BeforeEach
    void setup() {
        memoryDAO.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        String newAuthToken = "1111";
        String username = "new auth";

        AuthData newAuth = new AuthData(newAuthToken, username);
        memoryDAO.createAuth(newAuth);

        assertThrows(DataAccessException.class, () -> memoryDAO.createAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.createAuth(newAuth));
    }

    @Test
    void getAuth() throws DataAccessException {
        memoryDAO.createAuth(validAuth);

        assertEquals(validAuth, memoryDAO.getAuth(validAuth.authToken()));

        assertThrows(DataAccessException.class, () -> memoryDAO.getAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.getAuth("1111"));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        memoryDAO.createAuth(validAuth);

        memoryDAO.deleteAuth(validAuth.authToken());

        assertThrows(DataAccessException.class, () -> memoryDAO.deleteAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.deleteAuth("1111"));
    }

    @Test
    void clear() throws DataAccessException {
        memoryDAO.createAuth(validAuth);

        memoryDAO.clear();

        assertThrows(DataAccessException.class, () -> memoryDAO.getAuth(validAuth.authToken()));
    }
}
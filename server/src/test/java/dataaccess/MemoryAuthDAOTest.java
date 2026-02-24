package dataaccess;

import dataaccess.exception.*;
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
    void createAuth() throws DataException {
        String newAuthToken = "1111";
        String username = "new auth";

        AuthData newAuth = new AuthData(newAuthToken, username);
        try {
            memoryDAO.createAuth(newAuth);
        } catch (dataaccess.exception.DataException e) {
            throw new RuntimeException(e);
        }

        assertThrows(BadDataException.class, () -> memoryDAO.createAuth(null));
        assertThrows(DataConflictException.class, () -> memoryDAO.createAuth(newAuth));
    }

    @Test
    void getAuth() throws DataException {
        try {
            memoryDAO.createAuth(validAuth);
        } catch (dataaccess.exception.DataException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals(validAuth, memoryDAO.getAuth(validAuth.authToken()));
        } catch (dataaccess.exception.DataException e) {
            throw new RuntimeException(e);
        }

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getAuth(null));
        assertThrows(DataNotFoundException.class, () -> memoryDAO.getAuth("1111"));
    }

    @Test
    void deleteAuth() throws DataException {
        try {
            memoryDAO.createAuth(validAuth);
        } catch (dataaccess.exception.DataException e) {
            throw new RuntimeException(e);
        }

        try {
            memoryDAO.deleteAuth(validAuth.authToken());
        } catch (dataaccess.exception.DataException e) {
            throw new RuntimeException(e);
        }

        assertThrows(DataNotFoundException.class, () -> memoryDAO.deleteAuth(null));
        assertThrows(DataNotFoundException.class, () -> memoryDAO.deleteAuth("1111"));
    }

    @Test
    void clear() throws DataException {
        memoryDAO.createAuth(validAuth);

        memoryDAO.clear();

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getAuth(validAuth.authToken()));
    }
}
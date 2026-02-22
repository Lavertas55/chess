package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthDAOTest {

    private static Map<String, AuthData> authStorage;
    private static MemoryAuthDAO memoryDAO;
    private static AuthData validAuth;

    @BeforeAll
    static void init() {
        authStorage = new HashMap<>();
        memoryDAO = new MemoryAuthDAO(authStorage);

        String authToken = "1234";
        String username = "test";

        validAuth = new AuthData(authToken, username);
    }

    @BeforeEach
    void setup() {
        authStorage.clear();
        authStorage.put(validAuth.authToken(), validAuth);
    }

    @Test
    void createAuth() throws DataAccessException {
        assertEquals(1, authStorage.size());

        String newAuthToken = "1111";
        String username = "new auth";

        AuthData newAuth = new AuthData(newAuthToken, username);
        memoryDAO.createAuth(newAuth);

        assertEquals(2, authStorage.size());
        assertEquals(newAuth, authStorage.get(newAuthToken));

        assertThrows(DataAccessException.class, () -> memoryDAO.createAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.createAuth(newAuth));
    }

    @Test
    void getAuth() throws DataAccessException {
        assertEquals(validAuth, memoryDAO.getAuth(validAuth.authToken()));

        assertThrows(DataAccessException.class, () -> memoryDAO.getAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.getAuth("1111"));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        assertEquals(1, authStorage.size());

        memoryDAO.deleteAuth(validAuth.authToken());

        assertEquals(0, authStorage.size());

        assertThrows(DataAccessException.class, () -> memoryDAO.deleteAuth(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.deleteAuth("1111"));
    }

    @Test
    void clear() {
        assertEquals(1, authStorage.size());

        memoryDAO.clear();

        assertEquals(0, authStorage.size());
    }
}
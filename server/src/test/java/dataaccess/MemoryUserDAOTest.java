package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDAOTest {

    private static Map<String, UserData> userStorage;
    private static MemoryUserDAO memoryDAO;
    private static UserData validUser;

    @BeforeAll
    static void init() {
        userStorage = new HashMap<>();
        memoryDAO = new MemoryUserDAO(userStorage);

        String username = "valid";
        String password = "password";
        String email = "test@yahoo.com";

        validUser = new UserData(username, password, email);
    }

    @BeforeEach
    void setup() {
        userStorage.clear();
        userStorage.put(validUser.username(), validUser);
    }

    @Test
    void createUser() throws DataAccessException {
        assertEquals(1, userStorage.size());

        String username = "new user";
        String password = "1234";
        String email = "random@gmail.com";

        UserData newUser = new UserData(username, password, email);

        memoryDAO.createUser(newUser);

        assertEquals(2, userStorage.size());
        assertEquals(newUser, userStorage.get(username));

        assertThrows(DataAccessException.class, () -> memoryDAO.createUser(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.createUser(newUser));
    }

    @Test
    void getUser() throws DataAccessException {
        assertEquals(validUser, memoryDAO.getUser(validUser.username()));

        assertThrows(DataAccessException.class, () -> memoryDAO.getUser(null));
        assertThrows(DataAccessException.class, () -> memoryDAO.getUser("non-existent"));
    }

    @Test
    void clear() {
        assertEquals(1, userStorage.size());

        memoryDAO.clear();

        assertEquals(0, userStorage.size());
    }
}
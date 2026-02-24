package dataaccess;

import dataaccess.exception.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDAOTest {

    private static MemoryUserDAO memoryDAO;
    private static UserData validUser;

    @BeforeAll
    static void init() {
        memoryDAO = new MemoryUserDAO();

        String username = "valid";
        String password = "password";
        String email = "test@yahoo.com";

        validUser = new UserData(username, password, email);
    }

    @BeforeEach
    void setup() {
        memoryDAO.clear();
    }

    @Test
    void createUser() throws DataException {
        memoryDAO.createUser(validUser);

        assertThrows(BadDataException.class, () -> memoryDAO.createUser(null));
        assertThrows(DataConflictException.class, () -> memoryDAO.createUser(validUser));
    }

    @Test
    void getUser() throws DataException {
        memoryDAO.createUser(validUser);

        assertEquals(validUser, memoryDAO.getUser(validUser.username()));

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser(null));
        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser("non-existent"));
    }

    @Test
    void clear() throws DataException {
        memoryDAO.createUser(validUser);

        memoryDAO.clear();

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser(validUser.username()));
    }
}
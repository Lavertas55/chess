package dataaccess;

import dataaccess.exception.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDAOTest {

    private static MemoryUserDAO memoryDAO;
    private static UserData validUser;
    private static RegisterRequest registerRequest;

    @BeforeAll
    static void init() {
        memoryDAO = new MemoryUserDAO();

        int userID = 1;
        String username = "valid";
        String password = "password";
        String email = "test@yahoo.com";

        validUser = new UserData(userID, username, password, email);
        registerRequest = new RegisterRequest(username, password, email);
    }

    @BeforeEach
    void setup() {
        memoryDAO.clear();
    }

    @Test
    void createUser() throws DataException {
        memoryDAO.createUser(registerRequest);

        assertThrows(BadDataException.class, () -> memoryDAO.createUser(null));
        assertThrows(DataConflictException.class, () -> memoryDAO.createUser(registerRequest));
    }

    @Test
    void getUser() throws DataException {
        memoryDAO.createUser(registerRequest);

        assertEquals(validUser, memoryDAO.getUser(validUser.username()));

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser(null));
        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser("non-existent"));
    }

    @Test
    void clear() throws DataException {
        memoryDAO.createUser(registerRequest);

        memoryDAO.clear();

        assertThrows(DataNotFoundException.class, () -> memoryDAO.getUser(validUser.username()));
    }
}
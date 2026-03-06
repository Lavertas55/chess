package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static UserData validUser;
    private static UserDAO userDAO;
    private static RegisterRequest registerRequest;

    @BeforeAll
    static void init() {
        int userID = 1;
        String username = "test";
        String password = "1234";
        String email = "test@yahoo.com";

        validUser = new UserData(userID, username, password, email);
        registerRequest = new RegisterRequest(username, password, email);
    }

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);
    }

    @Test
    void registerNewUser() throws ResponseException, DataException {
        int actual = userService.register(registerRequest);

        assertEquals(validUser.userID(), actual);
        assertEquals(validUser, userDAO.getUser(validUser.username()));
    }

    @Test
    void registerExistingUser() throws ResponseException {
        userService.register(registerRequest);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(registerRequest)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void verifyPasswordValid() throws ResponseException, DataException {
        userDAO.createUser(registerRequest);

        assertDoesNotThrow(() -> userService.verifyPassword(validUser.username(), validUser.password()));
    }

    @Test
    void verifyPasswordBadUsername() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.verifyPassword(validUser.username(), validUser.password())
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void verifyPasswordBadPassword() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.verifyPassword(validUser.username(), "bad password")
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }
}
package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static UserData validUser;
    private static UserDAO userDAO;

    @BeforeAll
    static void init() {
        String username = "test";
        String password = "1234";
        String email = "test@yahoo.com";

        validUser = new UserData(username, password, email);
    }

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);
    }

    @Test
    void registerNewUser() throws ResponseException, DataException {
        String actual = userService.register(validUser);

        assertEquals(validUser.username(), actual);
        assertEquals(validUser, userDAO.getUser(validUser.username()));
    }

    @Test
    void registerExistingUser() throws ResponseException {
        userService.register(validUser);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(validUser)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void loginValid() throws ResponseException, DataException {
        userDAO.createUser(validUser);

        String actual = userService.login(validUser.username(), validUser.password());

        assertEquals(validUser.username(), actual);
    }

    @Test
    void loginBadUsername() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(validUser.username(), validUser.password())
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void loginBadPassword() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(validUser.username(), "bad password")
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }
}
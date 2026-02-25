package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static RegisterRequest validRegister;
    private static UserData validUser;
    private static UserDAO userDAO;

    @BeforeAll
    static void init() {
        String username = "test";
        String password = "1234";
        String email = "test@yahoo.com";

        validRegister = new RegisterRequest(username, password, email);
        validUser = new UserData(username, password, email);
    }

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);
    }

    @Test
    void registerNewUser() throws ResponseException, DataException {
        String actual = userService.register(validRegister);

        assertEquals(validRegister.username(), actual);
        assertEquals(validUser, userDAO.getUser(validRegister.username()));
    }

    @Test
    void registerExistingUser() throws ResponseException {
        userService.register(validRegister);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(validRegister)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void loginValid() throws ResponseException, DataException {
        userDAO.createUser(validUser);

        LoginRequest loginRequest = new LoginRequest(
                validRegister.username(),
                validRegister.password()
        );

        String actual = userService.login(loginRequest);

        assertEquals(validRegister.username(), actual);
    }

    @Test
    void loginBadUsername() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest("bad user", "1234"))
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void loginBadPassword() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest(validRegister.username(), "bad password"))
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void logout() {
    }
}
package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static RegisterRequest validRegister;
    private static UserData validUser;
    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;

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
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        userService = new UserService(authDAO, userDAO);
    }

    @Test
    void register() throws ResponseException, DataException {
        RegisterResponse actual = userService.register(validRegister);

        assertEquals(validRegister.username(), actual.username());
        assertEquals(validUser, userDAO.getUser(validRegister.username()));

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(validRegister)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void login() throws ResponseException, DataException {
        userService.register(validRegister);

        LoginRequest loginRequest = new LoginRequest(
                validRegister.username(),
                validRegister.password()
        );

        LoginResponse actual = userService.login(loginRequest);

        assertEquals(validRegister.username(), actual.username());
        assertEquals(validRegister.username(), authDAO.getAuth(actual.authToken()).username());

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest("bad user", "1234"))
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());

        exception = assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest(validRegister.username(), "bad password"))
        );
        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void logout() {
    }
}
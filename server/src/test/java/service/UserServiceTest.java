package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
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

    @BeforeAll
    static void init() {
        String username = "test";
        String password = "1234";
        String email = "test@yahoo.com";

        validRegister = new RegisterRequest(username, password, email);
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(new MemoryAuthDAO(), new MemoryUserDAO());
    }

    @Test
    void register() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(
                validRegister.username(),
                validRegister.password(),
                validRegister.email()
        );

        RegisterResponse actual = userService.register(registerRequest);

        assertEquals(validRegister.username(), actual.username());

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(registerRequest)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void login() throws ResponseException {
        userService.register(validRegister);

        LoginRequest loginRequest = new LoginRequest(
                validRegister.username(),
                validRegister.password()
        );

        LoginResponse actual = userService.login(loginRequest);

        assertEquals(validRegister.username(), actual.username());

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest("bad user", "1234"))
        );
        assertEquals(ResponseException.Code.NOT_FOUND, exception.getCode());

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
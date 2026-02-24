package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import response.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static UserData validUser;

    @BeforeAll
    static void init() {
        String username = "test";
        String password = "1234";
        String email = "test@yahoo.com";

        validUser = new UserData(username, password, email);
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(new MemoryAuthDAO(), new MemoryUserDAO());
    }

    @Test
    void register() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(
                validUser.username(),
                validUser.password(),
                validUser.email()
        );

        RegisterResponse actual = userService.register(registerRequest);

        assertEquals(validUser.username(), actual.username());

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> userService.register(registerRequest)
        );

        assertEquals(ResponseException.Code.FORBIDDEN, exception.getCode());
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}
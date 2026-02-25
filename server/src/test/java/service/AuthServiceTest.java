package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static AuthService authService;
    private static AuthDAO authDAO;
    private static AuthData validAuth;

    @BeforeAll
    static void init() {
        String username = "test";
        String authToken = "1111";

        validAuth = new AuthData(username, authToken);
    }

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }

    @Test
    void isValidToken() throws DataException {
        authDAO.createAuth(validAuth);

        assert(authService.isValidToken(validAuth.authToken()));
        assert(!authService.isValidToken("1234"));
    }

    @Test
    void generateSession() throws DataException {
        String authToken = authService.generateSession(validAuth.username());

        AuthData testAuth = new AuthData(validAuth.username(), authToken);

        assertEquals(testAuth, authDAO.getAuth(authToken));
    }

    @Test
    void getSession() throws ResponseException, DataException {
        authDAO.createAuth(validAuth);

        assertEquals(validAuth, authService.getSession(validAuth.authToken()));

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.getSession("1234")
        );

        assertEquals(ResponseException.Code.NOT_FOUND, exception.getCode());
    }
}
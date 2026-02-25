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
    void validateValidToken() throws ResponseException, DataException {
        authDAO.createAuth(validAuth);

        assert(authService.isValidToken(validAuth.authToken()));
    }

    @Test
    void validateInvalidToken() throws ResponseException, DataException {
        authDAO.createAuth(validAuth);

        assert(!authService.isValidToken("1234"));
    }

    @Test
    void generateSession() throws DataException, ResponseException {
        String authToken = authService.generateSession(validAuth.username());

        AuthData testAuth = new AuthData(validAuth.username(), authToken);

        assertEquals(testAuth, authDAO.getAuth(authToken));
    }

    @Test
    void generateSessionBadInput() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.generateSession(null)
        );

        assertEquals(ResponseException.Code.BAD_REQUEST, exception.getCode());
    }
}
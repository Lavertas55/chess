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

        authService.verifySession(validAuth.authToken());
    }

    @Test
    void validateInvalidToken() throws ResponseException, DataException {
        authDAO.createAuth(validAuth);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.verifySession("1234")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void generateSession() throws DataException, ResponseException {
        AuthData authData = authService.generateSession(validAuth.username());

        assertEquals(authData, authDAO.getAuth(authData.authToken()));
    }

    @Test
    void generateSessionBadInput() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.generateSession(null)
        );

        assertEquals(ResponseException.Code.BAD_REQUEST, exception.getCode());
    }

    @Test
    void closeSessionValid() throws ResponseException, DataException {
        authDAO.createAuth(validAuth);

        authService.closeSession(validAuth.authToken());
    }

    @Test
    void closeSessionInvalid() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.closeSession("1234")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }
}
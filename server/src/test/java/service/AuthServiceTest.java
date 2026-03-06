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
        int userID = 1;
        String authToken = "1111";

        validAuth = new AuthData(userID, authToken);
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
    void validateInvalidToken() throws DataException {
        authDAO.createAuth(validAuth);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> authService.verifySession("1234")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void generateSession() throws DataException, ResponseException {
        AuthData authData = authService.generateSession(validAuth.userID());

        assertEquals(authData, authDAO.getAuth(authData.authToken()));
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
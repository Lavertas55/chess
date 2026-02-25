package service;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.AuthData;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public boolean isValidToken(String authToken) {
        throw new RuntimeException("not implemented");
    }

    public String generateSession(String username) {
        throw new RuntimeException("not implemented");
    }

    public AuthData getSession(String authToken) throws ResponseException {
        throw new RuntimeException("not implemented");
    }
}

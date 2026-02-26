package service;

import dataaccess.AuthDAO;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import exception.ResponseException;
import model.AuthData;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public boolean isValidToken(String authToken) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
        }
        catch (DataNotFoundException e) {
            return false;
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return true;
    }

    public String generateSession(String username) throws ResponseException {
        if (username == null) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "bad request");
        }

        String authToken = generateToken();

        AuthData authData = new AuthData(username, authToken);

        try {
            authDAO.createAuth(authData);
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return authToken;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void closeSession(String authToken) throws ResponseException {
        try {
            authDAO.deleteAuth(authToken);
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "unauthorized");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }
}

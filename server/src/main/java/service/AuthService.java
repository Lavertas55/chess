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

    public void verifySession(String authToken) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Unauthorized");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public AuthData generateSession(int userID) throws ResponseException {
        String authToken = generateToken();

        AuthData authData = new AuthData(userID, authToken);

        try {
            authDAO.createAuth(authData);
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return new AuthData(userID, authToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void closeSession(String authToken) throws ResponseException {
        try {
            authDAO.deleteAuth(authToken);
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Unauthorized");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public int getUserID(String authToken) throws ResponseException {
        try {
            return authDAO.getAuth(authToken).userID();
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public void clearAuth() throws ResponseException {
        try {
            authDAO.clear();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }
}

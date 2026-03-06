package service;

import dataaccess.*;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataConflictException;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import exception.ResponseException;
import model.UserData;
import request.RegisterRequest;

import java.util.Objects;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public int register(RegisterRequest registerRequest) throws ResponseException {
        try {
            return userDAO.createUser(registerRequest).userID();
        }
        catch (BadDataException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }
        catch (DataConflictException e) {
            throw new ResponseException(ResponseException.Code.FORBIDDEN, "Forbidden");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public void verifyPassword(String username, String password) throws ResponseException {
        if (username == null || password == null) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }

        try {
            UserData userData = userDAO.getUser(username);

            if (!Objects.equals(userData.password(), password)) {
                throw new DataNotFoundException("password mismatch");
            }
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Unauthorized");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public String getUsername(int userID) throws ResponseException {
        try {
            return userDAO.getUser(userID).username();
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "userID does not exist");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public int getUserID(String username) throws ResponseException {
        try {
            return userDAO.getUser(username).userID();
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "username does not exist");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public void clearUsers() throws ResponseException {
        try {
            userDAO.clear();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }
}

package service;

import dataaccess.*;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataConflictException;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import exception.ResponseException;
import model.UserData;

import java.util.Objects;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String register(UserData userData) throws ResponseException {
        try {
            userDAO.createUser(userData);
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

        return userData.username();
    }

    public String login(String username, String password) throws ResponseException {
        if (username == null || password == null) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }

        UserData userData;

        try {
            userData = userDAO.getUser(username);

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

        return username;
    }

    public void clearUsers() {
        userDAO.clear();
    }
}

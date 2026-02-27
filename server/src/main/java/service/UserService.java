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
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, e.getMessage());
        }
        catch (DataConflictException e) {
            throw new ResponseException(ResponseException.Code.FORBIDDEN, e.getMessage());
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return userData.username();
    }

    public String login(LoginRequest loginRequest) throws ResponseException {
        String username = loginRequest.username();

        try {
            UserData userData = userDAO.getUser(username);

            if (!Objects.equals(userData.password(), loginRequest.password())) {
                throw new DataNotFoundException("Invalid Password");
            }
        }
        catch (DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.UNAUTHORIZED, "Invalid Credentials");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return username;
    }
}

package service;

import dataaccess.*;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataConflictException;
import dataaccess.exception.DataException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import java.util.UUID;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        UserData userData = new UserData(
                registerRequest.username(),
                registerRequest.password(),
                registerRequest.email()
        );

        // Add the user
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

        AuthData authData = new AuthData(
                userData.username(),
                generateToken()
        );

        // Create a session
        try {
            authDAO.createAuth(authData);
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return new RegisterResponse(authData.username(), authData.authToken());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        throw new RuntimeException("not implemented");
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void logout(String authToken) {
        throw new RuntimeException("not implemented");
    }
}

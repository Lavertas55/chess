package dataaccess;

import dataaccess.exception.DataAccessException;
import model.AuthData;

public interface AuthDAO extends DataAccessObject {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}

package dataaccess;

import dataaccess.exception.DataException;
import model.AuthData;

public interface AuthDAO extends DataAccessObject {
    void createAuth(AuthData authData) throws DataException;
    AuthData getAuth(String authToken) throws DataException;
    void deleteAuth(String authToken) throws DataException;
}

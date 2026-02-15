package dataaccess;

import model.AuthData;

public interface AuthDAO extends DataAccessObject {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken);
}

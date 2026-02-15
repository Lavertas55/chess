package dataaccess;

import model.UserData;

public interface UserDAO extends DataAccessObject {
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}

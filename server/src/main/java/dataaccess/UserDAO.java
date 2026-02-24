package dataaccess;

import dataaccess.exception.DataException;
import model.UserData;

public interface UserDAO extends DataAccessObject {
    void createUser(UserData userData) throws DataException;
    UserData getUser(String username) throws DataException;
}

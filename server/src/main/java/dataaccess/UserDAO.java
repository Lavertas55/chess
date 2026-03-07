package dataaccess;

import dataaccess.exception.DataException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;

public interface UserDAO extends DataAccessObject {
    UserData createUser(RegisterRequest registerRequest) throws DataException;
    UserData getUser(int userID) throws DataException;
    UserData getUser(String username) throws DataException;

    default String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

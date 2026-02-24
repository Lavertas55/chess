package dataaccess;

import dataaccess.exception.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new DataAccessException("userData cannot be null.");
        }

        String username = userData.username();

        if (hasUsername(username)) {
            throw new DataAccessException(String.format("username %s is already in use.", username));
        }

        userStorage.put(username, userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!hasUsername(username)) {
            throw new DataAccessException(String.format("username %s is not in use.", username));
        }

        return userStorage.get(username);
    }

    private boolean hasUsername(String username) {
        return userStorage.containsKey(username);
    }

    @Override
    public void clear() {
        userStorage.clear();
    }
}

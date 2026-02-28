package dataaccess;

import dataaccess.exception.*;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataException {
        if (userData == null || userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadDataException("userData cannot be null.");
        }

        String username = userData.username();

        if (hasUsername(username)) {
            throw new DataConflictException(String.format("username %s is already in use.", username));
        }

        userStorage.put(username, userData);
    }

    @Override
    public UserData getUser(String username) throws DataException {
        if (!hasUsername(username)) {
            throw new DataNotFoundException(String.format("username %s is not in use.", username));
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

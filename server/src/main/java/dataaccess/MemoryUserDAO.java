package dataaccess;

import dataaccess.exception.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import request.RegisterRequest;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<Integer, UserData> userStorage = new HashMap<>();
    private final HashMap<String, Integer> usernameToID = new HashMap<>();
    private int nextUserID = 1;

    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataException {
        if (registerRequest == null ||
                registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null
        ) {
            throw new BadDataException("userData cannot be null.");
        }

        String username = registerRequest.username();

        if (hasUsername(username)) {
            throw new DataConflictException(String.format("username %s is already in use.", username));
        }

        String password = registerRequest.password();
        String email = registerRequest.email();

        UserData userData = new UserData(nextUserID++, username, password, email);

        userStorage.put(userData.userID(), userData);
        usernameToID.put(userData.username(), userData.userID());

        return userData;
    }

    @Override
    public UserData getUser(String username) throws DataException {
        if (!hasUsername(username)) {
            throw new DataNotFoundException(String.format("username %s is not in use.", username));
        }

        int userID = usernameToID.get(username);
        return getUser(userID);
    }

    @Override
    public UserData getUser(int userID) throws DataException {
        if (userStorage.containsKey(userID)) {
            return userStorage.get(userID);
        }
        else {
            throw new DataNotFoundException(String.format("userID %d does not exist", userID));
        }
    }

    private boolean hasUsername(String username) {
        return usernameToID.containsKey(username);
    }

    @Override
    public void clear() {
        userStorage.clear();
        usernameToID.clear();
    }
}

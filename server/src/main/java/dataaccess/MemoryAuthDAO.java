package dataaccess;

import dataaccess.exception.*;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    private final HashMap<String, AuthData> authStorage = new HashMap<>();

    private boolean hasToken(String authToken) {
        return authStorage.containsKey(authToken);
    }

    @Override
    public void createAuth(AuthData authData) throws DataException {
        if (authData == null) {
            throw new BadDataException("authData cannot be null");
        }

        String authToken = authData.authToken();

        if (hasToken(authToken)) {
            throw new DataConflictException(String.format("Auth Token %s is in use.", authToken));
        }

        authStorage.put(authToken, authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataException {
        if (!hasToken(authToken)) {
            throw new DataNotFoundException(String.format("Auth Token %s does not exist.", authToken));
        }

        return authStorage.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataException {
        if (!hasToken(authToken)) {
            throw new DataNotFoundException(String.format("Auth Token %s does not exist.", authToken));
        }

        authStorage.remove(authToken);
    }

    @Override
    public void clear() {
        authStorage.clear();
    }
}

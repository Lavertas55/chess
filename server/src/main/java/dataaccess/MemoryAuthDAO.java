package dataaccess;

import model.AuthData;

import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> authStorage;

    public MemoryAuthDAO(Map<String, AuthData> authStorage) {
        this.authStorage = authStorage;
    }

    private boolean hasToken(String authToken) {
        return authStorage.containsKey(authToken);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("authData cannot be null");
        }

        String authToken = authData.authToken();

        if (hasToken(authToken)) {
            throw new DataAccessException("Auth Token is in use.");
        }

        authStorage.put(authToken, authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!hasToken(authToken)) {
            throw new DataAccessException("Auth Token does not exist.");
        }

        return authStorage.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!hasToken(authToken)) {
            throw new DataAccessException("Auth Token does not exist.");
        }

        authStorage.remove(authToken);
    }

    @Override
    public void clear() {
        authStorage.clear();
    }
}

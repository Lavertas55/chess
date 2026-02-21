package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccessObject {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGameWhiteUser(int gameID, String whiteUsername) throws DataAccessException;
    void updateGameBlackUser(int gameID, String blackUsername) throws DataAccessException;
    void updateGameString(int gameID, String gameString) throws DataAccessException;
}

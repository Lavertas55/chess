package dataaccess;

import dataaccess.exception.DataException;
import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccessObject {
    void createGame(GameData gameData) throws DataException;
    GameData getGame(int gameID) throws DataException;
    Collection<GameData> listGames();
    void updateGameWhiteUser(int gameID, String whiteUsername) throws DataException;
    void updateGameBlackUser(int gameID, String blackUsername) throws DataException;
    void updateGameString(int gameID, String gameString) throws DataException;
}

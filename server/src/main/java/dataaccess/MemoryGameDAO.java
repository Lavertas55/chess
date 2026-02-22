package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> gameStorage;

    public MemoryGameDAO(Map<Integer, GameData> gameStorage) {
        this.gameStorage = gameStorage;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        if (gameData == null) {
            throw new DataAccessException("gameData cannot be null.");
        }

        int gameID = gameData.gameID();
        if (hasGameID(gameID)) {
            throw new DataAccessException(String.format("gameID = %d already in use.", gameID));
        }

        gameStorage.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!hasGameID(gameID)) {
            throw new DataAccessException(String.format("gameID = %d not in use.", gameID));
        }

        return gameStorage.get(gameID);
    }

    private boolean hasGameID(int gameID) {
        return gameStorage.containsKey(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return new HashSet<>(gameStorage.values());
    }

    @Override
    public void updateGameWhiteUser(int gameID, String whiteUsername) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = new GameData(
                game.gameID(),
                whiteUsername,
                game.blackUsername(),
                game.gameName(),
                game.gameString()
        );

        gameStorage.replace(gameID, updatedGame);
    }

    @Override
    public void updateGameBlackUser(int gameID, String blackUsername) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                blackUsername,
                game.gameName(),
                game.gameString()
        );

        gameStorage.replace(gameID, updatedGame);
    }

    @Override
    public void updateGameString(int gameID, String gameString) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                gameString
        );

        gameStorage.replace(gameID, updatedGame);
    }

    @Override
    public void clear() {
        gameStorage.clear();
    }
}

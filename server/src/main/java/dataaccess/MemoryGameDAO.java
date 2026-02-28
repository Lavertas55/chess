package dataaccess;

import dataaccess.exception.*;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> gameStorage = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public GameData createGame(String gameName) throws DataException {
        if (gameName == null) {
            throw new BadDataException("gameName cannot be null.");
        }

        GameData gameData = new GameData(
                nextGameID++,
                null,
                null,
                gameName,
                new ChessGame().getBoard().toJson()
        );

        gameStorage.put(gameData.gameID(), gameData);

        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws DataException {
        if (!hasGameID(gameID)) {
            throw new DataNotFoundException(String.format("gameID = %d not in use.", gameID));
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
    public void updateGameWhiteUser(int gameID, String whiteUsername) throws DataException {
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
    public void updateGameBlackUser(int gameID, String blackUsername) throws DataException {
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
    public void updateGameString(int gameID, String gameString) throws DataException {
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

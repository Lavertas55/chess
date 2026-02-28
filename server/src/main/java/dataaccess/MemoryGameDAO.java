package dataaccess;

import chess.ChessGame;
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
    public void updateGameUser(int gameID, ChessGame.TeamColor teamColor, String username) throws DataException {
        GameData game = getGame(gameID);

        GameData updatedGame;

        switch (teamColor) {
            case WHITE -> updatedGame = new GameData(
                                game.gameID(),
                                username,
                                game.blackUsername(),
                                game.gameName(),
                                game.gameString()
                        );
            case BLACK -> updatedGame = new GameData(
                                game.gameID(),
                                game.whiteUsername(),
                                username,
                                game.gameName(),
                                game.gameString()
                        );
            default -> throw new BadDataException("color must be WHITE or BLACK");
        };
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

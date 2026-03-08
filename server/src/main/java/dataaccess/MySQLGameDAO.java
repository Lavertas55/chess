package dataaccess;

import chess.ChessGame;
import dataaccess.exception.DataException;
import model.GameData;

import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    @Override
    public GameData createGame(String gameName) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void updateGameUser(int gameID, ChessGame.TeamColor teamColor, Integer userID) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Integer getGameUser(int gameID, ChessGame.TeamColor teamColor) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void updateGameString(int gameID, String gameString) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void clear() throws DataException {
        throw new RuntimeException("not implemented");
    }
}
